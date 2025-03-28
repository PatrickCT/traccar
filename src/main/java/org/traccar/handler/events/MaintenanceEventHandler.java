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
package org.traccar.handler.events;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelHandler;
import org.traccar.helper.Parser;
import org.traccar.model.Event;
import org.traccar.model.Maintenance;
import org.traccar.model.Position;
import org.traccar.session.cache.CacheManager;
import org.traccar.storage.StorageException;
import org.traccar.storage.query.Columns;
import org.traccar.storage.query.Condition;
import org.traccar.storage.query.Request;
import org.traccar.utils.GeneralUtils;
import org.traccar.utils.GenericUtils;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@ChannelHandler.Sharable
public class MaintenanceEventHandler extends BaseEventHandler {

    private final CacheManager cacheManager;

    @Inject
    public MaintenanceEventHandler(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    protected Map<Event, Position> analyzePosition(Position position) {
        Position lastPosition = cacheManager.getPosition(position.getDeviceId());
        if (lastPosition == null || position.getFixTime().compareTo(lastPosition.getFixTime()) < 0) {
            return null;
        }

        Map<Event, Position> events = new HashMap<>();
        for (Maintenance maintenance : cacheManager.getDeviceObjects(position.getDeviceId(), Maintenance.class)) {
            if (maintenance.getPeriod() != 0) {
                if (maintenance.getType().equals("date")) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.forLanguageTag("es-MX"));
                    LocalDate localDate = LocalDate.parse(maintenance.getAttributes().getOrDefault("last", sdf.format(new Date())).toString(), formatter);

                    int each = (int) maintenance.getPeriod();
                    each *= 30;
                    Map<TimeUnit, Long> diff = GenericUtils.computeDiff(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()), new Date());

                    var diff_days = diff.get(TimeUnit.DAYS);//if negative, localdate is in the future

                    if (diff_days >= each) {
                        maintenance.getAttributes().put("last", sdf.format(new Date()));
                        try {
                            cacheManager.getStorage().updateObject(maintenance, new Request(
                                    new Columns.Exclude("id"),
                                    new Condition.Equals("id", maintenance.getId())));
                        } catch (StorageException e) {

                        }
                        Event event = new Event(Event.TYPE_MAINTENANCE, position);
                        event.setMaintenanceId(maintenance.getId());
                        event.set(maintenance.getType(), maintenance.getType());
                        events.put(event, position);
                    }

                } else if (maintenance.getType().equals("hours")) {
                    double oldValue = lastPosition.getDouble(maintenance.getType());
                    double newValue = position.getDouble(maintenance.getType());

                    oldValue = oldValue / 3600000;
                    newValue = newValue / 3600000;

                    if (oldValue != 0.0 && newValue != 0.0 && newValue >= maintenance.getStart()) {
                        if (oldValue < maintenance.getStart()
                                || (long) ((oldValue - maintenance.getStart()) / maintenance.getPeriod())
                                < (long) ((newValue - maintenance.getStart()) / maintenance.getPeriod())) {
                            Event event = new Event(Event.TYPE_MAINTENANCE, position);
                            event.setMaintenanceId(maintenance.getId());
                            event.set(maintenance.getType(), newValue);
                            events.put(event, position);
                        }
                    }
                } else {
                    double oldValue = lastPosition.getDouble(maintenance.getType());
                    double newValue = position.getDouble(maintenance.getType());

                    oldValue = oldValue * 0.001;
                    newValue = newValue * 0.001;

                    if (oldValue != 0.0 && newValue != 0.0 && newValue >= (maintenance.getStart() / 100)) {
                        if (oldValue < maintenance.getStart()
                                || (long) ((oldValue - (maintenance.getStart() / 100)) / maintenance.getPeriod())
                                < (long) ((newValue - (maintenance.getStart() / 100)) / maintenance.getPeriod())) {
                            Event event = new Event(Event.TYPE_MAINTENANCE, position);
                            event.setMaintenanceId(maintenance.getId());
                            event.set(maintenance.getType(), newValue);
                            events.put(event, position);
                        }
                    }
                }
            }
        }

        return events;
    }

}
