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
import org.traccar.model.*;
import org.traccar.session.ConnectionManager;
import org.traccar.session.cache.CacheManager;
import org.traccar.storage.StorageException;
import org.traccar.storage.query.Columns;
import org.traccar.storage.query.Condition;
import org.traccar.storage.query.Request;

import javax.annotation.security.PermitAll;
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
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.json.JSONObject;
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
            @QueryParam("id") List<Long> deviceIds,
            @QueryParam("includeGroups") boolean includeGroups) throws StorageException {

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
                    if (includeGroups) {
                        conditions.add(new Condition.Permission(User.class, userId, baseClass));
                    } else {
                        conditions.add(new Condition.Permission(User.class, userId, baseClass).excludeGroups());
                    }
                }
            }
            List<Device> result = storage.getObjects(baseClass, new Request(new Columns.All(), Condition.merge(conditions)));
            return result;

        }
    }

    @Path("{id}/accumulators")
    @PUT
    public Response updateAccumulators(DeviceAccumulators entity) throws StorageException {
        if (permissionsService.notAdmin(getUserId())) {
//            permissionsService.checkManager(getUserId());
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

        // Fetching subroutes
        List<Subroute> subroutes = storage.getObjects(Subroute.class, new Request(new Columns.All()));
        response.put("subroutes", subroutes);

        // Fetching salida
        Salida salida = storage.getObject(Salida.class,
                new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                    {
                        add(new Condition.Equals("deviceId", deviceId));
                        add(new Condition.Equals("finished", false));
                    }
                })));

        Date StartDate = new Date();
        StartDate.setHours(0);

        // Fetching salidas
        List<Salida> salidas = storage.getObjectsByQuery(Salida.class, String.format("select * from tc_salidas where deviceid='%s' and DATE(date)= DATE(NOW())", deviceId));
//        List<Salida> salidas = storage.getObjects(Salida.class,
//                new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
//                    {
//                        add(new Condition.Equals("deviceId", deviceId));
//                        add(new Condition.Between("date", "from", StartDate, "to",
//                                GenericUtils.addTimeToDate(StartDate, Calendar.DAY_OF_MONTH, 1)));
//                    }
//                })));
        response.put("vueltas", salidas.size());

        // Fetching choferes permissions
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
//        System.out.println("Time taken to fetch choferes permissions: " + (choferesEndTime - choferesStartTime) / 1e6 + " ms");
        response.put("choferes", choferes);

        // Processing salida
        if (salida != null) {
            response.put("salida", salida);

            // Fetching tickets
            List<Ticket> tickets = storage.getObjects(Ticket.class,
                    new Request(new Columns.All(), new Condition.Equals("salidaId", salida.getId())));
            response.put("ticket", tickets);
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
            conditions.add(new Condition.Permission(User.class, id, baseClass));

            return storage.getObjects(baseClass, new Request(new Columns.All(), Condition.merge(conditions)));
        } catch (StorageException ex) {
            Logger.getLogger(DeviceResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(DeviceResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<>();
    }

    @PermitAll
    @Path("reporte")
    @GET
    @Produces("application/json")
    public Response reporte() throws SQLException, StorageException {
        JSONObject obj = new JSONObject();
        List<Device> todos = storage.getObjectsByQuery(Device.class, "select * from tc_devices");
        //List<Device> todosCopy = new ArrayList<>(todos);
        List<Device> principalesDirectos = storage.getObjectsByQuery(Device.class, String.format(""
                + "SELECT d.*, u.name as userName \n"
                + "\n"
                + "FROM tc_users u \n"
                + "INNER JOIN tc_user_device ud ON ud.userid = u.id\n"
                + "INNER JOIN tc_devices d ON d.id = ud.deviceid\n"
                + "\n"
                + "WHERE u.main"
        ));
        List<Device> principalesGrupos = storage.getObjectsByQuery(Device.class,String.format(""
                + "SELECT d.*, u.name as userName \n"
                + "FROM tc_users u \n"
                + "INNER JOIN tc_user_group ug ON ug.userid = u.id\n"
                + "INNER JOIN tc_devices d ON d.groupid=ug.groupid\n"
                + "INNER JOIN tc_groups g ON g.id=ug.groupid\n"
                + "WHERE u.main"
        ));
        List<Device> principales = new ArrayList<>();
        List<Device> duplicados = new ArrayList<>();
        List<Device> duplicadosOriginal = new ArrayList<>();

        duplicadosOriginal.addAll(principalesDirectos);
        duplicadosOriginal.addAll(principalesGrupos);
        principales.addAll(principalesDirectos);
        for (Device d : principalesGrupos) {
            if (principales.stream().filter(o -> o.getId() == d.getId()).findFirst().orElse(null) == null) {
                principales.add(d);
            }
        }

        Map<Long, Integer> idCountMap = new HashMap<>();
        for (Device device : duplicadosOriginal) {
            idCountMap.put(device.getId(), idCountMap.getOrDefault(device.getId(), 0) + 1);
        }

        for (Device device : duplicadosOriginal) {
            if (idCountMap.get(device.getId()) > 1) {
                duplicados.add(device);
            }
        }

        obj.put("todos", todos);
        obj.put("principales", GenericUtils.removeDuplicates(principales, Device::getUniqueId));
        obj.put("duplicados", duplicados);
//        obj.put("faltantes", todosCopy);

        return Response.ok().entity(obj.toMap()).build();
    }
}
