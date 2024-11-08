/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.api.resource;

import java.sql.SQLException;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.traccar.api.BaseResource;
import org.traccar.session.cache.CacheManager;

/**
 *
 * @author USER
 */
@Path("GetGPSEvents")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GPSEventsResource extends BaseResource{
    
    @Inject
    private CacheManager cacheManager;
    
    @GET
    public Response getJson() throws SQLException{
        return Response.ok().build();
    }
}
