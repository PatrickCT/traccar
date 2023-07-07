/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.api.resource;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
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
import org.traccar.model.ManagedUser;
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
@Path("subroutes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SubrouteResource extends BaseObjectResource<Subroute> {

    @Inject
    private Config config;

    public SubrouteResource() {
        super(Subroute.class);
    }

    @GET
    public Collection<Subroute> get(
            @QueryParam("all") boolean all, @QueryParam("userId") long userId) throws StorageException {

        var conditions = new LinkedList<Condition>();

        if (all) {
            if (permissionsService.notAdmin(getUserId())) {
                conditions.add(new Condition.Permission(User.class, getUserId(), baseClass));
            }
        } else {
            if (userId == 0) {
                conditions.add(new Condition.Permission(User.class, getUserId(), baseClass));
            } else {
                permissionsService.checkUser(getUserId(), userId);
                conditions.add(new Condition.Permission(User.class, userId, baseClass).excludeGroups());
            }
        }

        return storage.getObjects(baseClass, new Request(new Columns.All(), Condition.merge(conditions)));

    }

}
