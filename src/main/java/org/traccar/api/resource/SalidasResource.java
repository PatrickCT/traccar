/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.api.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.traccar.api.BaseObjectResource;
import org.traccar.model.Device;
import org.traccar.model.Group;
import org.traccar.model.Salida;
import org.traccar.model.Subroute;
import org.traccar.model.User;
import org.traccar.storage.StorageException;
import org.traccar.storage.query.Columns;
import org.traccar.storage.query.Condition;
import org.traccar.storage.query.Request;

/**
 *
 * @author K
 */
@Path("salidas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SalidasResource extends BaseObjectResource<Salida>{
    
    public SalidasResource() {
        super(Salida.class);
    }
    
    @GET
    public Collection<Salida> get(
            @QueryParam("all") boolean all, @QueryParam("finished") boolean finished) throws StorageException {

        Collection<Salida> result = new ArrayList<>();
        List<Long> devices = storage.getPermissions(User.class, Device.class).stream().map((item) -> item.getPropertyId()).collect(Collectors.toList());
        devices.forEach(id -> {
            try {
                result.addAll(storage.getObjects(baseClass, new Request(new Columns.All(), new Condition.Equals("deviceId", id))));
            } catch (StorageException ex) {
                Logger.getLogger(ItinerarioResource.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        return all? result : result.stream().filter(item -> item.getFinished() == finished).collect(Collectors.toList());

    }
    
}
