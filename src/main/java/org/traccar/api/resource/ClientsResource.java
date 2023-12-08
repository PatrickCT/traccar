/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.api.resource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.jetty.util.resource.Resource;
import org.json.JSONObject;
import org.traccar.api.AsyncSocket;
import org.traccar.api.BaseResource;
import org.traccar.config.Config;
import org.traccar.model.User;
import org.traccar.session.cache.CacheManager;

/**
 *
 * @author K
 */
@Path("clients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClientsResource extends BaseResource {

    @Inject
    private Config config;

    @Inject
    private CacheManager cacheManager;

    @Path("list")
    @PermitAll
    @GET
    public Response list() {
        List<JSONObject> clients = new ArrayList<>();
        cacheManager.getSocketsLogged().forEach((id, socketList) -> {

            Iterator<AsyncSocket> iterator = socketList.iterator();
            while (iterator.hasNext()) {
                AsyncSocket socket = iterator.next();
                if (!socket.isConnected()) {
                    iterator.remove();
                }
            }

            // Continue processing if there are still connected sockets
            if (!socketList.isEmpty()) {
                List<JSONObject> connections = new ArrayList<>();
                for (int i = 0; i < socketList.size(); i++) {
                    AsyncSocket socket = socketList.get(i);                    
                    JSONObject socketInfo = new JSONObject();
                    socketInfo.put("id", i + 1);
                    socketInfo.put("ip", socket.getRemote().getRemoteAddress());
                    // Add other information as needed (e.g., browser type)

                    connections.add(socketInfo);
                }
                
                User user = ((User) cacheManager.getObject(User.class, id));
                String name = user != null ? user.getName() : "Not found";
                

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", id);
                jsonObject.put("name", name);
                jsonObject.put("connections", connections);
                clients.add(jsonObject);
            }

        });
        return Response.ok(clients.toString()).build();
    }
}
