/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.api.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.traccar.api.BaseObjectResource;
import org.traccar.model.Group;
import org.traccar.model.HoraSalida;
import org.traccar.model.Itinerario;
import org.traccar.model.Permission;
import org.traccar.model.Subroute;
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
@Path("horasalidas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HorasSalidasResource extends BaseObjectResource<HoraSalida> {

    @Inject
    private CacheManager cacheManager;

    @Inject
    private ConnectionManager connectionManager;

    public HorasSalidasResource() {
        super(HoraSalida.class);
    }

    @GET
    public Collection<HoraSalida> get(
            @QueryParam("all") boolean all, @QueryParam("userId") long userId) throws StorageException {
        Collection<HoraSalida> result = new ArrayList<>();

        User userRequest = storage.getObject(User.class, new Request(new Columns.All(), new Condition.Equals("id", getUserId())));
        var conditions = new LinkedList<Condition>();

        if (userRequest.getAdministrator()) {
            return storage.getObjects(HoraSalida.class, new Request(new Columns.All()));
        }

        List<Permission> directas = storage.getPermissions(User.class, HoraSalida.class);
        directas.stream().filter((i) -> i.getOwnerId() == userId).forEach((p) -> {
            try {
                result.add(storage.getObject(HoraSalida.class, new Request(new Columns.All(), new Condition.Equals("id", p.getPropertyId()))));
            } catch (StorageException ex) {
                Logger.getLogger(HorasSalidasResource.class.getName()).log(Level.SEVERE, null, ex);
            }

        });
        List<Long> grupos = storage.getPermissions(User.class, Group.class).stream().filter((item) -> item.getOwnerId() == getUserId()).map((item) -> item.getPropertyId()).collect(Collectors.toList());

        grupos.stream().forEach((g) -> {
            try {
                List<Permission> pgrupos = storage.getPermissions(Group.class, HoraSalida.class);

                pgrupos.stream().filter((p) -> p.getOwnerId() == g).forEach((i) -> {
                    try {
                        result.add(storage.getObject(HoraSalida.class, new Request(new Columns.All(), new Condition.Equals("id", i.getPropertyId()))));
                    } catch (StorageException ex) {
                        Logger.getLogger(HorasSalidasResource.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });

            } catch (StorageException ex) {
                Logger.getLogger(HorasSalidasResource.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        return removeDuplicates(result.stream().collect(Collectors.toList()));
    }

    @GET
    @Path("{name}/list")
    public Collection<HoraSalida> getHoras(@PathParam("name") String name) throws StorageException {
        Collection<HoraSalida> result = new ArrayList<>();

        result = storage.getObjects(HoraSalida.class, new Request(new Columns.All(), new Condition.Equals("name", name)));

        return removeDuplicates(result.stream().collect(Collectors.toList()));
    }

    public static <T> List<T> removeDuplicates(List<T> list) {
        Set<T> seen = new HashSet<>();
        List<T> filteredList = new ArrayList<>();

        for (T item : list) {
            if (seen.add(item)) {
                filteredList.add(item);
            }
        }

        return filteredList;
    }
}
