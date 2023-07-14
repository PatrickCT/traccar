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
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.traccar.api.BaseObjectResource;
import org.traccar.api.SimpleObjectResource;
import org.traccar.config.Config;
import org.traccar.model.Device;
import org.traccar.model.Salida;
import org.traccar.model.Ticket;
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
@Path("tickets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TicketsResource extends BaseObjectResource<Ticket> {

    @Inject
    private Config config;

    @Inject
    private CacheManager cacheManager;

    @Inject
    private ConnectionManager connectionManager;

    public TicketsResource() {
        super(Ticket.class);
    }

    @GET
    public Collection<Ticket> get(
            @QueryParam("all") boolean all,
            @QueryParam("finished") boolean finished,
            @QueryParam("deviceId") long deviceId) throws StorageException {

        Collection<Ticket> result = new ArrayList<>();
        Collection<Long> salidas = new ArrayList<>();
        if (deviceId != 0) {
            List<Long> devices = new ArrayList<>() {
                {
                    add(deviceId);
                }
            };
            devices.forEach(id -> {
                try {
                    salidas.addAll(storage.getObjects(Salida.class, new Request(new Columns.All(), new Condition.Equals("deviceId", id))).stream().map(item -> item.getId()).collect(Collectors.toList()));
                } catch (StorageException ex) {
                    Logger.getLogger(ItinerarioResource.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            salidas.forEach(id -> {
                try {
                    result.addAll(storage.getObjects(baseClass, new Request(new Columns.All(), new Condition.Equals("salidaId", id))));
                } catch (Exception e) {
                }
            });
        } else {
            List<Long> devices = storage.getPermissions(User.class, Device.class).stream().map((item) -> item.getPropertyId()).collect(Collectors.toList());
            devices.forEach(id -> {
                try {
                    salidas.addAll(storage.getObjects(Salida.class, new Request(new Columns.All(), new Condition.Equals("deviceId", id))).stream().map(item -> item.getId()).collect(Collectors.toList()));
                } catch (StorageException ex) {
                    Logger.getLogger(ItinerarioResource.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            salidas.forEach(id -> {
                try {
                    result.addAll(storage.getObjects(baseClass, new Request(new Columns.All(), new Condition.Equals("salidaId", id))));
                } catch (Exception e) {
                }
            });
        }
        return result;

    }
}
