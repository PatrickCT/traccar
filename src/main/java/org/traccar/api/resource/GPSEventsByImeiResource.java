/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.api.resource;

import java.sql.SQLException;
import java.util.Collection;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import org.traccar.api.BaseResource;
import org.traccar.helper.DateUtil;
import org.traccar.model.GPSEvents;
import org.traccar.storage.StorageException;

/**
 *
 * @author USER
 */
public class GPSEventsByImeiResource extends BaseResource {

    @GET
    public Collection<GPSEvents> getJson(
            @QueryParam("imei") String imei,
            @QueryParam("from") String from,
            @QueryParam("to") String to
    ) throws SQLException, StorageException {
        if (from != null && to != null & !from.isEmpty() & !to.isEmpty()) {
            String query = String.format(""
                    + "SELECT \n"
                    + "        d.uniqueid AS imei,\n"
                    + "        IF(\n"
                    + "        e.attributes LIKE '%sos%',\n"
                    + "        1,\n"
                    + "        IF(\n"
                    + "        e.attributes LIKE '%powerOff%',\n"
                    + "        3,\n"
                    + "        IF(\n"
                    + "        e.attributes LIKE '%powerOn%',\n"
                    + "        6,\n"
                    + "        IF(\n"
                    + "        e.attributes LIKE '%overspeed%',\n"
                    + "        4,\n"
                    + "        IF(\n"
                    + "        p.attributes LIKE '%\"motion\":true%',\n"
                    + "        5,\n"
                    + "        2\n"
                    + "        )\n"
                    + "        )\n"
                    + "        )\n"
                    + "        )\n"
                    + "        ) AS eventtype,\n"
                    + "        p.fixtime AS dtime,\n"
                    + "        p.latitude AS lat,\n"
                    + "        p.longitude AS lon,\n"
                    + "        p.speed/0.539957 AS speed,\n"
                    + "        '' AS address,\n"
                    + "        d.carPlate AS plate,\n"
                    + "        d.name AS alias,\n"
                    + "        p.course AS course,\n"
                    + "        p.attributes AS positionattributes,\n"
                    + "        e.type AS evtype,\n"
                    + "        e.attributes AS eventattributes,\n"
                    + "        g.name AS geofence\n"
                    + "        FROM\n"
                    + "        tc_positions_day p \n"
                    + "        INNER JOIN tc_devices d \n"
                    + "        ON d.id = p.deviceid \n"
                    + "        INNER JOIN tc_user_device ud \n"
                    + "        ON ud.deviceid = d.id \n"
                    + "        INNER JOIN tc_users u \n"
                    + "        ON u.id = ud.userid \n"
                    + "        LEFT JOIN tc_events e \n"
                    + "        ON e.positionid = p.id \n"
                    + "        LEFT JOIN tc_geofences g \n"
                    + "        ON g.id = e.geofenceid \n"
                    + "        WHERE d.disabled IS FALSE \n"
                    + "        AND d.uniqueid = %s   \n"
                    + "        AND p.fixtime BETWEEN %s\n"
                    + "        AND %s", imei, from, to);
            return storage.getObjectsByQuery(GPSEvents.class, query);
        } else {
            return null;
        }
    }

}
