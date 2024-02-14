/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.api.resource;

import java.util.Collection;
import java.util.Date;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.traccar.api.BaseObjectResource;
import org.traccar.config.Config;
import org.traccar.helper.LogAction;
import org.traccar.model.Excuse;
import org.traccar.session.ConnectionManager;
import org.traccar.session.cache.CacheManager;
import org.traccar.storage.StorageException;
import org.traccar.storage.query.Columns;
import org.traccar.storage.query.Request;

/**
 *
 * @author K
 */
@Path("excuses")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ExcusesResource extends BaseObjectResource<Excuse> {

    @Inject
    private Config config;

    @Inject
    private CacheManager cacheManager;

    @Inject
    private ConnectionManager connectionManager;

    public ExcusesResource() {
        super(Excuse.class);
    }

    @GET
    public Collection<Excuse> get() throws StorageException {
        return storage.getObjects(Excuse.class, new Request(new Columns.All()));
    }
    
    @POST
    public Response add(Excuse entity) throws StorageException {   
        entity.setAvailability(new Date());
        entity.setId(storage.addObject(entity, new Request(new Columns.Exclude("id"))));
        LogAction.create(getUserId(), entity);       
        return Response.ok(entity).build();
    }
}
