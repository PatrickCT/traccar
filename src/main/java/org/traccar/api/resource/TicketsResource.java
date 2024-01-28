/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.api.resource;

import java.time.Duration;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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
import org.traccar.model.Device;
import org.traccar.model.HojaSalida;
import org.traccar.model.HoraSalida;
import org.traccar.model.Itinerario;
import org.traccar.model.Salida;
import org.traccar.model.Ticket;
import org.traccar.model.User;
import org.traccar.session.ConnectionManager;
import org.traccar.session.cache.CacheManager;
import org.traccar.storage.StorageException;
import org.traccar.storage.query.Columns;
import org.traccar.storage.query.Condition;
import org.traccar.storage.query.Request;

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
            List<Long> devices = storage.getPermissions(User.class, Device.class).stream().map((item) -> item.getPropertyId()).collect(Collectors.toList());
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
        }
        return result;

    }

    @GET
    @Path("/hoja")
    public Response getHoja(
            @QueryParam("deviceId") long deviceId,
            @QueryParam("from") Date from,
            @QueryParam("to") Date to) throws StorageException {

        JSONObject obj = new JSONObject();
        HojaSalida sheet = storage.getObject(HojaSalida.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
            {
                add(new Condition.Equals("valid", true));
                add(new Condition.Between("day", "from", from, "to", to));
            }
        })
        ));
        if (sheet == null) {
            Salida salida = storage.getObject(Salida.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                {
                    add(new Condition.Equals("valid", true));
                    add(new Condition.Equals("deviceId", deviceId));
                    add(new Condition.Between("date", "from", from, "to", to));
                }
            })
            ));

            if (salida == null) {
                obj.append("deviceId", 0);
                obj.append("device", "");
                return Response.ok(obj.toMap()).build();
            }

            sheet = new HojaSalida();
            sheet.setDeviceId(deviceId);
            sheet.setObservations("");
            sheet.setScheduleId(salida.getScheduleId());
            sheet.setDay(from);
            sheet.setValid(true);
            sheet.setId(storage.addObject(sheet, new Request(new Columns.Exclude("id"))));
        }

        obj.append("deviceId", deviceId);
        obj.append("device", ((Device) ObjectUtils.defaultIfNull(cacheManager.getObject(Device.class, deviceId), storage.getObject(HojaSalida.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
            {
                add(new Condition.Equals("id", deviceId));
            }
        })
        )))).getName());
        obj.put("observations", sheet.getObservations());
        final HojaSalida s = sheet;
        Itinerario schedule = storage.getObject(Itinerario.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
            {
                add(new Condition.Equals("id", s.getScheduleId()));
            }
        })
        ));
        List<HoraSalida> horas_ida = cacheManager.getStorage().getObjects(HoraSalida.class, new Request(new Columns.All(), new Condition.Equals("name", cacheManager.getStorage().getObject(HoraSalida.class, new Request(new Columns.All(), new Condition.Equals("id", schedule.getHorasId()))).getName())));
        Itinerario schedule_rel = storage.getObject(Itinerario.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
            {
                add(new Condition.Equals("horasIdRel", schedule.getHorasId()));
            }
        })
        ));
        List<HoraSalida> horas_vuelta = cacheManager.getStorage().getObjects(HoraSalida.class, new Request(new Columns.All(), new Condition.Equals("name", cacheManager.getStorage().getObject(HoraSalida.class, new Request(new Columns.All(), new Condition.Equals("id", schedule_rel.getHorasId()))).getName())));

        List<Salida> salidas_ida = storage.getObjects(Salida.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
            {
                add(new Condition.Equals("valid", true));
                add(new Condition.Equals("deviceId", deviceId));
                add(new Condition.Between("date", "from", from, "to", to));
                add(new Condition.Equals("scheduleId", schedule.getId()));
            }
        })
        ));

        List<Salida> salidas_vuelta = storage.getObjects(Salida.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
            {
                add(new Condition.Equals("valid", true));
                add(new Condition.Equals("deviceId", deviceId));
                add(new Condition.Between("date", "from", from, "to", to));
                add(new Condition.Equals("scheduleId", schedule.getId()));
            }
        })
        ));

        obj.put("hours", new JSONArray());
        for (int i = 0; i < horas_ida.size(); i++) {
            final int index = i;  // Create a final variable
            ((JSONArray) obj.get("hours")).put(new JSONObject() {
                {
                    put("forward", new JSONObject() {
                        {
                            append("expected", horas_ida.get(index) != null ? horas_ida.get(index).getHour() : null);
                            append("difference", (salidas_ida.get(index) != null && salidas_ida.get(index) != null) ? Duration.between(horas_ida.get(index).getHour().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), salidas_ida.get(index).getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()) : null);
                        }
                    });
                    put("backward", new JSONObject() {
                        {
                            append("expected", horas_vuelta.get(index) != null ? horas_vuelta.get(index).getHour() : null);
                            append("difference", (salidas_vuelta.get(index) != null && salidas_vuelta.get(index) != null) ? Duration.between(horas_vuelta.get(index).getHour().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), salidas_vuelta.get(index).getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()) : null);
                        }
                    });
                }
            });
        }

        return Response.ok(obj.toMap()).build();
    }
}
