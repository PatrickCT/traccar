/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.utils;

import java.io.IOException;
import java.util.HashMap;
import javax.inject.Inject;
import org.json.JSONObject;
import org.traccar.model.Device;
import org.traccar.model.Position;
import org.traccar.session.cache.CacheManager;

/**
 *
 * @author K
 */
public class TUrbanUtils {
    @Inject
    private CacheManager cacheManager;
    
    public String positionSend(Position position) throws IOException {        
        JSONObject obj = new JSONObject();
        Device device = cacheManager.getObject(Device.class, position.getDeviceId());
        obj.put("imei", device.getUniqueId());
        obj.put("lattitude", String.valueOf(position.getLatitude()));
        obj.put("longitude", String.valueOf(position.getLongitude()));
        obj.put("angle", String.valueOf(position.getCourse()));
        obj.put("speed", String.valueOf(position.getSpeed() * 1.852));


        String result = GenericUtils.genericPOST("http://localhost:4050/", obj.toString(), new HashMap<>());
        return result;
    }
}
