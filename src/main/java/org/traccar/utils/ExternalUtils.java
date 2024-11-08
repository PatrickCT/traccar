/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.utils;

import com.unisolutions.PEvento;
import com.unisolutions.ServiceSoapStub;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.apache.axis.AxisFault;
import org.apache.axis.client.Service;
import org.apache.commons.lang3.ObjectUtils;
import org.datacontract.schemas._2004._07.IronTracking.AppointResult;
import org.datacontract.schemas._2004._07.IronTracking.Event;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tempuri.BasicHttpBinding_IRCServiceStub;
import org.traccar.helper.DataConverter;
import org.traccar.model.Device;
import org.traccar.model.Position;
import org.traccar.model.WebService;
import org.traccar.session.cache.CacheManager;
import org.traccar.storage.StorageException;
import org.traccar.storage.query.Columns;
import org.traccar.storage.query.Condition;
import org.traccar.storage.query.Request;
import static org.traccar.utils.GeneralUtils.genericPOST;
import static org.traccar.utils.GenericUtils.aDate2Epoch;
import static org.traccar.utils.GenericUtils.addTimeToDate;

/**
 *
 * @author K
 */
public final class ExternalUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalUtils.class);
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateRandomCode(int length) {
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = RANDOM.nextInt(ALPHANUMERIC.length());
            char randomChar = ALPHANUMERIC.charAt(randomIndex);
            sb.append(randomChar);
        }

        return sb.toString();
    }

    public static String sitrackSend(Position position, CacheManager cacheManager) {
        try {
            LOGGER.info("Sitrack proccess");
            WebService ws = cacheManager.getStorage().getObject(WebService.class,
                    new Request(new Columns.All(), new Condition.Equals("tableName", "tc_sitrack")));
            LOGGER.info(ws.toString());
            JSONObject obj = new JSONObject();
            Device device = cacheManager.getObject(Device.class, position.getDeviceId()); // Context.getDeviceManager().getById(position.getDeviceId());
            LOGGER.info(device.toString());
            obj.put("imei_no", device.getUniqueId());
            obj.put("lattitude", String.valueOf(position.getLatitude()));
            obj.put("longitude", String.valueOf(position.getLongitude()));
            obj.put("angle", String.valueOf(position.getCourse()));
            obj.put("speed", String.valueOf(position.getSpeed() * 1.852));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            obj.put("time", sdf.format(GeneralUtils.dateToUTC(position.getDeviceTime())));
            obj.put("battery_voltage", position.getAttributes().getOrDefault("battery", 0).toString());
            obj.put("gps_validity", position.getValid() ? "A" : "A"); // se cambia a solicitud de sitrack para enviar
            LOGGER.info("Sitrack obj:");
            LOGGER.info(obj.toString());                                                         // siempre la letra A

            String result = GeneralUtils.genericPOST("http://54.193.100.127:5175/", obj.toString(), new HashMap<>(), 5);
            LOGGER.info(obj.toString());
            JSONObject wh = new JSONObject();
            wh.put("webservice", obj.toString());
            wh.put("device", device);
            GeneralUtils.genericPOST("https://crmgpstracker.mx:4040/api/webhooks/traccar", wh.toString(), new HashMap<>(),
                    5);
            return result;
        } catch (IOException | JSONException | StorageException e) {
            LOGGER.info("Sitrack proccess error");
            LOGGER.info(e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    public static String recursoConfiable(Position position, CacheManager cacheManager) {
        try {
            WebService ws = cacheManager.getStorage().getObject(WebService.class,
                    new Request(new Columns.All(), new Condition.Equals("tableName", "tc_dacero")));
            String identifier = "[" + generateRandomCode(10) + "] - ";
            LOGGER.info(identifier + "DAcero start");
            List<Event> evts = new ArrayList<>();

            Device dev = cacheManager.getObject(Device.class, position.getDeviceId());

            Event evt = null;
            evt = new Event();

            evt.setAltitude(String.valueOf(position.getAltitude()));
            evt.setAsset(ObjectUtils.firstNonNull(dev.getCarPlate(), ""));
            if (dev.getAttributes().containsKey("battery")) {
                evt.setBattery(String.valueOf(dev.getAttributes().getOrDefault("battery", "0")));
            }
            evt.setCode("");
            evt.setCourse(String.valueOf(position.getCourse()));
            // pendiente nombre e identificador
            Instant instant = position.getFixTime().toInstant();
            ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("UTC"));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(Date.from(zonedDateTime.toInstant()));
            evt.setDate(calendar);
            evt.setDirection(position.getAddress());
            if (position.getAttributes().containsKey("ignition")) {
                evt.setIgnition(String.valueOf(position.getAttributes().get("ignition")));
            }
            evt.setLatitude(String.valueOf(position.getLatitude()));
            evt.setLongitude(String.valueOf(position.getLongitude()));
            // pendiente odometro
            evt.setSpeed(String.valueOf(position.getSpeed()));
            // pendiente temperatura
            evts.add(evt);

            LOGGER.info(identifier + "DAcero Service start");
            LOGGER.info(identifier + evt.toString());
            Service service = new Service();

            BasicHttpBinding_IRCServiceStub client = new BasicHttpBinding_IRCServiceStub(
                    new URL("http://gps.rcontrol.com.mx/Tracking/wcf/RCService.svc?singleWsdl"), service);
            LOGGER.info(identifier + "Client " + client);
            String token = client.getUserToken(ws.getUser(), ws.getPassword()).getToken();
            LOGGER.info(identifier + "Token " + token);
            AppointResult[] res = client.GPSAssetTracking(token, evts.toArray(new Event[evts.size()]));
            LOGGER.info(identifier + "Resultado dacero");

            StringBuilder builder = new StringBuilder();
            builder.append("[");
            for (int i = 0; i < res.length; i++) {
                builder.append(res[i]);
                if (i < res.length - 1) {
                    builder.append(", ");
                }
            }
            builder.append("]");
            String arrayAsString = builder.toString();

            LOGGER.info(identifier + arrayAsString);
            JSONObject wh = new JSONObject();
            wh.put("webservice", evt.toString());
            wh.put("device", dev);
            GeneralUtils.genericPOST("https://crmgpstracker.mx:4040/api/webhooks/traccar", wh.toString(), new HashMap<>(),
                    5);
            return "";
        } catch (IOException | JSONException ex) {
            ex.printStackTrace();
        } catch (StorageException ex) {
            java.util.logging.Logger.getLogger(ExternalUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        LOGGER.info("DAcero something wrong");
        return "";
    }

    public static String lala(Position position, CacheManager cacheManager)
            throws AxisFault, SQLException, RemoteException, MalformedURLException, StorageException, IOException {
        try {
            WebService ws = cacheManager.getStorage().getObject(WebService.class,
                    new Request(new Columns.All(), new Condition.Equals("tableName", "tc_lala")));
            String code = generateRandomCode(10);
            LOGGER.info(String.format("[%s]WS Lala", code));
            Map<String, String> events_codes = new HashMap();
            events_codes.put(org.traccar.model.Event.TYPE_COMMAND_RESULT, "CR");
            events_codes.put(org.traccar.model.Event.TYPE_DEVICE_ONLINE, "DO");
            events_codes.put(org.traccar.model.Event.TYPE_DEVICE_UNKNOWN, "DU");
            events_codes.put(org.traccar.model.Event.TYPE_DEVICE_OFFLINE, "DF");
            events_codes.put(org.traccar.model.Event.TYPE_DEVICE_MOVING, "DM");
            events_codes.put(org.traccar.model.Event.TYPE_DEVICE_STOPPED, "DS");
            events_codes.put(org.traccar.model.Event.TYPE_DEVICE_OVERSPEED, "DOS");
            events_codes.put(org.traccar.model.Event.TYPE_DEVICE_FUEL_DROP, "DF");
            events_codes.put(org.traccar.model.Event.TYPE_GEOFENCE_ENTER, "DGI");
            events_codes.put(org.traccar.model.Event.TYPE_GEOFENCE_EXIT, "DGO");
            events_codes.put(org.traccar.model.Event.TYPE_ALARM, "A");
            events_codes.put(org.traccar.model.Event.TYPE_ALARM_MAX_TEMP, "ATU");
            events_codes.put(org.traccar.model.Event.TYPE_ALARM_MIN_TEMP, "ATD");
            events_codes.put(org.traccar.model.Event.TYPE_IGNITION_ON, "II");
            events_codes.put(org.traccar.model.Event.TYPE_IGNITION_OFF, "IO");
            events_codes.put(org.traccar.model.Event.TYPE_MAINTENANCE, "M");
            events_codes.put(org.traccar.model.Event.TYPE_TEXT_MESSAGE, "TM");
            events_codes.put(org.traccar.model.Event.TYPE_DRIVER_CHANGED, "DC");
            events_codes.put(org.traccar.model.Event.TYPE_ALARM_POWERCUT, "PC");
            events_codes.put(org.traccar.model.Event.TYPE_ALARM_OVERSPEED, "OS");

            Device device = cacheManager.getObject(Device.class, position.getDeviceId());
            ;

            List<PEvento> eventos = new ArrayList<>();
            PEvento pevento = new PEvento();
            pevento.setAltitud(position.getAltitude());
            pevento.setCodigo(events_codes.get(""));
            pevento.setDominio(device.getUniqueId());
            pevento.setFechaHoraEvento(toCalendar(position.getFixTime()));
            pevento.setFechaHoraRecepcion(toCalendar(position.getServerTime()));
            pevento.setLatitud(position.getLatitude());
            pevento.setLongitud(position.getLongitude());
            pevento.setNroSerie("-1");
            pevento.setVelocidad(position.getSpeed());
            LOGGER.info(String.format("[%s]WS Lala evento", code));
            LOGGER.info(String.format("[%s]WS Lala evento", code) + pevento.toString());
            PEvento[] evts = new PEvento[1];
            evts[0] = pevento;
            LOGGER.info(String.format("[%s]WS Lala evento", code) + Arrays.toString(eventos.toArray(new PEvento[0])));
            LOGGER.info(String.format("[%s]WS Lala evento", code) + Arrays.toString(evts));
            org.traccar.model.Event evento = null;
            List<org.traccar.model.Event> events = cacheManager.getStorage().getObjectsByQuery(
                    org.traccar.model.Event.class, "select * from tc_events where positionid = " + position.getId());
            if (!events.isEmpty()) {
                evento = events.get(0);
            }
            if (evento != null) {
                pevento.setCodigo(events_codes.getOrDefault(evento.getType(), "P"));
            } else {
                pevento.setCodigo("P");
            }

            Service service = new Service();

            ServiceSoapStub client = new ServiceSoapStub(
                    new URL("http://hub.unisolutions.com.ar/hub/unigis/Mapi/soap/gps/service.asmx?wsdl"), service);
            int[] response = client.loginYInsertarEventos(ws.getUser(), ws.getPassword(), evts);
            LOGGER.info(String.format("[%s]WS Lala response", code));
            LOGGER.info(String.format("[%s]: %s", code, Arrays.toString(response)));
            JSONObject wh = new JSONObject();
            wh.put("webservice", pevento.toString());
            wh.put("device", device);
            GeneralUtils.genericPOST("https://crmgpstracker.mx:4040/api/webhooks/traccar", wh.toString(), new HashMap<>(),
                    5);
            return Arrays.toString(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String thruster(Position position, CacheManager cacheManager)
            throws SQLException, IOException, StorageException {
        WebService ws = cacheManager.getStorage().getObject(WebService.class,
                new Request(new Columns.All(), new Condition.Equals("tableName", "tc_thruster")));
        JSONArray objs = new JSONArray();
        JSONObject obj = new JSONObject();
        Device device = cacheManager.getObject(Device.class, position.getDeviceId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        Map<String, Integer> events = new HashMap<String, Integer>();
        events.put(org.traccar.model.Event.TYPE_ALARM, 1);
        events.put(org.traccar.model.Event.TYPE_DEVICE_STOPPED, 2);
        events.put(org.traccar.model.Event.TYPE_IGNITION_OFF, 3);
        events.put(org.traccar.model.Event.TYPE_ALARM_OVERSPEED, 4);
        events.put(org.traccar.model.Event.TYPE_DEVICE_OVERSPEED, 4);
        events.put(org.traccar.model.Event.TYPE_DEVICE_MOVING, 5);
        events.put(org.traccar.model.Event.TYPE_IGNITION_ON, 6);

        org.traccar.model.Event evento = null;
        List<org.traccar.model.Event> eventos = cacheManager.getStorage().getObjectsByQuery(
                org.traccar.model.Event.class, "select * from tc_events where positionid = " + position.getId());
        if (!eventos.isEmpty()) {
            evento = eventos.get(0);
        }
        obj.put("imei", device.getUniqueId());
        if (evento != null && events.containsKey(evento.getType())) {
            obj.put("eventType", events.get(evento.getType()));
        }

        obj.put("plate", device.getCarPlate());
        obj.put("lat", String.valueOf(position.getLatitude()));
        obj.put("lon", String.valueOf(position.getLongitude()));
        obj.put("speed", String.valueOf(position.getSpeed()));
        obj.put("course", String.valueOf(position.getCourse()));
        obj.put("dTime", sdf.format(GeneralUtils.dateToUTC(position.getServerTime())));
        obj.put("address", position.getAddress());
        objs.put(obj);

        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", String.format("Basic %s", DataConverter
                .printBase64(String.format("%s:%s", ws.getUser(), ws.getPassword()).getBytes(StandardCharsets.UTF_8))));
        LOGGER.info("WS thruster");
        String result = GeneralUtils.genericPOST(
                "https://glmsgpstrackerapiserviacero.azurewebsites.net/api/GPSTrackerFunction?clientId="
                + ws.getExtra(),
                objs.toString(), headers, 5);
        LOGGER.info(obj.toString());

        JSONObject wh = new JSONObject();
        wh.put("webservice", objs.toString());
        wh.put("device", device);
        GeneralUtils.genericPOST("https://crmgpstracker.mx:4040/api/webhooks/traccar", wh.toString(), new HashMap<>(), 5);

        return result;
    }

    public static String nuevoWSGigaPegasus(CacheManager cacheManager, Position position) {

        try {
            WebService ws = cacheManager.getStorage().getObject(WebService.class,
                    new Request(new Columns.All(), new Condition.Equals("tableName", "tc_activetrack")));
            //System.out.println("Init giga new ws (pegasus)");
            String endpoint = "https://pegasus248.peginstances.com/receivers/json";
            String body;
            String bodyContent;
            Map<String, Object> headers = new HashMap<>();
            headers.putIfAbsent("Content-Type", "application/json");
            headers.putIfAbsent("Authenticate", ws.getUser());
            //proceso
            //requeridos
            bodyContent = "";
            String query = ""
                    + "SELECT pd.* "
                    + "FROM tc_positions_day pd "
                    + "INNER JOIN tc_positions p "
                    + "ON p.id=pd.id "
                    + "WHERE p.enviadows=FALSE and "
                    + "DATE(p.fixtime) = CURDATE()"
                    + "LIMIT 50";
            Collection<Position> positions = new ArrayList<>() {
                {
                    add(position);
                }
            };
            if (positions.size() <= 0) {
                return "";
            }
            boolean first = true;
            for (Position p : positions) {
                String temp = "";
                Device d = cacheManager.getObject(Device.class, p.getDeviceId());
                org.traccar.model.Event e = cacheManager.getStorage().getObject(org.traccar.model.Event.class, new Request(new Columns.All(), new Condition.Equals("positionId", p.getId())));

                try {
                    if (!first) {
                        temp += ",";
                    }
                    temp += "{";
                    temp += "\"device.id\": " + d.getUniqueId() + ",";
                    temp += "\"position.latitude\": " + p.getLatitude() + ",";
                    temp += "\"position.longitude\": " + p.getLongitude() + ",";
                    temp += "\"timestamp\": " + aDate2Epoch(p.getFixTime()) + ",";
                    temp += "\"protocol.id\":  \"rt.platform\"" + ",";
                    temp += "\"device.type.id\":  \"rt.platform\"" + ",";
                    temp += "\"device.name\": \"" + ObjectUtils.firstNonNull(d.getName(), "") + "\",";

                    if (e != null) {
                        if (e.getAttributes().containsValue("sos")) {
                            temp += "\"event.enum\": 1" + ",";
                        } else if (e.getAttributes().containsValue("powerOff")) {
                            temp += "\"event.enum\": 3" + ",";
                        } else if (e.getAttributes().containsValue("powerOn")) {
                            temp += "\"event.enum\": 6" + ",";
                        } else if (e.getAttributes().containsValue("overspeed")) {
                            temp += "\"event.enum\": 4" + ",";
                        } else if (e.getAttributes().containsKey("motion") && (boolean) e.getAttributes().get("motion")) {
                            temp += "\"event.enum\": 5" + ",";
                        } else {
                            temp += "\"event.enum\": 2" + ",";
                        }
                        temp += "\"event.label\": \"" + e.getType() + "\",";
                    }
                    temp += "\"position.direction\": " + p.getCourse() + ",";
                    temp += "\"position.speed\": " + p.getSpeed() / 0.539957 + ",";
                    temp += "\"position.altitude\": " + p.getAltitude() + ",";

                    if (p.getAttributes().containsKey("hdop")) {
                        temp += "\"position.hdop\": " + Double.valueOf(p.getAttributes().get("hdop").toString()) + ",";
                    }
                    if (p.getAttributes().containsKey("sat")) {
                        temp += "\"position.satellites\": " + (int) p.getAttributes().get("sat") + ",";
                    }
                    temp += "\"position.valid\": " + p.getValid() + ",";

                    if (p.getAttributes().containsKey("ignition") && (boolean) p.getAttributes().get("ignition")) {
                        temp += "\"io.ignition\": " + (boolean) p.getAttributes().get("ignition") + ",";
                    }
                    if (p.getAttributes().containsKey("battery")) {
                        temp += "\"device.battery.level\": " + Double.parseDouble(p.getAttributes().get("battery")
                                .toString()) * 1000 + ",";
                    }
                    if (p.getAttributes().containsKey("batteryLevel")) {
                        temp += "\"device.battery.percent\": " + p.getAttributes().get("batteryLevel") + ",";
                    }

                    if (p.getAttributes().containsKey("motion")) {
                        temp += "\"movement.status\": " + p.getAttributes().get("motion");
                    }

                    temp += "}";
                    first = false;

                } catch (Exception ee) {
                    temp = "";
                    ee.printStackTrace();
                }
                bodyContent += temp;
                p.setEnviadows(true);
                cacheManager.getStorage().updateObject(p, new Request(new Columns.Exclude("id"), new Condition.Equals("id", p.getId())));

            }

            body = ""
                    + "["
                    + bodyContent
                    + "]";

            //envio
            LOGGER.info("Pawload pegasus ws");
            LOGGER.info(body);
            String result = "";

            try {
                result = genericPOST(endpoint, body, headers, 5);
            } catch (IOException ex) {
                ex.printStackTrace();
                //System.out.println(ex);
            }
            LOGGER.info("WS Response");
            LOGGER.info(result);

            return body;
        } catch (StorageException ex) {
            java.util.logging.Logger.getLogger(GeneralUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public static Calendar toCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }
}
