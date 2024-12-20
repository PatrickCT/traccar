/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.api.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONObject;
import org.traccar.api.BaseObjectResource;
import org.traccar.api.BaseResource;
import org.traccar.api.security.ServiceAccountUser;
import org.traccar.helper.LogAction;
import org.traccar.model.Itinerario;
import org.traccar.model.Password;
import org.traccar.model.SubUser;
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
@Path("subusers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SubUserResource extends BaseObjectResource<SubUser> {

    @Inject
    private CacheManager cacheManager;

    @Inject
    private ConnectionManager connectionManager;

    public SubUserResource() {
        super(SubUser.class);
    }

    @GET
    public Collection<SubUser> get() throws StorageException {
        return storage.getObjectsByQuery(SubUser.class, "select * from tc_subusers where user = " + getUserId());
    }

    @Path("get/{user}")
    @GET
    public Collection<SubUser> list(@PathParam("user") Long user) throws StorageException {
        System.out.println("user "+user);
        return storage.getObjectsByQuery(SubUser.class, "select * from tc_subusers where user = " + user);
    }
    
    @POST
    public Response add(SubUser entity) throws StorageException {
        entity.setId(storage.addObject(entity, new Request(new Columns.Exclude("id"))));
        LogAction.create(getUserId(), entity);
        return Response.ok(entity).build();
    }
    
    @PUT
    public Response update(SubUser entity) throws StorageException {
        storage.updateObject(entity, new Request(
                new Columns.Exclude("id"),
                new Condition.Equals("id", entity.getId())));
        return Response.ok(entity).build();
    }
}
