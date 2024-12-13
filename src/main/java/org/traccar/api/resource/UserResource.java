/*
 * Copyright 2015 - 2022 Anton Tananaev (anton@traccar.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar.api.resource;

import java.sql.SQLException;
import org.traccar.api.BaseObjectResource;
import org.traccar.config.Config;
import org.traccar.helper.LogAction;
import org.traccar.helper.model.UserUtil;
import org.traccar.model.ManagedUser;
import org.traccar.model.Permission;
import org.traccar.model.User;
import org.traccar.storage.StorageException;
import org.traccar.storage.query.Columns;
import org.traccar.storage.query.Condition;
import org.traccar.storage.query.Request;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import org.json.JSONObject;
import org.traccar.api.AsyncSocket;
import org.traccar.model.Device;
import org.traccar.model.ExtraMail;
import org.traccar.model.ExtraPhone;
import org.traccar.session.cache.CacheManager;

@Path("users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource extends BaseObjectResource<User> {

    @Inject
    private Config config;

    @Inject
    private CacheManager cacheManager;

    public UserResource() {
        super(User.class);
    }

    @GET
    public Collection<User> get(@QueryParam("userId") long userId) throws StorageException {
        if (userId > 0) {
            permissionsService.checkUser(getUserId(), userId);
            return storage.getObjects(baseClass, new Request(
                    new Columns.All(),
                    new Condition.Permission(User.class, userId, ManagedUser.class).excludeGroups()));
        } else if (permissionsService.notAdmin(getUserId())) {
            return storage.getObjects(baseClass, new Request(
                    new Columns.All(),
                    new Condition.Permission(User.class, getUserId(), ManagedUser.class).excludeGroups()));
        } else {
            List<User> usuarios = storage.getObjects(baseClass, new Request(new Columns.All()));

            for (User u : usuarios) {
                u.getAttributes().put("total_devices", cacheManager.getDevicesPerUser(u.getId()));
            }

            return usuarios;

        }
    }

    @Override
    @PermitAll
    @POST
    public Response add(User entity) throws StorageException {
        User currentUser = getUserId() > 0 ? permissionsService.getUser(getUserId()) : null;
        if (currentUser == null || !currentUser.getAdministrator()) {
            permissionsService.checkUserUpdate(getUserId(), new User(), entity);
            if (currentUser != null && currentUser.getUserLimit() != 0) {
                int userLimit = currentUser.getUserLimit();
                if (userLimit > 0) {
                    int userCount = storage.getObjects(baseClass, new Request(
                            new Columns.All(),
                            new Condition.Permission(User.class, getUserId(), ManagedUser.class).excludeGroups()))
                            .size();
                    if (userCount >= userLimit) {
                        throw new SecurityException("Manager user limit reached");
                    }
                }
            } else {
                if (!permissionsService.getServer().getRegistration()) {
                    throw new SecurityException("Registration disabled");
                }
                UserUtil.setUserDefaults(entity, config);
            }
        }

        if (UserUtil.isEmpty(storage)) {
            entity.setAdministrator(true);
        }

        entity.setId(storage.addObject(entity, new Request(new Columns.Exclude("id"))));
        storage.updateObject(entity, new Request(
                new Columns.Include("hashedPassword", "salt"),
                new Condition.Equals("id", entity.getId())));

        LogAction.create(getUserId(), entity);

        if (currentUser != null && currentUser.getUserLimit() != 0) {
            storage.addPermission(new Permission(User.class, getUserId(), ManagedUser.class, entity.getId()));
            LogAction.link(getUserId(), User.class, getUserId(), ManagedUser.class, entity.getId());
        }
        return Response.ok(entity).build();
    }

    @Path("{id}/mails")
    @GET
    public Collection<ExtraMail> getUserMails(@PathParam("id") long id) throws StorageException {

        return storage.getObjects(ExtraMail.class, new Request(
                new Columns.All(),
                new Condition.Equals("userid", id)
        ));
    }

    @Path("{id}/mails")
    @PUT
    public Collection<ExtraMail> setUserMail(ExtraMail entity) throws StorageException {
        storage.updateObject(entity, new Request(
                new Columns.All(),
                new Condition.Equals("id", entity.getId())
        ));
        return storage.getObjects(ExtraMail.class, new Request(
                new Columns.All(),
                new Condition.Equals("userid", entity.getId())
        ));
    }

    @Path("{id}/mails")
    @POST
    public Collection<ExtraMail> addUserMail(ExtraMail entity) throws StorageException {
        Object e = storage.addObject(entity, new Request(
                new Columns.All()
        ));
        return storage.getObjects(ExtraMail.class, new Request(
                new Columns.All(),
                new Condition.Equals("userid", entity.getUserid())
        ));
    }

    @Path("{id}/mails/{mail}")
    @DELETE
    public Collection<ExtraMail> removeUserMail(@PathParam("id") long id, @PathParam("id") String mail) throws StorageException {
        storage.removeObject(ExtraMail.class, new Request(new Columns.All(), new Condition.And(new Condition.Equals("userid", getUserId()), new Condition.Equals("email", mail))));
        return storage.getObjects(ExtraMail.class, new Request(
                new Columns.All(),
                new Condition.Equals("userid", getUserId())
        ));
    }

    @Path("{id}/phones")
    @GET
    public Collection<ExtraPhone> getUserPhones(@PathParam("id") long id) throws StorageException {

        return storage.getObjects(ExtraPhone.class, new Request(
                new Columns.All(),
                new Condition.Equals("userid", id)
        ));
    }

    @Path("{id}/phones")
    @PUT
    public Collection<ExtraPhone> setUserPhone(ExtraPhone entity) throws StorageException {
        storage.updateObject(entity, new Request(
                new Columns.All(),
                new Condition.Equals("id", entity.getId())
        ));
        return storage.getObjects(ExtraPhone.class, new Request(
                new Columns.All(),
                new Condition.Equals("userid", entity.getId())
        ));
    }

    @Path("{id}/phones")
    @POST
    public Collection<ExtraPhone> addUserPhone(ExtraPhone entity) throws StorageException {
        Object e = storage.addObject(entity, new Request(
                new Columns.All()
        ));
        return storage.getObjects(ExtraPhone.class, new Request(
                new Columns.All(),
                new Condition.Equals("userid", entity.getUserid())
        ));
    }

    @Path("{id}/phones/{phone}")
    @DELETE
    public Collection<ExtraPhone> removeUserPhone(@PathParam("id") long id, @PathParam("id") String phone) throws StorageException {
        storage.removeObject(ExtraPhone.class, new Request(new Columns.All(), new Condition.And(new Condition.Equals("userid", getUserId()), new Condition.Equals("phone", phone))));
        return storage.getObjects(ExtraPhone.class, new Request(
                new Columns.All(),
                new Condition.Equals("userid", getUserId())
        ));
    }

    @Path("main")
    @GET
    public Collection<User> getMainUsers(@QueryParam("userId") long userId) throws SQLException, StorageException {
        permissionsService.checkAdmin(getUserId());
        return storage.getObjects(baseClass, new Request(new Columns.All(), new Condition.Equals("main", true)));
    }

    @Path("{id}/test")
    @PermitAll
    @GET
    public Response testById(@PathParam("id") long id) {
//        System.out.println("dev > test");
        JSONObject obj = new JSONObject();
        obj.put("test", "xdxdxd");
        System.out.println(obj.toString());
        if (cacheManager.getSocketsLogged().get(id) != null) {
            for (AsyncSocket soc : cacheManager.getSocketsLogged().get(id)) {
                soc.onUpdateCustom(obj);
            }
        }
        return Response.ok("").build();
    }

    @Path("{id}/debt")
    @PermitAll
    @GET
    public Response markInDebt(@PathParam("id") long id) throws StorageException {
        setUserDebt(id, true);
        System.out.println("debt "+id);
        var conditions = new LinkedList<Condition>();
        conditions.add(new Condition.Permission(User.class, id, Device.class));
        List<Long> devices = storage.getObjects(Device.class, new Request(new Columns.All(), Condition.merge(conditions))).stream().map((device) -> device.getId()).collect(Collectors.toList());
        System.out.println("devices");
        System.out.println(devices);
        
        storage.getPermissions(User.class, Device.class).stream().filter((permission) -> devices.indexOf(permission.getPropertyId()) >= 0).forEach((permission) -> {
            try {
                System.out.println("found user "+permission);
                setUserDebt(permission.getOwnerId(), true);
            } catch (StorageException ex) {
                Logger.getLogger(UserResource.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        return Response.ok("").build();
    }

    @Path("{id}/undebt")
    @PermitAll
    @GET
    public Response removeFromDebt(@PathParam("id") long id) throws StorageException {
        setUserDebt(id, false);
        
        var conditions = new LinkedList<Condition>();
        conditions.add(new Condition.Permission(User.class, id, Device.class));
        List<Long> devices = storage.getObjects(Device.class, new Request(new Columns.All(), Condition.merge(conditions))).stream().map((device) -> device.getId()).collect(Collectors.toList());

        storage.getPermissions(User.class, Device.class).stream().filter((permission) -> devices.indexOf(permission.getPropertyId()) >= 0).forEach((permission) -> {
            try {
                setUserDebt(permission.getOwnerId(), false);
            } catch (StorageException ex) {
                Logger.getLogger(UserResource.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        return Response.ok("").build();
    }

    private void setUserDebt(Long userId, boolean debt) throws StorageException {
        User user = storage.getObject(User.class, new Request(new Columns.All(), new Condition.Equals("id", userId)));
        if (user != null) {
            user.setDebt(debt);
            storage.updateObject(user, new Request(
                    new Columns.Exclude("id"),
                    new Condition.Equals("id", user.getId())));
        }
        if (cacheManager.getSocketsLogged().get(userId) != null) {
            JSONObject obj = new JSONObject();
            obj.put("command", "refreshUser");
            for (AsyncSocket soc : cacheManager.getSocketsLogged().get(userId)) {
                if (soc != null) {
                    soc.onUpdateCustom(obj);
                }

            }
        }
    }
}
