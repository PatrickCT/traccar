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
import org.traccar.storage.StorageException;
import org.traccar.storage.query.Columns;
import org.traccar.storage.query.Condition;
import org.traccar.storage.query.Request;
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
//        cacheManager.getDevLog().log("Position received on BaseEventHandler " + position.getId());
//        cacheManager.getDevLog().log(position.toString());
        Device d = cacheManager.getObject(Device.class, position.getDeviceId());
//        if (d != null) {
//            cacheManager.getDevLog().log(d.toString());
//        }
        Logger.getLogger(GeofenceEventHandler.class.getName()).log(Level.INFO, "Geofence event \n\r" + position + "\n\rdevice " + position.getDeviceId());
        if (!PositionUtil.isLatest(cacheManager, position)) {
            Logger.getLogger(GeofenceEventHandler.class.getName()).log(Level.INFO, "Is not latest");
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
                    Logger.getLogger(GeofenceEventHandler.class.getName()).log(Level.INFO, "Geofence exit evt " + event + " device " + position.getDeviceId()+" group "+d.getGroupId());
//                    cacheManager.getDevLog().log("Salida geocerca " + geofence.getId() + " " + d.getId());
//                    cacheManager.getDevLog().log(geofence.toString());
                    CompletableFuture<Void> asyncTask = CompletableFuture.supplyAsync(() -> {
                        try {
                            TransporteUtils.cleanSalidas(geofenceId, position.getDeviceId(), cacheManager);
                            TransporteUtils.finishOldSalidas(geofenceId, position.getDeviceId(), cacheManager);
                            if (!TransporteUtils.hasSalida(position.getDeviceId(), cacheManager, geofenceId)) {
//                                cacheManager.getDevLog().log("Se generara la salida");
                                TransporteUtils.generarSalida(position.getDeviceId(), geofenceId, event.getEventTime(), cacheManager);
                            } else {
//                                cacheManager.getDevLog().log("Se actualizara la salida");
                                TransporteUtils.updateSalida(position.getDeviceId(), geofenceId, event.getEventTime(), cacheManager, false);
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

                if (g != null) {
                    geofenceAttsEval(d, g);
                }

                Event event = new Event(Event.TYPE_GEOFENCE_ENTER, position);
                event.setGeofenceId(geofenceId);
                events.put(event, position);
                Logger.getLogger(GeofenceEventHandler.class.getName()).log(Level.INFO, "Geofence enter evt " + event + " device " + position.getDeviceId()+" group "+d.getGroupId());
//                cacheManager.getDevLog().log("Entrada geocerca " + g.getId() + " " + d.getId());
//                cacheManager.getDevLog().log(g.toString());
                CompletableFuture<Void> asyncTask = CompletableFuture.supplyAsync(() -> {
                    try {
                        if (!TransporteUtils.hasSalida(position.getDeviceId(), cacheManager, geofenceId)) {
                            Logger.getLogger(GeofenceEventHandler.class.getName()).log(Level.INFO, "Geofence enter, no salida, " + event);
                            // TransporteUtils.generarSalida(position.getDeviceId(), geofenceId, position.getFixTime(), cacheManager);
                        } else {
//                            cacheManager.getDevLog().log("Se actualizara la salida");
                            Logger.getLogger(GeofenceEventHandler.class.getName()).log(Level.INFO, "Geofence enter, update salida, " + event + " device " + position.getDeviceId());
                            TransporteUtils.updateSalida(position.getDeviceId(), geofenceId, event.getEventTime(), cacheManager, true);
                        }
                    } catch (Exception e) {
                        Logger.getLogger(GeofenceEventHandler.class.getName()).log(Level.INFO, null, "Error transporte");
                        Logger.getLogger(GeofenceEventHandler.class.getName()).log(Level.SEVERE, null, e);
                    }
                    return null;
                });
            }
        }
        return events;
    }

    private void geofenceAttsEval(Device d, Geofence g) {
        for (String att : g.getAttributes().keySet()) {
            String val = String.valueOf(g.getAttributes().get(att));
            switch (att) {
                case "groupChange":
                    changeGroupGeofence(d, Long.parseLong(val));
                    break;
                case "colorChange":
                    changeColorGeofence(d, val);
                    break;
                default:
                    continue;
            }
        }
    }

    private void changeGroupGeofence(Device d, Long groupId) {
        cacheManager.getDevLog().log(String.format("Device {imei: %s, name: %s} changed group from %s to %s", d.getUniqueId(), d.getName(), d.getGroupId(), groupId));
        d.setGroupId(groupId);

        try {
            cacheManager.getStorage().updateObject(d, new Request(
                    new Columns.Exclude("id"),
                    new Condition.Equals("id", d.getId())));
            cacheManager.invalidate(Device.class, d.getId());
            LogAction.edit(0, d);
        } catch (StorageException ex) {
            ex.printStackTrace();
            Logger.getLogger(GeofenceEventHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void changeColorGeofence(Device d, String color) {
        d.getAttributes().put("background", color);

        try {
            cacheManager.getStorage().updateObject(d, new Request(
                    new Columns.Exclude("id"),
                    new Condition.Equals("id", d.getId())));
            cacheManager.invalidate(Device.class, d.getId());
            LogAction.edit(0, d);
        } catch (StorageException ex) {
            ex.printStackTrace();
            Logger.getLogger(GeofenceEventHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
