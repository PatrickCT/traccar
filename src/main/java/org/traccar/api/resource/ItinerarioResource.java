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
import org.traccar.model.Itinerario;
import org.traccar.storage.StorageException;
import org.traccar.storage.query.Columns;
import org.traccar.storage.query.Condition;
import org.traccar.storage.query.Request;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.hk2.utilities.general.GeneralUtilities;
import org.traccar.api.SimpleObjectResource;
import org.traccar.api.security.ServiceAccountUser;
import org.traccar.helper.LogAction;
import org.traccar.model.Group;
import org.traccar.model.Subroute;
import org.traccar.model.User;
import org.traccar.session.ConnectionManager;
import org.traccar.session.cache.CacheManager;
import org.traccar.utils.GenericUtils;

/**
 *
 * @author K
 */
@Path("itinerarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ItinerarioResource extends SimpleObjectResource<Itinerario> {

    @Inject
    private CacheManager cacheManager;

    @Inject
    private ConnectionManager connectionManager;

    public ItinerarioResource() {
        super(Itinerario.class);
    }

    @GET
    public Collection<Itinerario> get(
            @QueryParam("all") boolean all, @QueryParam("userId") long userId) throws StorageException {
        Collection<Itinerario> result = new ArrayList<>();
        var conditions = new LinkedList<Condition>();
        List<Long> grupos = storage.getPermissions(User.class, Group.class).stream().filter((item) -> item.getOwnerId() == getUserId()).map((item) -> item.getPropertyId()).collect(Collectors.toList());
        conditions.clear();
        List<Long> subrutas = new ArrayList<>();
        grupos.forEach(id -> {
            try {
                subrutas.addAll(storage.getObjects(Subroute.class, new Request(new Columns.All(), new Condition.Equals("groupId", id))).stream().map(item ->item.getId()).collect(Collectors.toList()));
            } catch (StorageException ex) {
                ex.printStackTrace();
                Logger.getLogger(ItinerarioResource.class.getName()).log(Level.SEVERE, null, ex);
            }
        });        
        conditions.clear();
        subrutas.forEach(id -> {
            try {
                result.addAll(storage.getObjects(baseClass, new Request(new Columns.All(), new Condition.Equals("subrouteId", id))));
            } catch (StorageException ex) {
                ex.printStackTrace();
                Logger.getLogger(ItinerarioResource.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        return result;
    }

    @POST
    public Response add(Itinerario entity) throws StorageException {
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
