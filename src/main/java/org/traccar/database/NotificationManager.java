/*
 * Copyright 2016 - 2022 Anton Tananaev (anton@traccar.org)
 * Copyright 2016 - 2018 Andrey Kunitsyn (andrey@traccar.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.traccar.config.Config;
import org.traccar.config.Keys;
import org.traccar.forward.EventData;
import org.traccar.forward.EventForwarder;
import org.traccar.geocoder.Geocoder;
import org.traccar.model.Calendar;
import org.traccar.model.Device;
import org.traccar.model.Event;
import org.traccar.model.Geofence;
import org.traccar.model.Maintenance;
import org.traccar.model.Notification;
import org.traccar.model.Position;
import org.traccar.notification.MessageException;
import org.traccar.notification.NotificatorManager;
import org.traccar.session.cache.CacheManager;
import org.traccar.storage.Storage;
import org.traccar.storage.StorageException;
import org.traccar.storage.query.Columns;
import org.traccar.storage.query.Request;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.traccar.model.User;
import org.traccar.storage.query.Condition;
import org.traccar.utils.GenericUtils;

@Singleton
public class NotificationManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationManager.class);
    private final Map<Long, Map<Long, Map<String, Date>>> alerted = new HashMap<>();

    private final Storage storage;
    private final CacheManager cacheManager;
    private final EventForwarder eventForwarder;
    private final NotificatorManager notificatorManager;
    private final Geocoder geocoder;

    private final boolean geocodeOnRequest;

    @Inject
    public NotificationManager(
            Config config, Storage storage, CacheManager cacheManager, @Nullable EventForwarder eventForwarder,
            NotificatorManager notificatorManager, @Nullable Geocoder geocoder) {
        this.storage = storage;
        this.cacheManager = cacheManager;
        this.eventForwarder = eventForwarder;
        this.notificatorManager = notificatorManager;
        this.geocoder = geocoder;
        geocodeOnRequest = config.getBoolean(Keys.GEOCODER_ON_REQUEST);
    }

    private void updateEvent(Event event, Position position) {
        try {
            event.setId(storage.addObject(event, new Request(new Columns.Exclude("id"))));
        } catch (StorageException error) {
            LOGGER.warn("Event save error", error);
        }

        var notifications = cacheManager.getDeviceObjects(event.getDeviceId(), Notification.class).stream()
                .filter(notification -> notification.getType().equals(event.getType()))
                .filter(notification -> {
                    if (event.getType().equals(Event.TYPE_ALARM)) {
                        String alarmsAttribute = notification.getString("alarms");
                        if (alarmsAttribute != null) {
                            return Arrays.asList(alarmsAttribute.split(","))
                                    .contains(event.getString(Position.KEY_ALARM));
                        }
                        return false;
                    }
                    return true;
                })
                .filter(notification -> {
                    long calendarId = notification.getCalendarId();
                    Calendar calendar = calendarId != 0 ? cacheManager.getObject(Calendar.class, calendarId) : null;
                    return calendar == null || calendar.checkMoment(event.getEventTime());
                })
                .collect(Collectors.toUnmodifiableList());

        if (!notifications.isEmpty()) {
            if (position != null && position.getAddress() == null && geocodeOnRequest && geocoder != null) {
                position.setAddress(geocoder.getAddress(position.getLatitude(), position.getLongitude(), null));
            }

            notifications.forEach(notification -> {
                cacheManager.getNotificationUsers(notification.getId(), event.getDeviceId()).forEach(user -> {
                    for (String notificator : notification.getNotificatorsTypes()) {
                        try {

                            int tiempoEspera = 0;
                            switch (event.getType()) {
                                case Event.TYPE_DEVICE_ONLINE:
                                    tiempoEspera = user.getOfflineTimeout();
                                    break;
                                case Event.TYPE_DEVICE_STOPPED:
                                    tiempoEspera = user.getStopTimeout();
                                    break;
                                default:
                                    tiempoEspera = 0;
                            }
                            if(position != null){
                                Date check = alerted.get(position.getDeviceId()).get(user.getId()).putIfAbsent(notificator, new Date());
                            if (check == null) {
                                notificatorManager.getNotificator(notificator).send(notification, user, event, position, storage);
                            } else {
                                if (GenericUtils.checkElapsedTime(new Date(),
                                        alerted.get(position.getDeviceId()).get(user.getId()).get(notificator),
                                        tiempoEspera) || check == null) {

                                    alerted.get(position.getDeviceId()).get(user.getId()).put(notificator, new Date());
                                    notificatorManager.getNotificator(notificator).send(notification, user, event, position, storage);
                                }

                            }
                            } else {
                                notificatorManager.getNotificator(notificator).send(notification, user, event, position, storage);
                            }

                        } catch (MessageException exception) {
                            LOGGER.warn("Notification failed", exception);
                        }
                    }
                });
            });
        }

        forwardEvent(event, position);
    }

    private void forwardEvent(Event event, Position position) {
        if (eventForwarder != null) {
            EventData eventData = new EventData();
            eventData.setEvent(event);
            eventData.setPosition(position);
            eventData.setDevice(cacheManager.getObject(Device.class, event.getDeviceId()));
            if (event.getGeofenceId() != 0) {
                eventData.setGeofence(cacheManager.getObject(Geofence.class, event.getGeofenceId()));
            }
            if (event.getMaintenanceId() != 0) {
                eventData.setMaintenance(cacheManager.getObject(Maintenance.class, event.getMaintenanceId()));
            }
            eventForwarder.forward(eventData, (success, throwable) -> {
                if (!success) {
                    LOGGER.warn("Event forwarding failed", throwable);
                }
            });
        }
    }

    public void updateEvents(Map<Event, Position> events) {
        for (Entry<Event, Position> entry : events.entrySet()) {
            Event event = entry.getKey();
            Position position = entry.getValue();
            try {
                cacheManager.addDevice(event.getDeviceId());
                updateEvent(event, position);
            } catch (StorageException e) {
                throw new RuntimeException(e);
            } finally {
                cacheManager.removeDevice(event.getDeviceId());
            }
        }
    }
}
