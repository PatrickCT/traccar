/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.api.resource;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;
import org.traccar.api.BaseObjectResource;
import org.traccar.model.Device;
import org.traccar.model.Itinerario;
import org.traccar.model.Permission;
import org.traccar.model.Salida;
import org.traccar.model.SubUser;
import org.traccar.model.Ticket;
import org.traccar.model.Tramo;
import org.traccar.model.User;
import org.traccar.storage.StorageException;
import org.traccar.storage.query.Columns;
import org.traccar.storage.query.Condition;
import org.traccar.storage.query.Request;
import org.traccar.utils.GenericUtils;
import org.traccar.utils.TransporteUtils;

/**
 * @author K
 */
@Path("salidas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SalidasResource extends BaseObjectResource<Salida> {

    public SalidasResource() {
        super(Salida.class);
    }

    @GET
    public Collection<Salida> get(
            @QueryParam("all") boolean all, @QueryParam("finished") boolean finished) throws StorageException {

        Collection<Salida> result = new ArrayList<>();
        List<Long> devices = storage.getPermissions(User.class, Device.class).stream().map((item) -> item.getPropertyId()).collect(Collectors.toList());
        devices.forEach(id -> {
            try {
                result.addAll(storage.getObjects(baseClass, new Request(new Columns.All(), new Condition.Equals("deviceId", id))));
            } catch (StorageException ex) {
                Logger.getLogger(ItinerarioResource.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        return all ? result : result.stream().filter(item -> item.getFinished() == finished).collect(Collectors.toList());

    }

    @Path("{id}/adjustment")
    @POST
    public Response ajuste(@PathParam("id") long salidaId, LinkedHashMap<String, Object> values) throws StorageException, ParseException {
        JSONObject response = new JSONObject("{}");
        response.put("status", true);
        Salida salida = storage.getObject(Salida.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
            {
                add(new Condition.Equals("id", salidaId));
            }
        })));
        if (salida == null) {
            return Response.ok(response.toMap()).build();
        }

        List<Ticket> tickets = storage.getObjects(Ticket.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
            {
                add(new Condition.Equals("salidaId", salida.getId()));
            }
        })));
        if (tickets.isEmpty()) {
            return Response.ok(response.toMap()).build();
        }

        List<Permission> permisos = storage.getPermissions(Itinerario.class, Tramo.class).stream().filter((p) -> p.getOwnerId() == salida.getScheduleId()).collect(Collectors.toList());
        List<Tramo> tramos = new ArrayList<>();
        permisos.forEach((p) -> {
            try {
                tramos.add(storage.getObject(Tramo.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                    {
                        add(new Condition.Equals("id", p.getPropertyId()));
                    }
                }))));
            } catch (StorageException ex) {
                Logger.getLogger(TransporteUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        Date newDate = new Date(tickets.get(0).getExpectedTime().getTime());

        Date parsedDate = GenericUtils.parseTime((String) values.get("time"));
        newDate.setHours(parsedDate.getHours());
        newDate.setMinutes(parsedDate.getMinutes());

        long differenceInMillis = newDate.getTime() - tickets.get(0).getExpectedTime().getTime();

        long minutesDifference = differenceInMillis / (1000 * 60);

        for (int i = 0; i < tickets.size(); i++) {
            Ticket ticket = tickets.get(i);

            ticket.setExpectedTime(GenericUtils.addTimeToDate(ticket.getExpectedTime(), Calendar.MINUTE, (int) minutesDifference));
            if (ticket.getEnterTime() != null) {
                long newDifference = ticket.getEnterTime().getTime() - ticket.getExpectedTime().getTime();
                long newMinutesDifference = newDifference / (1000 * 60);

                ticket.setDifference(newMinutesDifference);
                ticket.setPunishment((int) (tramos.stream().filter((t) -> t.getId() == ticket.getTramo()).findFirst().get().getPunishment() * newMinutesDifference));
            }

            storage.updateObject(ticket, new Request(
                    new Columns.Exclude("id"),
                    new Condition.Equals("id", ticket.getId())));
        }
        salida.setEndingDate(GenericUtils.addTimeToDate(salida.getEndingDate(), Calendar.MINUTE, (int) minutesDifference));
        String pass = (String) values.get("pass");
        SubUser su = storage.getObjectsByQuery(SubUser.class, String.format("select * from tc_subusers where pass = '%s'", pass)).get(0);
        salida.setModifiedBy((int) su.getId());
        salida.setModifiedWhen(new Date());
        storage.updateObject(salida, new Request(
                new Columns.Exclude("id"),
                new Condition.Equals("id", salida.getId())));
        return Response.ok(response.toMap()).build();
    }

    @POST
    @Path("crear")
    public Response crear(Salida entity) throws StorageException, ParseException {
        if (TransporteUtils.hasSalida(entity.getDeviceId(), storage)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        TransporteUtils.generarSalida(entity.getDeviceId(), entity.getScheduleId(), entity.getDate(), storage);
        return Response.ok(entity).build();
    }

    @GET
    @Path("report")
    public Response report() throws StorageException {
        final long id = getUserId();
        Collection<Salida> result = new ArrayList<>();
        List<Long> devices = storage.getPermissions(User.class, Device.class).stream().filter((item) -> item.getOwnerId() == id).map((item) -> item.getPropertyId()).collect(Collectors.toList());
        result.addAll(storage.getObjectsByQuery(baseClass, "select * from tc_salidas "
                + "where finished = false and "
                + "deviceid in "
                + GenericUtils.getIdsAsString(devices)
                + " "));

        return Response.ok(result).build();
    }
}
