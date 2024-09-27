/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.api.resource;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.traccar.api.BaseObjectResource;
import org.traccar.model.Device;
import org.traccar.model.Link;
import org.traccar.model.Position;
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
@Path("links")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LinkResource extends BaseObjectResource<Link> {

    @javax.ws.rs.core.Context
    private HttpServletRequest request;

    @Inject
    private CacheManager cacheManager;

    @Inject
    private ConnectionManager connectionManager;

    public LinkResource() {
        super(Link.class);
    }

    @GET
    public Collection<Link> get() {
        try {
            return cacheManager.getStorage().getObjectsByQuery(Link.class, "select * from tc_links where userid=" + String.valueOf(getUserId()));
        } catch (StorageException ex) {
            Logger.getLogger(LinkResource.class.getName()).log(Level.SEVERE, null, ex);
        }

        return new ArrayList<Link>();
    }

    @POST
    public Response add(Link entity) throws StorageException {
        entity.setUserId((int) getUserId());
        entity.setCode(RandomStringUtils.random(15, true, true));
        entity.setId(storage.addObject(entity, new Request(new Columns.Exclude("id"))));
        return Response.ok(entity).build();
    }

    @Path("{id}")
    @PUT
    public Response update(Link entity) throws StorageException {
        if (getUserId() != entity.getUserId()) {
            return Response.notModified().build();
        }
        storage.updateObject(entity, new Request(
                new Columns.Exclude("id"),
                new Condition.Equals("id", entity.getId())));
        return Response.ok(entity).build();
    }
    
    @Path("delete/{id}")
    @DELETE
    public Response deleteLink(@PathParam("id") long linkId) throws StorageException {
        Link entity = storage.getObject(Link.class, new Request(new Columns.All(), new Condition.Equals("id", linkId)));
        if (getUserId() != entity.getUserId()) {
            return Response.notModified().build();
        }
        storage.removeObject(Link.class, new Request(                
                new Condition.Equals("id", entity.getId())));        
        return Response.ok(entity).build();
    }

    @GET
    @Path("myLinks")
    public String getMyLinks() {
        try {

            JSONObject obj = new JSONObject("{}");
            obj.put("data", cacheManager.getStorage().getObjectsByQuery(Link.class, "select * from tc_links where userid = " + String.valueOf(getUserId())));

            return obj.toString();
        } catch (StorageException ex) {
            Logger.getLogger(LinkResource.class.getName()).log(Level.SEVERE, null, ex);
        }

        return "Fail";
    }

    @Path("{id}/devices")
    @GET
    public Collection<Device> getDevices(@PathParam("id") long id
    ) throws SQLException, StorageException {
        List<Device> devices = new ArrayList<>();
        return cacheManager.getStorage().getObjectsByQuery(Device.class, "select * from tc_devices d "
                + "inner join tc_link_device ld "
                + "on ld.deviceid=d.id "
                + "where ld.linkid = " + String.valueOf(id));

    }

    @Path("verify")
    @POST
    @PermitAll
    public Response verify(String body) throws IOException, ServletException {
        JSONObject obj = new JSONObject(body);
        if (obj.has("code")) {
            try {
                List<Link> links = cacheManager.getStorage().getObjectsByQuery(Link.class, String.format("select * from tc_links "
                        + "where code = '%s' "
                        + "and enabled "
                        + "and limitDate >= NOW()", obj.getString("code")));

                if (links.size() <= 0) {
                    return Response.status(Response.Status.FORBIDDEN).build();
                }

                request.setAttribute("guestUserId", links.get(0).getUserId());
                request.setAttribute("guestLinkId", links.get(0).getId());
                request.getSession().setAttribute("guestUserId", links.get(0).getUserId());
                request.getSession().setAttribute("guestLinkId", links.get(0).getId());

                JSONObject responseJson = new JSONObject();
                responseJson.put("userId", links.get(0).getUserId());
                responseJson.put("linkId", links.get(0).getId());
                responseJson.put("pass", links.get(0).getPass());
                responseJson.put("code", links.get(0).getCode());

                return Response.status(Response.Status.OK).entity(responseJson.toString()).build();

            } catch (StorageException ex) {
                Logger.getLogger(LinkResource.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        return Response.status(Response.Status.FORBIDDEN).build();
    }

    @Path("positions")
    @POST
    @PermitAll
    public Response positions(String body) {
        JSONObject obj = new JSONObject(body);
        if (obj.has("code")) {
            try {

                List<Link> links = cacheManager.getStorage().getObjectsByQuery(Link.class, String.format("select * from tc_links "
                        + "where code = '%s' "
                        + "and enabled "
                        + "and limitDate >= NOW()", obj.getString("code")));
                if (links.size() <= 0) {
                    return Response.status(Response.Status.FORBIDDEN).build();
                }
                Link link = links.get(0);
                String sql = String.format("SELECT DISTINCT p.* "
                        + "FROM tc_positions p "
                        + "INNER JOIN tc_devices d "
                        + "ON p.id=d.positionid "
                        + "INNER JOIN tc_link_device ld "
                        + "ON ld.deviceid=d.id "
                        + "WHERE ld.linkid=%s", link.getId());
                List<Position> result = cacheManager.getStorage().getObjectsByQuery(Position.class, sql);

                for (Position p : result) {
                    p.getAttributes()
                            .put(
                                    "deviceName",
                                    cacheManager.getObject(Device.class, p.getDeviceId()).getName());
                }

                return Response.status(Response.Status.OK).entity(result).build();

            } catch (StorageException ex) {
                Logger.getLogger(LinkResource.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        return Response.status(Response.Status.FORBIDDEN).build();
    }

    @Path("validate")
    @POST
    @PermitAll
    public Response validate(String body) {
        JSONObject obj = new JSONObject(body);
        if (obj.has("code")) {
            try {

                List<Link> links = cacheManager.getStorage().getObjectsByQuery(Link.class, String.format("select * from tc_links "
                        + "where code = '%s' "
                        + "and enabled "
                        + "and limitDate >= NOW()", obj.getString("code")));
                if (links.size() <= 0) {
                    return Response.status(Response.Status.FORBIDDEN).build();
                }

                return Response.status(Response.Status.OK).build();

            } catch (StorageException ex) {
                Logger.getLogger(LinkResource.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        return Response.status(Response.Status.FORBIDDEN).build();
    }
}
