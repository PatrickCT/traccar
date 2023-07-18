/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.slf4j.LoggerFactory;
import org.traccar.model.Device;
import org.traccar.model.Group;
import org.traccar.model.Itinerario;
import org.traccar.model.Permission;
import org.traccar.model.Salida;
import org.traccar.model.Subroute;
import org.traccar.model.Ticket;
import org.traccar.model.Tramo;
import org.traccar.session.cache.CacheManager;
import org.traccar.storage.StorageException;
import org.traccar.storage.query.Columns;
import org.traccar.storage.query.Condition;
import org.traccar.storage.query.Request;

/**
 *
 * @author K
 */
public class TransporteUtils {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TransporteUtils.class);

    public static void generarSalida(long deviceId, long geofenceId, Date time, CacheManager cacheManager) {
        try {

            System.out.println("Generando salida");
            //revisar salidas pendientes
            System.out.println("Revisar salidas pendientes");
            Salida salida = cacheManager.getStorage().getObject(Salida.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                {
                    add(new Condition.Equals("finished", false));
                    add(new Condition.Equals("deviceId", deviceId));
                }
            })));
            if (salida != null) {
                if (GenericUtils.isSameDate(salida.getDate(), new Date())) {
                    if (salida.getEndingDate().getTime() > new Date().getTime()) {
                        salida.setFinished(true);
                        cacheManager.getStorage().updateObject(salida, new Request(
                                new Columns.Exclude("id"),
                                new Condition.Equals("id", salida.getId())));
                    } else {
                        return;
                    }
                }
            }
            System.out.println(salida);
            //obtener dispositivo
            System.out.println("Obtener dispositivo");
            Device device = cacheManager.getStorage().getObject(Device.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                {
                    add(new Condition.Equals("id", deviceId));
                }
            })));
            System.out.println(device);
            if (device == null) {
                return;
            }
            //obetener grupo
            System.out.println("Obtener grupo");
            Group group = cacheManager.getStorage().getObject(Group.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                {
                    add(new Condition.Equals("id", device.getGroupId()));
                }
            })));
            System.out.println(group);
            if (group == null) {
                return;
            }

            //obtener subrutas
            System.out.println("Obtener sub-rutas");
            List<Subroute> subroutes = cacheManager.getStorage().getObjects(Subroute.class, new Request(new Columns.All(), new Condition.Equals("groupId", group.getId())));
            List<Long> subroutesId = subroutes.stream().map((s) -> s.getId()).collect(Collectors.toList());
            System.out.println(subroutes);
            //encontrar itinerario
            //Itinerario itinerario = cacheManager.getStorage().getObject(Itinerario.class, new Request(new Columns.All(), new Condition.Equals("geofence", salida)));
            System.out.println("Obtener itinerarios");
            
            List<Itinerario> itinerarios = cacheManager.getStorage().getObjects(Itinerario.class, new Request(new Columns.All(), new Condition.Equals("geofenceId", geofenceId)));
            System.out.println(itinerarios);
            if (itinerarios.isEmpty()) {
                return;
            }            
            Itinerario itinerarioSelected = null;
            System.out.println("Obtener itinerario");
            for (Itinerario itinerario : itinerarios) {
                if (subroutesId.contains(itinerario.getSubrouteId())) {
                    if (GenericUtils.isDaySelected(itinerario.getDays(), GenericUtils.getDayValue(new Date()))) {
                        itinerarioSelected = findClosestObject(itinerarios, new Date(), 3);
                        if (itinerarioSelected == null) {
                            return;
                        }
                    }
                }
            }
            System.out.println(itinerarioSelected);

            //encontrar puntos de control
            System.out.println("Obtener tramos");
            final Itinerario finalItinerarioSelected = itinerarioSelected;
            List<Permission> permisos = cacheManager.getStorage().getPermissions(Itinerario.class, Tramo.class).stream().filter((p) -> p.getOwnerId() == finalItinerarioSelected.getId()).collect(Collectors.toList());
            List<Tramo> tramos = new ArrayList<>();
            permisos.forEach((p) -> {
                try {
                    tramos.add(cacheManager.getStorage().getObject(Tramo.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                        {
                            add(new Condition.Equals("id", p.getPropertyId()));
                        }
                    }))));
                } catch (StorageException ex) {
                    Logger.getLogger(TransporteUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            System.out.println(tramos);

            if (tramos.isEmpty()) {
                return;
            }

            //crear nueva salida
            System.out.println("Nueva salida");
            Date today = new Date();
            Date endDate = today;
            Salida newSalida = new Salida();
            newSalida.setDate(today);
            newSalida.setDeviceId(deviceId);
            newSalida.setFinished(false);
            newSalida.setScheduleId(itinerarioSelected.getId());
            for (Tramo tramo : tramos) {
                endDate = GenericUtils.addTimeToDate(endDate, Calendar.MINUTE, tramo.getMaxTime());
            }
            newSalida.setEndingDate(endDate);
            newSalida.setId(cacheManager.getStorage().addObject(newSalida, new Request(new Columns.Exclude("id"))));
            System.out.println(newSalida);

            //crear tickets
            System.out.println("Nuevos tickets");
            Date ticketStart = (itinerarioSelected.getStart() != null ? GenericUtils.parseTime(itinerarioSelected.getStart()) : today);
            for (Tramo tramo : tramos) {
                Ticket ticket = new Ticket();
                ticketStart = GenericUtils.addTimeToDate(ticketStart, Calendar.MINUTE, tramo.getMinTime());
                ticket.setExpectedTime(ticketStart);
                ticket.setGeofenceId(tramo.getGeofenceId());
                ticket.setPunishment(0);
                ticket.setSalidaId(newSalida.getId());
                ticket.setId(cacheManager.getStorage().addObject(ticket, new Request(new Columns.Exclude("id"))));
                System.out.println(ticket);
            }
            updateSalida(deviceId, geofenceId, time, cacheManager);
            return;
        } catch (StorageException ex) {
            Logger.getLogger(TransporteUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(TransporteUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static boolean hasSalida(long deviceId, CacheManager cacheManager) {
        try {
            Salida salida = cacheManager.getStorage().getObject(Salida.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                {
                    add(new Condition.Equals("finished", false));
                    add(new Condition.Equals("deviceId", deviceId));
                }
            })));
            return salida != null;
        } catch (StorageException ex) {
            Logger.getLogger(TransporteUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static void updateSalida(long deviceId, long geofenceId, Date realTime, CacheManager cacheManager) {
        try {
            System.out.println("update salida");
            Salida salida = cacheManager.getStorage().getObject(Salida.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                {
                    add(new Condition.Equals("finished", false));
                    add(new Condition.Equals("deviceId", deviceId));
                }
            })));
            System.out.println(salida);
            List<Ticket> tickets = cacheManager.getStorage().getObjects(Ticket.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                {
                    add(new Condition.Equals("salidaId", salida.getId()));
                }
            })));
            System.out.println(tickets);
            Ticket ticket = cacheManager.getStorage().getObject(Ticket.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                {
                    add(new Condition.Equals("salidaId", salida.getId()));
                    add(new Condition.Equals("geofenceId", geofenceId));
                }
            })));
            System.out.println(ticket);
            if (ticket == null) {
                salida.setFinished(true);
                cacheManager.getStorage().updateObject(salida, new Request(
                        new Columns.Exclude("id"),
                        new Condition.Equals("id", salida.getId())));

                return;
            }
            boolean isLastTicket = ticket.getId() == tickets.get(tickets.size() - 1).getId();
            System.out.println(isLastTicket);
            if (isLastTicket) {
                salida.setFinished(true);
                cacheManager.getStorage().updateObject(salida, new Request(
                        new Columns.Exclude("id"),
                        new Condition.Equals("id", salida.getId())));
            }

            if (ticket.getEnterTime() == null) {
                ticket.setEnterTime(realTime);
            } else {
                ticket.setExitTime(realTime);
            }

            long differenceInMillis = realTime.getTime() - ticket.getExpectedTime().getTime();
            long minutesDifference = differenceInMillis / (1000 * 60);

            if (differenceInMillis < 0) {
                minutesDifference *= -1;
            }
            ticket.setPunishment((int) (minutesDifference * ticket.getPunishment()));
            if (realTime.getTime() < ticket.getExpectedTime().getTime()) {
                minutesDifference *= -1;
            }
            System.out.println(minutesDifference);
            ticket.setDifference(minutesDifference);

            cacheManager.getStorage().updateObject(ticket, new Request(
                    new Columns.Exclude("id"),
                    new Condition.Equals("id", ticket.getId())));
            System.out.println(ticket);

        } catch (StorageException ex) {
            Logger.getLogger(TransporteUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Itinerario findClosestObject(List<Itinerario> objects, Date date, int rangeInMinutes) throws ParseException {
        Itinerario closestObject = null;
        long closestDifference = Long.MAX_VALUE;

        for (Itinerario obj : objects) {
            if (obj.getStart() != null) {
                Date start = GenericUtils.parseTime(obj.getStart());  // Parse "start" value to Date

                long difference = Math.abs(date.getTime() - start.getTime());
                if (difference <= rangeInMinutes * 60000 && difference < closestDifference) {
                    closestObject = obj;
                    closestDifference = difference;
                }
            } else {
                if (closestObject == null) {
                    closestObject = obj;
                }
            }
        }

        return closestObject;
    }

    public static void terminarSalidas(CacheManager cacheManager) {
        try {
            List<Salida> salidas = cacheManager.getStorage().getObjects(Salida.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                {
                    add(new Condition.Equals("finished", false));
                }
            })));
            for (Salida salida : salidas) {
                if (salida.getEndingDate().getTime() > new Date().getTime()) {
                    salida.setFinished(true);
                    cacheManager.getStorage().updateObject(salida, new Request(
                            new Columns.Exclude("id"),
                            new Condition.Equals("id", salida.getId())));
                }
            }
        } catch (StorageException ex) {
            Logger.getLogger(TransporteUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
