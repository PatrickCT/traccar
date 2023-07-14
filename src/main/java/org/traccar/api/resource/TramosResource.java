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
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.traccar.api.SimpleObjectResource;
import org.traccar.api.security.ServiceAccountUser;
import org.traccar.config.Config;
import org.traccar.helper.LogAction;
import org.traccar.model.Geofence;
import org.traccar.model.Group;
import org.traccar.model.Itinerario;
import org.traccar.model.Permission;
import org.traccar.model.Subroute;
import org.traccar.model.Tramo;
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
@Path("tramos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TramosResource extends SimpleObjectResource<Tramo> {

    @Inject
    private Config config;

    @Inject
    private CacheManager cacheManager;

    @Inject
    private ConnectionManager connectionManager;

    public TramosResource() {
        super(Tramo.class);
    }

    @GET
    public Collection<Tramo> get(
            @QueryParam("all") boolean all, @QueryParam("scheduleId") long scheduleId) throws StorageException {

        Collection<Tramo> result = new ArrayList<>();
        Collection<Geofence> geocercas = new ArrayList<>();
        if (scheduleId != 0) {
            result.addAll(storage.getObjects(baseClass, new Request(new Columns.All(), new Condition.Permission(Itinerario.class, scheduleId, baseClass))));
        } else {
            geocercas.addAll(storage.getObjects(Geofence.class, new Request(new Columns.All(), new Condition.Permission(User.class, getUserId(), Geofence.class))));
            geocercas.forEach(item -> {
                try {
                    result.addAll(storage.getObjects(baseClass, new Request(new Columns.All(), new Condition.Equals("geofenceId", item.getId()))));
                } catch (StorageException ex) {
                    Logger.getLogger(ItinerarioResource.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }

        return result;

    }

    @POST
    public Response add(Tramo entity) throws StorageException {
        permissionsService.checkEdit(getUserId(), entity, true);

        entity.setId(storage.addObject(entity, new Request(new Columns.Exclude("id"))));
        LogAction.create(getUserId(), entity);

        if (getUserId() != ServiceAccountUser.ID) {
            cacheManager.invalidatePermission(true, User.class, getUserId(), baseClass, entity.getId());
            connectionManager.invalidatePermission(true, User.class, getUserId(), baseClass, entity.getId());
            LogAction.link(getUserId(), User.class, getUserId(), baseClass, entity.getId());
        }

        return Response.ok(entity).build();
    }
}
