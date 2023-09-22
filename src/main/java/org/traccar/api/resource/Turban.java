/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.api.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.traccar.api.BaseObjectResource;
import org.traccar.config.Config;
import org.traccar.model.Device;
import org.traccar.model.Group;
import org.traccar.model.Salida;
import org.traccar.model.Ticket;
import org.traccar.model.User;
import org.traccar.session.ConnectionManager;
import org.traccar.session.cache.CacheManager;
import org.traccar.storage.StorageException;
import org.traccar.storage.query.Columns;
import org.traccar.storage.query.Condition;
import org.traccar.storage.query.Request;

@Path("t-urban")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Turban extends BaseObjectResource<Device> {

    @Inject
    private Config config;

    @Inject
    private CacheManager cacheManager;

    @Inject
    private ConnectionManager connectionManager;

    public Turban() {
        super(Device.class);
    }

    @GET
    @PermitAll
    public Collection<Device> get(@QueryParam("deviceId") long deviceId) throws StorageException {
        Collection<Device> result = new ArrayList<>();
        if (deviceId > 0) {
            result = storage.getObjects(Device.class, new Request(new Columns.All(),new Condition.Equals("id", deviceId)));
        } else {
            result = storage.getObjects(Device.class, new Request(new Columns.All()));
        }
        return result;
    }
    
    @GET
    @Path("groups")
    @PermitAll
    public Collection<Group> groups() throws  StorageException {
        return storage.getObjects(Group.class, new Request(new Columns.All()));
    }
}
