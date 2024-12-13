/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.api.resource;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.ObjectUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.traccar.api.BaseObjectResource;
import org.traccar.config.Config;
import org.traccar.helper.LogAction;
import org.traccar.model.Device;
import org.traccar.model.Geofence;
import org.traccar.model.Group;
import org.traccar.model.HojaSalida;
import org.traccar.model.HoraSalida;
import org.traccar.model.Itinerario;
import org.traccar.model.Salida;
import org.traccar.model.Subroute;
import org.traccar.model.Ticket;
import org.traccar.model.Tramo;
import org.traccar.model.User;
import org.traccar.session.ConnectionManager;
import org.traccar.session.cache.CacheManager;
import org.traccar.storage.StorageException;
import org.traccar.storage.query.Columns;
import org.traccar.storage.query.Condition;
import org.traccar.storage.query.Request;
import org.traccar.utils.GenericUtils;

/**
 *
 * @author K
 */
@Path("tickets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TicketsResource extends BaseObjectResource<Ticket> {

    @Inject
    private Config config;

    @Inject
    private CacheManager cacheManager;

    @Inject
    private ConnectionManager connectionManager;

    public TicketsResource() {
        super(Ticket.class);
    }

    @GET
    public Collection<Ticket> get(
            @QueryParam("all") boolean all,
            @QueryParam("finished") boolean finished,
            @QueryParam("deviceId") long deviceId) throws StorageException {

        Collection<Ticket> result = new ArrayList<>();
        Collection<Long> salidas = new ArrayList<>();
        if (deviceId != 0) {
            List<Long> devices = new ArrayList<>() {
                {
                    add(deviceId);
                }
            };
            devices.forEach(id -> {
                try {
                    salidas.addAll(storage.getObjects(Salida.class, new Request(new Columns.All(), new Condition.Equals("deviceId", id))).stream().map(item -> item.getId()).collect(Collectors.toList()));
                } catch (StorageException ex) {
                    Logger.getLogger(ItinerarioResource.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            salidas.forEach(id -> {
                try {
                    result.addAll(storage.getObjects(baseClass, new Request(new Columns.All(), new Condition.Equals("salidaId", id))));
                } catch (Exception e) {
                }
            });
        } else {
            List<Long> devices = storage.getPermissions(User.class, Device.class).stream().filter((item) -> item.getOwnerId() == getUserId()).map((item) -> item.getPropertyId()).collect(Collectors.toList());
            devices.forEach(id -> {
                try {
                    salidas.addAll(storage.getObjects(Salida.class, new Request(new Columns.All(), new Condition.Equals("deviceId", id))).stream().map(item -> item.getId()).collect(Collectors.toList()));
                } catch (StorageException ex) {
                    Logger.getLogger(ItinerarioResource.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            salidas.forEach(id -> {
                try {
                    result.addAll(storage.getObjects(baseClass, new Request(new Columns.All(), new Condition.Equals("salidaId", id))));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        return result;

    }

    @Path("{id}")
    @PUT
    @Override
    public Response update(Ticket entity) throws StorageException {
        Ticket ticket = storage.getObject(baseClass, new Request(new Columns.All(), new Condition.Equals("id", entity.getId())));
        if (ticket == null) {
            return Response.status(400).build();
        }

        ticket.setExcuse(entity.getExcuse());
        ticket.setGlobalExcuse(entity.getGlobalExcuse());

        storage.updateObject(ticket, new Request(
                new Columns.Exclude("id"),
                new Condition.Equals("id", entity.getId())));

        LogAction.edit(getUserId(), entity);

        return Response.ok(ticket).build();
    }

    @GET
    @Path("/hoja")
    public Response getHoja(
            @QueryParam("deviceId") long deviceId,
            @QueryParam("from") Date from,
            @QueryParam("to") Date to) throws StorageException {

        JSONArray objs = new JSONArray();
        for (Date day : GenericUtils.getDatesBetween(from, to)) {
            JSONObject obj = new JSONObject();
            //1 encontrar el itinerario
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            System.out.println(String.format("select * from tc_hojas where valid = true and date(day) = '%s' and deviceid = %s", sdf.format(day), String.valueOf(deviceId)));
            HojaSalida sheet = storage.getObjectsByQuery(HojaSalida.class, String.format("select * from tc_hojas where valid = true and date(day) = '%s' and deviceid = %s", sdf.format(day), String.valueOf(deviceId))).stream().findFirst().orElse(null);
            if (sheet == null) {
                System.out.println(String.format("select * from tc_salidas where valid = true and date(date) = '%s' and deviceid = %s", sdf.format(day), String.valueOf(deviceId)));
                Salida salida = storage.getObjectsByQuery(Salida.class, String.format("select * from tc_salidas where valid = true and date(date) = '%s' and deviceid = %s", sdf.format(day), String.valueOf(deviceId))).stream().findFirst().orElse(null);

                if (salida == null) {
                    obj.put("day", day);
                    continue;
                }

                sheet = new HojaSalida();
                sheet.setDeviceId(deviceId);
                sheet.setObservations("");
                sheet.setScheduleId(salida.getScheduleId());
                sheet.setDay(from);
                sheet.setValid(true);
                sheet.setId(storage.addObject(sheet, new Request(new Columns.Exclude("id"))));
            }

            //itinerario = sheet.getScheduleId();
            //2 encontrar las horas de ese itinerario
//            System.out.println(sheet);
            final HojaSalida s = sheet;
            System.out.println(s);
            Itinerario schedule = storage.getObject(Itinerario.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                {
                    add(new Condition.Equals("id", s.getScheduleId()));
                }
            })
            ));
            System.out.println(schedule);
            List<HoraSalida> horas_ida = cacheManager.getStorage().getObjects(HoraSalida.class, new Request(new Columns.All(), new Condition.Equals("group_uuid", cacheManager.getStorage().getObject(HoraSalida.class, new Request(new Columns.All(), new Condition.Equals("id", schedule.getHorasId()))).getGroup_uuid())));
            Itinerario schedule_rel = storage.getObject(Itinerario.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                {
                    add(new Condition.Equals("horasIdRel", schedule.getHorasId()));
                }
            })
            ));
            List<HoraSalida> horas_vuelta = cacheManager.getStorage().getObjects(HoraSalida.class, new Request(new Columns.All(), new Condition.Equals("group_uuid", cacheManager.getStorage().getObject(HoraSalida.class, new Request(new Columns.All(), new Condition.Equals("id", schedule_rel.getHorasId()))).getGroup_uuid())));
            
            //3 encontrar las geocercas de ese itinerario(tramos)
            List<Tramo> tramos_ida = cacheManager.getStorage().getObjects(Tramo.class, new Request(new Columns.All(), new Condition.Permission(Itinerario.class, schedule.getId(), Tramo.class)));
            List<Tramo> tramos_vuelta = cacheManager.getStorage().getObjects(Tramo.class, new Request(new Columns.All(), new Condition.Permission(Itinerario.class, schedule_rel.getId(), Tramo.class)));
            //4 por cada geocerca encontrar los tickets
//            System.out.println(tramos_ida);
//            System.out.println(tramos_vuelta);
            //datos del objeto
            Device device = cacheManager.getObject(Device.class, deviceId);
            if (device == null) {
                device = storage.getObject(Device.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                    {
                        add(new Condition.Equals("id", deviceId));
                    }
                })
                ));
            }
            System.out.println(device);
            obj.put("device", device.getName());
            obj.put("route", schedule.getName());
            obj.put("going", new JSONArray());
            obj.put("return", new JSONArray());
            obj.put("day", day);
            Map<Long, String> geofencesNames = new HashMap<>();
            for (HoraSalida ida : horas_ida) {
                ida.getHour().setYear(day.getYear());
                ida.getHour().setMonth(day.getMonth());
                ida.getHour().setDate(day.getDate());
                JSONObject going = new JSONObject();
                going.put("hour", ida.getHour());
                going.put("tickets", new JSONArray());
                Salida salida = storage.getObject(Salida.class,
                        new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                            {
                                add(new Condition.Equals("scheduleId", s.getScheduleId()));
                                add(new Condition.Equals("deviceId", s.getDeviceId()));
                                add(new Condition.Equals("date", ida.getHour()));
                                add(new Condition.Equals("valid", true));
                            }
                        })));

                if (salida != null) {
                    System.out.println(salida);
                    (going.getJSONArray("tickets")).putAll(storage.getObjects(Ticket.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                        {
                            add(new Condition.Equals("salidaId", salida.getId()));
                        }
                    }))).stream().map((ticket) -> new JSONObject() {
                        {
                            put("id", ticket.getId());
                            put("salidaId", ticket.getSalidaId());
                            put("geofenceId", ticket.getGeofenceId());
                            put("expectedTime", ticket.getExpectedTime());
                            put("enterTime", ticket.getEnterTime());
                            put("difference", ticket.getDifference());
                            put("punishment", ticket.getPunishment());
                            put("tramo", ticket.getTramo());
                            put("excuse", ticket.getExcuse());
                            put("globalExcuse", ticket.getGlobalExcuse());
                        }
                    }).collect(Collectors.toList()));
                }

                obj.getJSONArray("going").put(going);
            }

            for (HoraSalida vuelta : horas_vuelta) {
                vuelta.getHour().setYear(day.getYear());
                vuelta.getHour().setMonth(day.getMonth());
                vuelta.getHour().setDate(day.getDate());
                JSONObject going = new JSONObject();
                going.put("hour", vuelta.getHour());
                going.put("tickets", new JSONArray());
                Salida salida = storage.getObject(Salida.class,
                        new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                            {
                                add(new Condition.Equals("scheduleId", schedule_rel.getId()));
                                add(new Condition.Equals("deviceId", s.getDeviceId()));
                                add(new Condition.Equals("date", vuelta.getHour()));
                                add(new Condition.Equals("valid", true));
                            }
                        })));

                if (salida != null) {
                    System.out.println(salida);
                    (going.getJSONArray("tickets")).putAll(storage.getObjects(Ticket.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                        {
                            add(new Condition.Equals("salidaId", salida.getId()));
                        }
                    }))).stream().map((ticket) -> new JSONObject() {
                        {
                            put("id", ticket.getId());
                            put("salidaId", ticket.getSalidaId());
                            put("geofenceId", ticket.getGeofenceId());
                            put("expectedTime", ticket.getExpectedTime());
                            put("enterTime", ticket.getEnterTime());
                            put("difference", ticket.getDifference());
                            put("punishment", ticket.getPunishment());
                            put("tramo", ticket.getTramo());
                            put("excuse", ticket.getExcuse());
                            put("globalExcuse", ticket.getGlobalExcuse());
                        }
                    }).collect(Collectors.toList()));
                }

                obj.getJSONArray("return").put(going);
            }
            objs.put(obj);
        }

        return Response.ok(objs.toString()).build();
    }
}
