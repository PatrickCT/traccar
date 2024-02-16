/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.api.resource;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.traccar.api.BaseResource;
import org.traccar.model.Device;
import org.traccar.model.Event;
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
@Path("ws")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WSResource extends BaseResource {

    @Inject
    private CacheManager cacheManager;

    @Path("extra/{id}")
    @GET
    public Response geofenceReportById(@PathParam("id") long id) {
        try {
            JSONArray events = new JSONArray();
            JSONObject obj = new JSONObject();

            Date today = new Date();
            today.setHours(0);

            List<Event> eventos = storage.getObjects(Event.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                {
                    add(new Condition.Equals("geofenceId", id));
                    add(new Condition.Equals("type", "geofenceEnter"));
                    add(new Condition.Between("eventtime", "from", today, "to", GenericUtils.addTimeToDate(today, Calendar.DAY_OF_MONTH, 1)));
                }
            })));

            //Collection<Event> eventos = Context.getDataManager().getEventsGeo(id, from, from);
            for (Event ev : eventos) {
                Device dev = cacheManager.getObject(Device.class, ev.getDeviceId());
                if (dev == null) {
                    dev = storage.getObject(Device.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                        {
                            add(new Condition.Equals("id", ev.getDeviceId()));
                        }
                    })));
                }
                obj = new JSONObject();
                obj.put("geofenceid", ev.getGeofenceId());
                obj.put("deviceid", ev.getDeviceId());
                obj.put("devicename", dev != null ? dev.getName() : "Desconocido");
                obj.put("time", ev.getEventTime());
                events.put(obj);
            }

            return Response.ok(events.toString()).build();
        } catch (StorageException ex) {
            Logger.getLogger(WSResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.ok().build();
    }

    @Path("extra")
    @POST
    public Response reportByIds(String body) {
        JSONArray ids = new JSONObject(body).getJSONArray("geofenceids");
        JSONArray result = new JSONArray();
        Map<String, JSONArray> mapa = new HashMap();
        Map<Long, String> devices = new HashMap();
        for (Object o : ids) {
            try {
                JSONArray events = new JSONArray();
                JSONObject obj = new JSONObject();
                Date today = new Date();
                //today.setHours(0);

                List<Event> eventos = storage.getObjects(Event.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                    {
                        add(new Condition.Equals("geofenceId", o.toString()));
//                        add(new Condition.Equals("type", "geofenceEnter"));
                        add(new Condition.Between("eventtime", "from", GenericUtils.addTimeToDate(today, Calendar.HOUR_OF_DAY, -3), "to", today));
                    }
                })));

                //Collection<Event> eventos = Context.getDataManager().getEventsGeo(id, from, from);
                for (Event ev : eventos) {
                    if (!ev.getType().equals("geofenceEnter")) {
                        continue;
                    }
                    if (!devices.containsKey(ev.getDeviceId())) {
                        Device dev = cacheManager.getObject(Device.class, ev.getDeviceId());
                        if (dev == null) {
                            dev = storage.getObject(Device.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                                {
                                    add(new Condition.Equals("id", ev.getDeviceId()));
                                }
                            })));
                        }
                        devices.put(ev.getDeviceId(), dev.getName());
                    }

                    obj = new JSONObject();
                    obj.put("geofenceid", ev.getGeofenceId());
                    obj.put("deviceid", ev.getDeviceId());
                    obj.put("devicename", devices.containsKey(ev.getDeviceId()) ? devices.get(ev.getDeviceId()) : "Desconocido");
                    obj.put("time", ev.getEventTime());
                    events.put(obj);
                }
                mapa.put(o.toString(), events);
            } catch (StorageException ex) {
                Logger.getLogger(WSResource.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        result.put(mapa);
        return Response.ok(result.toString()).build();
    }

    @Path("test/test/test")
    @GET
    public Response test() {
        try {
            List<Integer> ids = new ArrayList<>();
            ids.add(7);
            ids.add(8);
            ids.add(9);
            ids.add(10);
            var result = storage.getObjects(Device.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                {
                    add(new Condition.In("id", ids));

                }
            })));
//            System.out.println("test");
//            System.out.println(result);
            return Response.ok(result.toString()).build();
        } catch (StorageException ex) {
            Logger.getLogger(WSResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.ok().build();
    }
}
