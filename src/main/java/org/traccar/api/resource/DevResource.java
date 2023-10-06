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
import org.traccar.api.BaseResource;
import org.traccar.model.Device;
import org.traccar.model.Event;
import org.traccar.model.Position;
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
@Path("dev")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DevResource extends BaseResource{
    @Inject
    private CacheManager cacheManager;
    
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
                if(pos == null){
                    pos = cacheManager.getObject(Position.class, dev.getPositionId());
                    if(pos == null){
                        pos = storage.getObject(Position.class, new Request(new Columns.All(), new Condition.Equals("id", dev.getPositionId())));
                    }
                }
                if(pos != null){
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

}
