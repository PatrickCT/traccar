/*
 * Copyright 2016 - 2023 Anton Tananaev (anton@traccar.org)
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

import io.netty.channel.ChannelHandler;
import java.io.IOException;
import org.traccar.helper.model.PositionUtil;
import org.traccar.model.Calendar;
import org.traccar.model.Event;
import org.traccar.model.Geofence;
import org.traccar.model.Position;
import org.traccar.session.cache.CacheManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.traccar.helper.LogAction;
import org.traccar.model.Device;
import org.traccar.session.ConnectionManager;
import org.traccar.storage.Storage;
import org.traccar.storage.StorageException;
import org.traccar.storage.query.Columns;
import org.traccar.storage.query.Condition;
import org.traccar.storage.query.Request;
import org.traccar.utils.GenericUtils;
import org.traccar.utils.TransporteUtils;

@Singleton
@ChannelHandler.Sharable
public class GeofenceEventHandler extends BaseEventHandler {

    private final CacheManager cacheManager;

    @Inject
    public GeofenceEventHandler(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    protected Map<Event, Position> analyzePosition(Position position) {
        if (!PositionUtil.isLatest(cacheManager, position)) {            
            return null;
        }

        List<Long> oldGeofences = new ArrayList<>();
        Position lastPosition = cacheManager.getPosition(position.getDeviceId());
        if (lastPosition != null && lastPosition.getGeofenceIds() != null) {
            oldGeofences.addAll(lastPosition.getGeofenceIds());
        }

        List<Long> newGeofences = new ArrayList<>();
        if (position.getGeofenceIds() != null) {
            newGeofences.addAll(position.getGeofenceIds());
            newGeofences.removeAll(oldGeofences);
            oldGeofences.removeAll(position.getGeofenceIds());
        }

        Map<Event, Position> events = new HashMap<>();
        for (long geofenceId : oldGeofences) {
            Geofence geofence = cacheManager.getObject(Geofence.class, geofenceId);
            if (geofence != null) {
                long calendarId = geofence.getCalendarId();
                Calendar calendar = calendarId != 0 ? cacheManager.getObject(Calendar.class, calendarId) : null;
                if (calendar == null || calendar.checkMoment(position.getFixTime())) {
                    Event event = new Event(Event.TYPE_GEOFENCE_EXIT, position);
                    event.setGeofenceId(geofenceId);
                    events.put(event, position);

                    CompletableFuture<Void> asyncTask = CompletableFuture.supplyAsync(() -> {
                        try {
                            if (!TransporteUtils.hasSalida(position.getDeviceId(), cacheManager)) {
                                TransporteUtils.generarSalida(position.getDeviceId(), geofenceId, position.getFixTime(), cacheManager);
                            } else {
                                TransporteUtils.updateSalida(position.getDeviceId(), geofenceId, position.getFixTime(), cacheManager);
                            }
                        } catch (Exception e) {
                            // Handle exceptions if needed
                        }
                        return null;
                    });
                }
            }
        }
        for (long geofenceId : newGeofences) {
            long calendarId = cacheManager.getObject(Geofence.class, geofenceId).getCalendarId();
            Calendar calendar = calendarId != 0 ? cacheManager.getObject(Calendar.class, calendarId) : null;
            if (calendar == null || calendar.checkMoment(position.getFixTime())) {
                //antes que nada cambiar de grupo si es necesario
                Geofence g = null;
                try {
                    g = cacheManager.getStorage().getObject(Geofence.class, new Request(new Columns.All(), new Condition.Equals("id", geofenceId)));
                } catch (StorageException ex) {
                    Logger.getLogger(GeofenceEventHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (g != null && g.hasAttribute("groupChange")) {
                    Device d = cacheManager.getObject(Device.class, position.getDeviceId());
                    d.setGroupId(Long.parseLong(String.valueOf(g.getAttributes().get("groupChange"))));

                    try {
                        cacheManager.getStorage().updateObject(d, new Request(
                                new Columns.Exclude("id"),
                                new Condition.Equals("id", d.getId())));
                        cacheManager.invalidate(Device.class, d.getId());
                        LogAction.edit(0, d);
                    } catch (StorageException ex) {
                        Logger.getLogger(GeofenceEventHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                Event event = new Event(Event.TYPE_GEOFENCE_ENTER, position);
                event.setGeofenceId(geofenceId);
                events.put(event, position);

                CompletableFuture<Void> asyncTask = CompletableFuture.supplyAsync(() -> {
                    try {
                        if (!TransporteUtils.hasSalida(position.getDeviceId(), cacheManager)) {
                            TransporteUtils.generarSalida(position.getDeviceId(), geofenceId, position.getFixTime(), cacheManager);
                        } else {
                            TransporteUtils.updateSalida(position.getDeviceId(), geofenceId, position.getFixTime(), cacheManager);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Handle exceptions if needed
                    }
                    return null;
                });
            }
        }
        return events;
    }

}
