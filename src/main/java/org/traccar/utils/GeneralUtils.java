/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.utils;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.LoggerFactory;
import org.traccar.model.Device;
import org.traccar.model.Event;
import org.traccar.model.Position;
import org.traccar.session.cache.CacheManager;
import org.traccar.storage.StorageException;
import org.traccar.storage.query.Columns;
import org.traccar.storage.query.Condition;
import org.traccar.storage.query.Request;
import static org.traccar.utils.GenericUtils.aDate2Epoch;
import static org.traccar.utils.GenericUtils.addTimeToDate;

/**
 *
 * @author USER
 */
public class GeneralUtils {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(GeneralUtils.class);

    public static Date dateToUTC(Date date) {
        return new Date(date.getTime() - Calendar.getInstance().getTimeZone().getOffset(date.getTime()));
    }

    public String nuevoWSGigaPegasus(CacheManager cacheManager) {

        try {

            //System.out.println("Init giga new ws (pegasus)");
            String endpoint = "https://pegasus248.peginstances.com/receivers/json";
            String body;
            String bodyContent;
            Map<String, Object> headers = new HashMap<>();
            headers.putIfAbsent("Content-Type", "application/json");
            headers.putIfAbsent("Authenticate", "3b42af9ff05544e8c821bae1721b70e3a63fbca6d8e91f41f1da3006");
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
            Collection<Position> positions = cacheManager.getStorage().getObjectsByQuery(Position.class, query);
            if (positions.size() <= 0) {
                return "";
            }
            boolean first = true;
            for (Position p : positions) {
                String temp = "";
                Device d = cacheManager.getObject(Device.class, p.getDeviceId());
                Event e = cacheManager.getStorage().getObject(Event.class, new Request(new Columns.All(), new Condition.Equals("positionId", p.getId())));
                LOG.info(
                        String.format("Time fix ws: "
                                + "original time %s "
                                + "as epoch %s, "
                                + "fixed time %s "
                                + "as epoch %s, "
                                + "Device: %s, "
                                + "Send at %s",
                                p.getFixTime(),
                                aDate2Epoch(p.getFixTime()),
                                addTimeToDate(p.getFixTime(), Calendar.HOUR_OF_DAY, 5),
                                aDate2Epoch(addTimeToDate(p.getFixTime(), Calendar.HOUR_OF_DAY, 5)),
                                d.getName(),
                                new Date()
                        )
                );
                try {
                    if (!first) {
                        temp += ",";
                    }
                    temp += "{";
                    temp += "\"device.id\": " + d.getUniqueId() + ",";
                    temp += "\"position.latitude\": " + p.getLatitude() + ",";
                    temp += "\"position.longitude\": " + p.getLongitude() + ",";
//                temp += "\"timestamp\": " + aDate2Epoch(addTimeToDate(p.getFixTime(), Calendar.HOUR_OF_DAY, 5)) + ",";
                    temp += "\"timestamp\": " + aDate2Epoch(p.getFixTime()) + ",";
                    temp += "\"protocol.id\":  \"rt.platform\"" + ",";
                    temp += "\"device.type.id\":  \"rt.platform\"" + ",";
//opcionales
//position event common
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

//position info
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

//input
                    if (p.getAttributes().containsKey("ignition") && (boolean) p.getAttributes().get("ignition")) {
                        temp += "\"io.ignition\": " + (boolean) p.getAttributes().get("ignition") + ",";
                    }
//device
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
            LOG.info("Pawload pegasus ws");
            LOG.info(body);
            //System.out.println("payload pegasus ws");
            //System.out.println(body);
            String result = "";

            //System.out.println("making post to " + endpoint);
            try {
                result = genericPOST(endpoint, body, headers, 5);
            } catch (IOException ex) {
                ex.printStackTrace();
                //System.out.println(ex);
            }
            LOG.info("WS Response");
            LOG.info(result);

            return body;
        } catch (StorageException ex) {
            Logger.getLogger(GeneralUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public static String genericPOST(String url, String json, Map<String, Object> headers, int timeoutInSeconds) throws IOException {
        try {
            // Configure request timeout
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(Timeout.ofSeconds(timeoutInSeconds))
                    .setConnectionRequestTimeout(Timeout.ofSeconds(timeoutInSeconds))
                    .build();

            CloseableHttpClient client = HttpClients.custom()
                    .setDefaultRequestConfig(requestConfig)
                    .build();

            HttpPost httpPost = new HttpPost(url);
            StringEntity entity = new StringEntity(json);
            httpPost.setEntity(entity);

            if (headers.isEmpty()) {
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");
            } else {
                headers.forEach(httpPost::addHeader);
            }

            CloseableHttpResponse response = client.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            String responseString = EntityUtils.toString(responseEntity, "UTF-8");
            LOG.info("Generic post to " + url + " response");
            LOG.info(responseString);
            client.close();

            return responseString;
        } catch (org.apache.hc.core5.http.ParseException ex) {
            // Handle exception
            System.out.println("error generic post");
            System.out.println(ex.getMessage());
            System.out.println(ex);
        }
        return "";
    }

}
