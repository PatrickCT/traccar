/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.api.resource;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.ObjectUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.traccar.api.AsyncSocket;
import org.traccar.api.BaseResource;
import org.traccar.config.Config;
import org.traccar.model.Device;
import org.traccar.model.Event;
import org.traccar.model.Geofence;
import org.traccar.model.Position;
import org.traccar.session.cache.CacheManager;
import org.traccar.storage.StorageException;
import org.traccar.storage.query.Columns;
import org.traccar.storage.query.Condition;
import org.traccar.storage.query.Request;
import org.traccar.utils.ExternalUtils;
import org.traccar.utils.GenericUtils;

/**
 *
 * @author K
 */
@Path("dev")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DevResource extends BaseResource {

    @Inject
    private CacheManager cacheManager;
    @Inject
    private Config config;

    @Path("devicesInventory")
    @PermitAll
    @GET
    public Response geofenceReportById(@PathParam("id") long id) {
        try {
            JSONArray data = new JSONArray();
            JSONObject obj = new JSONObject();

            Date today = new Date();
            today.setHours(0);

            List<Device> dispositivos = storage.getObjects(Device.class, new Request(new Columns.All()));

            //Collection<Event> eventos = Context.getDataManager().getEventsGeo(id, from, from);
            for (Device dev : dispositivos) {
                obj = new JSONObject();
                obj.put("imei", dev.getUniqueId());
                Position pos = cacheManager.getPosition(dev.getId());
                if (pos == null) {
                    pos = cacheManager.getObject(Position.class, dev.getPositionId());
                    if (pos == null) {
                        pos = storage.getObject(Position.class, new Request(new Columns.All(), new Condition.Equals("id", dev.getPositionId())));
                    }
                }
                if (pos != null) {
                    obj.put("protocol", pos.getProtocol());
                } else {
                    obj.put("protocol", "");
                }

                data.put(obj);
            }

            return Response.ok(data.toString()).build();
        } catch (StorageException ex) {
            Logger.getLogger(WSResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.ok().build();
    }

    @Path("sitracktest/{device}")
    @PermitAll
    @GET
    public String stracktest(@PathParam("device") long id) {
        Position position = cacheManager.getPosition(id);
        Event event = new Event();
        event.setType(Event.TYPE_DEVICE_MOVING);
        return ExternalUtils.sitrackSendReport(position, event, cacheManager);
    }

    @Path("geofences_collition_check/{geofenceId}/{positionId}")
    @PermitAll
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String check(@PathParam("geofenceId") long geofenceId, @PathParam("positionId") long positionId) {
        try {
            Position position = storage.getObject(Position.class, new Request(new Columns.All(), new Condition.Equals("id", positionId)));
            Geofence geofence = storage.getObject(Geofence.class, new Request(new Columns.All(), new Condition.Equals("id", geofenceId)));

            System.out.println(position);
            System.out.println("Lat: " + position.getLatitude());
            System.out.println("Lng: " + position.getLongitude());
            System.out.println("\n");
            System.out.println(geofence);
            System.out.println(geofence.getArea());

            String coordsText = geofence.getArea().replaceAll("^POLYGON\\s*\\(\\(", "")
                    .replaceAll("\\)\\)$", "")
                    .trim();
            String[] pairsArray = coordsText.split(",");
            List<String[]> pairs = new ArrayList<>();

            for (String pair : pairsArray) {
                String[] latLng = pair.trim().split("\\s+");
                String lat = latLng[0];
                String lng = latLng[1];
                System.out.println("lat: " + lat + " lng: " + lng);
                pairs.add(new String[]{lng, lat}); // GeoJSON expects [lng, lat]
            }

            System.out.println(pairs.stream().map(str_arr -> "[" + str_arr[0] + "," + str_arr[1] + "]").collect(Collectors.joining(",\n")));
            System.out.println("\n");
            System.out.println("Contains: " + geofence.getGeometry().containsPoint(config, geofence, position.getLatitude(), position.getLongitude()));

            return ""
                    + "<!DOCTYPE html>\n"
                    + "<html lang=\"en\">\n"
                    + "<head>\n"
                    + "  <meta charset=\"UTF-8\" />\n"
                    + "  <title>Polygon Containment Check</title>\n"
                    + "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n"
                    + "  <link\n"
                    + "    rel=\"stylesheet\"\n"
                    + "    href=\"https://unpkg.com/leaflet@1.9.4/dist/leaflet.css\"\n"
                    + "  />\n"
                    + "  <style>\n"
                    + "    #map {\n"
                    + "      height: 100vh;\n"
                    + "      width: 100%;\n"
                    + "    }\n"
                    + "  </style>\n"
                    + "</head>\n"
                    + "<body>\n"
                    + "  <div id=\"map\"></div>\n"
                    + "\n"
                    + "  <script src=\"https://unpkg.com/leaflet@1.9.4/dist/leaflet.js\"></script>\n"
                    + "  <script src=\"https://unpkg.com/@turf/turf@6.5.0/turf.min.js\"></script>\n"
                    + "\n"
                    + "  <script>\n"
                    + "    const map = L.map('map').setView([17.9535, -102.1936], 17);\n"
                    + "\n"
                    + "    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {\n"
                    + "      attribution: '&copy; OpenStreetMap contributors'\n"
                    + "    }).addTo(map);\n"
                    + "\n"
                    + "    // Define the polygon (GeoJSON format expects [lng, lat])\n"
                    + "    const polygonCoords = [\n"
                    + pairs.stream().map(str_arr -> "[" + str_arr[0] + "," + str_arr[1] + "]").collect(Collectors.joining(",\n")) + "\n"
                    + "    ];\n"
                    + "\n"
                    + "    const polygon = L.polygon(polygonCoords.map(p => [p[1], p[0]]), {\n"
                    + "      color: 'blue',\n"
                    + "      fillOpacity: 0.2\n"
                    + "    }).addTo(map);\n"
                    + "\n"
                    + "    // Define the point to test\n"
                    + "    const testPoint = [" + position.getLongitude() + ", " + position.getLatitude() + "];\n"
                    + "    const point = turf.point(testPoint);\n"
                    + "    const turfPolygon = turf.polygon([[...polygonCoords]]);\n"
                    + "    const isInside = turf.booleanPointInPolygon(point, turfPolygon);\n"
                    + "\n"
                    + "    // Add marker\n"
                    + "    L.circleMarker([testPoint[1], testPoint[0]], {\n"
                    + "      radius: 8,\n"
                    + "      color: isInside ? 'green' : 'red',\n"
                    + "      fillColor: isInside ? 'green' : 'red',\n"
                    + "      fillOpacity: 0.8\n"
                    + "    })\n"
                    + "      .addTo(map)\n"
                    + "      .bindPopup(`Point is <strong>${isInside ? 'inside' : 'outside'}</strong> the polygon.`)\n"
                    + "      .openPopup();\n"
                    + "  </script>\n"
                    + "</body>\n"
                    + "</html>";
        } catch (StorageException ex) {
            Logger.getLogger(DevResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "error";
    }
}
