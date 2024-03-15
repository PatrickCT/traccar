/*
 * Copyright 2015 - 2022 Anton Tananaev (anton@traccar.org)
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
package org.traccar.api.resource;

import org.traccar.api.BaseObjectResource;
import org.traccar.broadcast.BroadcastService;
import org.traccar.database.MediaManager;
import org.traccar.helper.LogAction;
import org.traccar.model.Device;
import org.traccar.model.DeviceAccumulators;
import org.traccar.model.Position;
import org.traccar.model.User;
import org.traccar.session.ConnectionManager;
import org.traccar.session.cache.CacheManager;
import org.traccar.storage.StorageException;
import org.traccar.storage.query.Columns;
import org.traccar.storage.query.Condition;
import org.traccar.storage.query.Request;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.json.JSONObject;
import org.traccar.model.Driver;
import org.traccar.model.Geofence;
import org.traccar.model.Salida;
import org.traccar.model.Ticket;
import org.traccar.utils.GenericUtils;

@Path("devices")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DeviceResource extends BaseObjectResource<Device> {

    @Inject
    private CacheManager cacheManager;

    @Inject
    private ConnectionManager connectionManager;

    @Inject
    private BroadcastService broadcastService;

    @Inject
    private MediaManager mediaManager;

    public DeviceResource() {
        super(Device.class);
    }

    @GET
    public Collection<Device> get(
            @QueryParam("all") boolean all, @QueryParam("userId") long userId,
            @QueryParam("uniqueId") List<String> uniqueIds,
            @QueryParam("id") List<Long> deviceIds) throws StorageException {

        if (!uniqueIds.isEmpty() || !deviceIds.isEmpty()) {

            List<Device> result = new LinkedList<>();
            for (String uniqueId : uniqueIds) {
                result.addAll(storage.getObjects(Device.class, new Request(
                        new Columns.All(),
                        new Condition.And(
                                new Condition.Equals("uniqueId", uniqueId),
                                new Condition.Permission(User.class, getUserId(), Device.class)))));
            }
            for (Long deviceId : deviceIds) {
                if (permissionsService.notAdmin(getUserId())) {
                    result.addAll(storage.getObjects(Device.class, new Request(
                            new Columns.All(),
                            new Condition.And(
                                    new Condition.Equals("id", deviceId),
                                    new Condition.Permission(User.class, getUserId(), Device.class)))));
                } else {
                    result.addAll(storage.getObjects(Device.class, new Request(
                            new Columns.All(),
                            new Condition.Equals("id", deviceId))));
                }

            }
            return result;

        } else {

            var conditions = new LinkedList<Condition>();

            if (all) {
                if (permissionsService.notAdmin(getUserId())) {
                    conditions.add(new Condition.Permission(User.class, getUserId(), baseClass));
                }
            } else {
                if (userId == 0) {
                    conditions.add(new Condition.Permission(User.class, getUserId(), baseClass));
                } else {
                    permissionsService.checkUser(getUserId(), userId);
                    conditions.add(new Condition.Permission(User.class, userId, baseClass).excludeGroups());
                }
            }

            return storage.getObjects(baseClass, new Request(new Columns.All(), Condition.merge(conditions)));

        }
    }

    @Path("{id}/accumulators")
    @PUT
    public Response updateAccumulators(DeviceAccumulators entity) throws StorageException {
        if (permissionsService.notAdmin(getUserId())) {
            permissionsService.checkManager(getUserId());
            permissionsService.checkPermission(Device.class, getUserId(), entity.getDeviceId());
        }

        Position position = storage.getObject(Position.class, new Request(
                new Columns.All(), new Condition.LatestPositions(entity.getDeviceId())));
        if (position != null) {
            if (entity.getTotalDistance() != null) {
                position.getAttributes().put(Position.KEY_TOTAL_DISTANCE, entity.getTotalDistance());
            }
            if (entity.getHours() != null) {
                position.getAttributes().put(Position.KEY_HOURS, entity.getHours());
            }
            position.setId(storage.addObject(position, new Request(new Columns.Exclude("id"))));

            Device device = new Device();
            device.setId(position.getDeviceId());
            device.setPositionId(position.getId());
            storage.updateObject(device, new Request(
                    new Columns.Include("positionId"),
                    new Condition.Equals("id", device.getId())));

            try {
                cacheManager.addDevice(position.getDeviceId());
                cacheManager.updatePosition(position);
                connectionManager.updatePosition(true, position);
            } finally {
                cacheManager.removeDevice(position.getDeviceId());
            }
        } else {
            throw new IllegalArgumentException();
        }

        LogAction.resetDeviceAccumulators(getUserId(), entity.getDeviceId());
        return Response.noContent().build();
    }

    @Path("{id}/image")
    @POST
    @Consumes("image/*")
    public Response uploadImage(
            @PathParam("id") long deviceId, File file,
            @HeaderParam(HttpHeaders.CONTENT_TYPE) String type) throws StorageException, IOException {

        Device device = storage.getObject(Device.class, new Request(
                new Columns.All(),
                new Condition.And(
                        new Condition.Equals("id", deviceId),
                        new Condition.Permission(User.class, getUserId(), Device.class))));
        if (device != null) {
            String name = "device";
            String extension = type.substring("image/".length());
            try (var input = new FileInputStream(file);
                    var output = mediaManager.createFileStream(device.getUniqueId(), name, extension)) {
                input.transferTo(output);
            }
            return Response.ok(name + "." + extension).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("{id}/ticket")
    public Response ticket(@PathParam("id") long deviceId) throws StorageException {
        JSONObject response = new JSONObject("{}");
        Salida salida = storage.getObject(Salida.class,
                new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                    {
                        add(new Condition.Equals("deviceId", deviceId));
                        add(new Condition.Equals("finished", false));
                    }
                })));
        Date StartDate = new Date();
        StartDate.setHours(0);
        List<Salida> salidas = storage.getObjects(Salida.class,
                new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                    {
                        add(new Condition.Equals("deviceId", deviceId));
                        add(new Condition.Between("date", "from", StartDate, "to",
                                GenericUtils.addTimeToDate(StartDate, Calendar.DAY_OF_MONTH, 1)));
                    }
                })));
        response.put("vueltas", salidas.size());
        List<Driver> choferes = new ArrayList<>();
        List<Long> permisos = storage.getPermissions(Device.class, Driver.class).stream()
                .filter((p) -> p.getOwnerId() == deviceId).map((p) -> p.getPropertyId()).collect(Collectors.toList());
        permisos.forEach((id) -> {
            try {
                choferes.add(storage.getObject(Driver.class,
                        new Request(new Columns.All(), new Condition.Equals("id", id))));
            } catch (StorageException ex) {
                Logger.getLogger(DeviceResource.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        response.put("choferes", choferes);
        if (salida != null) {
            response.put("salida", salida);
            List<Ticket> tickets = storage.getObjects(Ticket.class,
                    new Request(new Columns.All(), new Condition.Equals("salidaId", salida.getId())));
            response.put("ticket", tickets);
            List<Geofence> geoNames = new ArrayList<>();
            Map<Long, Object> otros = new HashMap<>();

            List<Salida> otras_salidas = storage.getObjects(Salida.class,
                    new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                        {
                            add(new Condition.Equals("scheduleId", salida.getScheduleId()));
                            add(new Condition.Between("date", "from",
                                    GenericUtils.addTimeToDate(new Date(), Calendar.HOUR_OF_DAY, -1), "to",
                                    new Date()));
                        }
                    })));

            for (int i = 0; i < tickets.size(); i++) {
                Ticket ticket = tickets.get(i);
                Geofence g;
                List<Ticket> t;
                try {
                    g = storage.getObject(Geofence.class,
                            new Request(new Columns.All(), new Condition.Equals("id", ticket.getGeofenceId())));
                    geoNames.add(g);

                    t = storage.getObjects(Ticket.class,
                            new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                                {
                                    add(new Condition.Equals("geofenceId", g.getId()));
                                    add(new Condition.Between("enterTime", "from",
                                            GenericUtils.addTimeToDate(new Date(), Calendar.HOUR_OF_DAY, -1), "to",
                                            new Date()));
                                }
                            })));

                    if (!t.isEmpty()) {

                    }
                } catch (StorageException ex) {
                    Logger.getLogger(DeviceResource.class.getName()).log(Level.SEVERE, null, ex);
                }

                response.put("geofencesNames", geoNames);
                response.put("geofencesPassed", otros);
            }
        }

        return Response.ok(response.toMap()).build();
    }

    private static <T> List<T> getLastNItems(List<T> list, int n) {
        int startIndex = Math.max(0, list.size() - n);
        int endIndex = list.size();
        return list.subList(startIndex, endIndex);
    }

    @Path("user/{id}")
    @GET
    public Collection<Device> getDevices(@PathParam("id") long id
    ) throws SQLException {
        try {
            var conditions = new LinkedList<Condition>();

            permissionsService.checkUser(getUserId(), id);
            conditions.add(new Condition.Permission(User.class, id, baseClass).excludeGroups());

            return storage.getObjects(baseClass, new Request(new Columns.All(), Condition.merge(conditions)));
        } catch (StorageException ex) {
            Logger.getLogger(DeviceResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(DeviceResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<>();
    }
}
