/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.utils;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.traccar.api.resource.DeviceResource;
import org.traccar.model.Device;
import org.traccar.model.Driver;
import org.traccar.model.Event;
import org.traccar.model.Excuse;
import org.traccar.model.Geofence;
import org.traccar.model.Group;
import org.traccar.model.HoraSalida;
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
import org.traccar.storage.Storage;

/**
 *
 * @author K
 */
public class TransporteUtils {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TransporteUtils.class);

    public static void generarSalida(long deviceId, long geofenceId, Date time, CacheManager cacheManager) throws ParseException {
        try {
            ProccessLogger logger = new ProccessLogger(LOGGER);
            logger.info("Generando salida " + deviceId + ", " + time);
//            cacheManager.getDevLog().log("Nueva salida salida");
//            cacheManager.getDevLog().log(deviceId + ", " + geofenceId + ", " + time);
            //revisar salidas pendientes
            Salida salida = cacheManager.getStorage().getObject(Salida.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                {
                    add(new Condition.Equals("finished", false));
                    add(new Condition.Equals("deviceId", deviceId));
                }
            })));
            if (salida != null) {
                return;
            }
            logger.info("Salida" + salida);
            //obtener dispositivo
            Device device = cacheManager.getStorage().getObject(Device.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                {
                    add(new Condition.Equals("id", deviceId));
                }
            })));

            if (device == null) {
                return;
            }
            //obetener grupo
            logger.info("Device" + device);
            Group group = cacheManager.getStorage().getObject(Group.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                {
                    add(new Condition.Equals("id", device.getGroupId()));
                }
            })));

            if (group == null) {
                return;
            }
            logger.info("Group" + group);
            //obtener subrutas
            List<Subroute> subroutes = cacheManager.getStorage().getObjects(Subroute.class, new Request(new Columns.All(), new Condition.Equals("groupId", group.getId())));
            List<Long> subroutesId = subroutes.stream().map((s) -> s.getId()).collect(Collectors.toList());

            //encontrar itinerario
            //Itinerario itinerario = cacheManager.getStorage().getObject(Itinerario.class, new Request(new Columns.All(), new Condition.Equals("geofence", salida)));
            List<Itinerario> itinerarios = cacheManager.getStorage().getObjects(Itinerario.class, new Request(new Columns.All(), new Condition.Equals("geofenceId", geofenceId)));

            if (itinerarios.isEmpty()) {
                return;
            }
            Itinerario itinerarioSelected = null;

            List<Itinerario> todayItinerarios = new ArrayList<>();
            for (Itinerario itinerario : itinerarios) {
                if (subroutesId.contains(itinerario.getSubrouteId())) {
                    logger.info(String.valueOf(itinerario.getDays()));
                    logger.info(String.valueOf(GenericUtils.getDayValue(new Date())));
                    if (GenericUtils.isDaySelected(itinerario.getDays(), GenericUtils.getDayValue(new Date()))) {
                        todayItinerarios.add(itinerario);
                    }
                }
            }

            //si es de villas
            if (group.hasAttribute("vp") && (boolean) group.getAttributes().get("vp") == true) {
                logger.info("Villas");
//                cacheManager.getDevLog().log("Villas");
                //si es la primer salida del dia

                logger.info(String.format("select * from tc_salidas where deviceid=%s and valid=true and date(tc_salidas.date) = curdate()", deviceId));
                List<Salida> salidas_today = cacheManager.getStorage().getObjectsByQuery(Salida.class, String.format("select * from tc_salidas where deviceid=%s and valid=true and date(tc_salidas.date) = curdate()", deviceId));
                if (salidas_today.isEmpty()) {
                    logger.info("primer salida del dia");
                    itinerarioSelected = findClosestObject(todayItinerarios, new Date(), 1, cacheManager.getStorage(), true);
                    List<HoraSalida> horas = cacheManager.getStorage().getObjects(HoraSalida.class, new Request(new Columns.All(), new Condition.Equals("name", cacheManager.getStorage().getObject(HoraSalida.class, new Request(new Columns.All(), new Condition.Equals("id", itinerarioSelected.getHorasId()))).getName())));

                    itinerarioSelected.getAttributes().put("horaFinal", horas.get(0).getHour());
                    logger.info("iti sel: " + itinerarioSelected);
                } else {
                    logger.info("n salida del dia");
                    boolean back = false;
                    itinerarioSelected = cacheManager.getStorage().getObject(Itinerario.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                        {
                            add(new Condition.Equals("id", salidas_today.get(0).getScheduleId()));
                        }

                    })));
                    if (itinerarioSelected.getGeofenceId() != geofenceId) {
                        back = true;
                        itinerarioSelected = cacheManager.getStorage().getObject(Itinerario.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                            {
                                add(new Condition.Equals("horasIdRel", cacheManager.getStorage().getObject(Itinerario.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                                    {
                                        add(new Condition.Equals("id", salidas_today.get(0).getScheduleId()));
                                    }

                                }))).getHorasId()));
                            }

                        })));
                    }
                    List<HoraSalida> horas = cacheManager.getStorage().getObjects(HoraSalida.class, new Request(new Columns.All(), new Condition.Equals("name", cacheManager.getStorage().getObject(HoraSalida.class, new Request(new Columns.All(), new Condition.Equals("id", itinerarioSelected.getHorasId()))).getName())));
                    if ((salidas_today.size() / 2) >= horas.size()) {
                        return;
                    }
                    itinerarioSelected.getAttributes().put("horaFinal", horas.get((int) (salidas_today.size() / 2)).getHour());
                    logger.info(itinerarioSelected.toString());
                }
                //si no
            } else {
                itinerarioSelected = findClosestObject(todayItinerarios, new Date(), 3, cacheManager.getStorage(), false);
            }
            //si no es de villas

            if (itinerarioSelected == null) {
                logger.info("no itinerario");
                return;
            }

//            logger.info(itinerarioSelected);
            //encontrar puntos de control
            final Itinerario finalItinerarioSelected = itinerarioSelected;
            List<Permission> permisos = cacheManager.getStorage().getPermissions(Itinerario.class, Tramo.class);

            List<Tramo> tramos = new ArrayList<>();
            permisos.forEach((p) -> {
                try {
                    Tramo t = cacheManager.getStorage().getObject(Tramo.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                        {
                            add(new Condition.Equals("id", p.getPropertyId()));
                        }
                    })));

                    if (t != null && p.getOwnerId() == finalItinerarioSelected.getId()) {
                        tramos.add(t);
                    }
                } catch (StorageException ex) {
                    Logger.getLogger(TransporteUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            if (tramos.isEmpty()) {
                return;
            }

            //crear nueva salida
            Date today = new Date();
            if (group.hasAttribute("vp") && (boolean) group.getAttributes().get("vp") == true) {
                Date hourDate = (Date) itinerarioSelected.getAttributes().get("horaFinal");
                today.setHours(hourDate.getHours());
                today.setMinutes(hourDate.getMinutes());
            } else {
                today = time;
            }
            today.setSeconds(0);
            Date endDate = today;
            Salida newSalida = new Salida();
            newSalida.setGeofenceId(geofenceId);
            newSalida.setValid(true);
            newSalida.setDate(today);
            newSalida.setDeviceId(deviceId);
            newSalida.setFinished(false);
            newSalida.setScheduleId(itinerarioSelected.getId());
            for (Tramo tramo : tramos) {
                endDate = GenericUtils.addTimeToDate(endDate, Calendar.MINUTE, tramo.getMinTime());
            }
            newSalida.setEndingDate(endDate);
            newSalida.setGroupId(group.getId());
            newSalida.setSubrouteId(itinerarioSelected.getSubrouteId());
            newSalida.setId(cacheManager.getStorage().addObject(newSalida, new Request(new Columns.Exclude("id"))));

            //crear tickets
            Date ticketStart = (itinerarioSelected.hasAttribute("horaFinal") ? (Date) itinerarioSelected.getAttributes().get("horaFinal") : today);
            ticketStart.setMonth((new Date()).getMonth());
            ticketStart.setDate((new Date()).getDate());
//            logger.info(ticketStart);
            boolean isFirstTicket = geofenceId == tramos.get(0).getGeofenceId();
//            logger.info("Device " + device + "caso especial");
            if (!isFirstTicket) {
//                logger.info("Buscando hora inicial");
                Date posibleStart = findFirstEventTime(newSalida.getId(), itinerarioSelected.getId(), cacheManager);
                if (posibleStart != null) {
                    ticketStart = posibleStart;
                } else {
                    for (Tramo tramo : tramos) {
                        ticketStart = GenericUtils.addTimeToDate(ticketStart, Calendar.MINUTE, (tramo.getMinTime() * -1));
                        if (tramo.getGeofenceId() == geofenceId) {
                            break;
                        }
                    }
                }

                newSalida.setDate(ticketStart);
                cacheManager.getStorage().updateObject(newSalida, new Request(
                        new Columns.Exclude("id"),
                        new Condition.Equals("id", newSalida.getId())));
            }
            Ticket ticket = null;
            for (Tramo tramo : tramos) {
                ticket = new Ticket();
                ticketStart = GenericUtils.addTimeToDate(ticketStart, Calendar.MINUTE, tramo.getMinTime());
                ticket.setExpectedTime(ticketStart);
                ticket.setGeofenceId(tramo.getGeofenceId());
                ticket.setPunishment(tramo.getPunishment());
                ticket.setSalidaId(newSalida.getId());
                ticket.setTramo(tramo.getId());
                ticket.setId(cacheManager.getStorage().addObject(ticket, new Request(new Columns.Exclude("id"))));

            }

            if (!isFirstTicket) {
                updateOldTramos(newSalida.getId(), itinerarioSelected.getId(), cacheManager, geofenceId);
            }

            updateSalida(deviceId, geofenceId, time, cacheManager, true);
        } catch (StorageException ex) {
            ex.printStackTrace();
            Logger.getLogger(TransporteUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.getLogger(TransporteUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static boolean hasSalida(long deviceId, CacheManager cacheManager, long geofenceId) {
        ProccessLogger logger = new ProccessLogger(LOGGER);
        try {
            logger.info("Revisando salidas activas device " + deviceId + " geofence " + geofenceId);
            Salida salida = cacheManager.getStorage().getObject(Salida.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                {
                    add(new Condition.Equals("finished", false));
                    add(new Condition.Equals("deviceId", deviceId));
                }
            })));
            logger.info("Salida encontrada " + salida);
            if (salida == null) {
                return false;
            }

            return true;
//            List<Itinerario> itinerarios = cacheManager.getStorage().getObjects(Itinerario.class, new Request(new Columns.All(), new Condition.Equals("geofenceId", geofenceId)));
//            if (!itinerarios.isEmpty()) {
//                salida.setFinished(true);
//                cacheManager.getStorage().updateObject(salida, new Request(
//                        new Columns.Exclude("id"),
//                        new Condition.Equals("id", salida.getId())));
//                return false;
//            }
//            return itinerarios.isEmpty();

        } catch (StorageException ex) {
            logger.info("Error al buscar salida");
            ex.printStackTrace();
            Logger.getLogger(TransporteUtils.class.getName()).log(Level.INFO, "Algo salio mal");
            Logger.getLogger(TransporteUtils.class.getName()).log(Level.SEVERE, null, ex.getMessage());
        }
        return false;
    }

    public static boolean hasSalida(long deviceId, Storage storage) {
        try {
            Salida salida = storage.getObject(Salida.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                {
                    add(new Condition.Equals("finished", false));
                    add(new Condition.Equals("deviceId", deviceId));
                }
            })));

            return salida != null;
        } catch (StorageException ex) {
            ex.printStackTrace();
            Logger.getLogger(TransporteUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static void updateSalida(long deviceId, long geofenceId, Date realTime, CacheManager cacheManager, boolean first) {
        try {
            ProccessLogger logger = new ProccessLogger(LOGGER);
//            logger.info("Update salida");
            logger.info("Actualizando tramo, device " + deviceId + ", geofence " + geofenceId + ", " + realTime);
            Salida salida = cacheManager.getStorage().getObject(Salida.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                {
                    add(new Condition.Equals("finished", false));
                    add(new Condition.Equals("deviceId", deviceId));
                }
            })));
            logger.info("Salida " + salida);
            if (salida == null) {
                logger.info("No salida");
                return;
            }
            List<Ticket> tickets = cacheManager.getStorage().getObjects(Ticket.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                {
                    add(new Condition.Equals("salidaId", salida.getId()));
                }
            })));
            logger.info("Tickets " + tickets);
//            Ticket ticket = cacheManager.getStorage().getObject(Ticket.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
//                {
//                    add(new Condition.Equals("salidaId", salida.getId()));
//                    add(new Condition.Equals("geofenceId", geofenceId));
//                }
//            })));
            Ticket ticket = tickets.stream().filter((t) -> t.getGeofenceId() == geofenceId).findFirst().orElse(null);
            logger.info("Ticket " + ticket);
            if (ticket == null) {
                logger.info("No ticket");
                return;
            }
            boolean isFirstTicket = ticket.getId() == tickets.get(0).getId();

            boolean isLastTicket = ticket.getId() == tickets.get(tickets.size() - 1).getId();

            int time = 29;
            Group device_group = cacheManager.getStorage().getObject(Group.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                {
                    add(new Condition.Equals("id", salida.getGroupId()));
                }
            })));

            if (device_group != null && device_group.getAttributes().containsKey("vp") && (boolean) device_group.getAttributes().get("vp") == true) {
                time = 120;
            }

            if (first) {
                ticket.setEnterTime(realTime);
                long differenceInMillis = realTime.getTime() - ticket.getExpectedTime().getTime();
                long secondsDifference = differenceInMillis / 1000;
                long minutesDifference = differenceInMillis / (1000 * 60);
                logger.info("Revisar tiempo");
                logger.info("minutesDifference " + minutesDifference);
                if (minutesDifference >= time || minutesDifference < -time) {
                    logger.info("Diferencia mayor a 20 min, eliminando salida " + salida);
                    salida.setValid(false);
//                    salida.setFinished(true);
                }
                Tramo tramo = cacheManager.getStorage().getObject(Tramo.class, new Request(new Columns.All(), new Condition.Equals("id", ticket.getTramo())));
                int punishmentCost = tramo.getPunishment();

                if (tramo != null) {
                    //checar si es vp
                    if (time == 120) {
                        List<Excuse> excusas = cacheManager.getStorage().getObjectsByQuery(Excuse.class, String.format("select * from tc_excuses where date(tc_excuses.availability) = curdate()"));
//                        logger.info(excusas);
                        if (!excusas.isEmpty()) {
                            for (Excuse excusa : excusas) {
                                if (GenericUtils.isDateBetween(realTime, excusa.getApplyFrom(), excusa.getApplyTo())) {
                                    punishmentCost = 0;
                                    ticket.setExcuse(excusa.getDescription());
                                    ticket.setGlobalExcuse(true);
                                    ticket.setPunishment(0);
                                    break;
                                }
                            }
                        } else {
                            //si no hay excusas revisar adelantados, atrasados

                            int multa_atrasado = Integer.parseInt(String.valueOf(device_group.getAttributes().getOrDefault("atrasado", punishmentCost)));
                            int multa_adelantado = Integer.parseInt(String.valueOf(device_group.getAttributes().getOrDefault("adelentado", punishmentCost)));
                            int tiempo_atrasado = Integer.parseInt(String.valueOf(device_group.getAttributes().getOrDefault("tiempo_atrasado", 0)));
                            int tiempo_adelantado = Integer.parseInt(String.valueOf(device_group.getAttributes().getOrDefault("tiempo_adelantado", 0)));
                            int segundos_tolerancia = Integer.parseInt(String.valueOf(device_group.getAttributes().getOrDefault("tolerancia_atrasado", 0)));

                            //adelanto
                            if (secondsDifference >= tiempo_adelantado * 60) {
                                punishmentCost = multa_adelantado;
                            }

                            // atrasado
                            if (secondsDifference >= ((tiempo_atrasado * 60) + segundos_tolerancia)) {  // 3 minutes and 16 seconds in milliseconds
                                punishmentCost = multa_atrasado;
                            }

                            ticket.setPunishment((int) (minutesDifference * punishmentCost));
                        }
                    } else {
                        if (minutesDifference < 0) {
                            ticket.setPunishment(0);
                        } else {
                            ticket.setPunishment((int) (minutesDifference * punishmentCost));
                        }
                    }
                } else {
                    ticket.setPunishment(0);
                }

                logger.info("Revisar tiempo 2");
                logger.info("minutesDifference " + minutesDifference);
                if (minutesDifference >= time || minutesDifference < -time) {
                    logger.info("Diferencia mayor a 20 min, eliminando salida " + salida);
                    //cacheManager.getStorage().removeObject(Salida.class, new Request(new Columns.All(), new Condition.Equals("id", salida.getId())));
                    salida.setValid(false);
//                    salida.setFinished(true);
                }

                ticket.setDifference(minutesDifference);
            } else {
                ticket.setExitTime(realTime);
            }

            if (ticket.getExitTime() != null) {
                ticket.setPassed(true);
            }

            logger.info("Ticket procesado " + ticket);

            if (isLastTicket) {
                logger.info("Ultimo ticket del recorrido terminando salida " + salida);
                salida.setFinished(true);
                if (ticket.getExitTime() == null) {
                    ticket.setExitTime(realTime);
                }
            }

            cacheManager.getStorage().updateObject(ticket, new Request(
                    new Columns.Exclude("id"),
                    new Condition.Equals("id", ticket.getId())));
            logger.info("Ticket guardado " + ticket);

            cacheManager.getStorage().updateObject(salida, new Request(
                    new Columns.Exclude("id"),
                    new Condition.Equals("id", salida.getId())));
            logger.info("Salida guardada " + salida);
//            logger.info("Updated ticket " + ticket);

        } catch (StorageException ex) {
            Logger.getLogger(TransporteUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void updateOldTramos(long salidaId, long itinerarioId, CacheManager cacheManager, long geofenceId) {
//        LOGGER.info("update old");
        try {
            boolean stop = false;
            Salida salida = cacheManager.getStorage().getObject(Salida.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                {
                    add(new Condition.Equals("id", salidaId));
                }
            })));
//            LOGGER.info(salida);
            if (salida == null) {
                return;
            }
            if (salida.getFinished()) {
                return;
            }

            Itinerario itinerario = cacheManager.getStorage().getObject(Itinerario.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                {
                    add(new Condition.Equals("id", itinerarioId));
                }
            })));
//            LOGGER.info(itinerario);
            if (itinerario == null) {
                return;
            }
            if (itinerario.getDays() <= 0) {
                return;
            }

            List<Ticket> tickets = cacheManager.getStorage().getObjects(Ticket.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                {
                    add(new Condition.Equals("salidaId", salida.getId()));
                }
            })));
//            LOGGER.info(tickets);
            boolean first = true;
            int origin=tickets.size();

            Ticket ticketStart = tickets.stream().filter((t) -> t.getGeofenceId() == geofenceId).findFirst().orElse(null);

            if(ticketStart != null){
                int originIndexInList = tickets.indexOf(ticketStart);
                if (originIndexInList != -1) {
                    origin=originIndexInList;

                }
            }

            for (Ticket ticket : tickets.subList(0, origin)) {
                
                if (ticket.getEnterTime() == null) {
                    List<Event> events = cacheManager.getStorage().getObjects(Event.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                        {
                            add(new Condition.Equals("type", (tickets.indexOf(ticket) == 0 ? "geofenceExit" : "geofenceEnter")));
                            add(new Condition.Equals("deviceid", salida.getDeviceId()));
                            add(new Condition.Equals("geofenceid", ticket.getGeofenceId()));
                            add(new Condition.Between("eventtime", "from", GenericUtils.addTimeToDate(salida.getDate(), Calendar.MINUTE, -15), "to", GenericUtils.addTimeToDate(salida.getEndingDate(), Calendar.MINUTE, 10)));
//                            add(new Condition.Compare("eventtime", "<=", "time", GenericUtils.addTimeToDate(salida.getEndingDate(), Calendar.MINUTE, 10)));
                        }
                    })));
                    Event event = events.get(events.size() - 1);
//                    LOGGER.info(event);

                    if (event == null) {
                        continue;
                    }

                    ticket.setEnterTime(event.getEventTime());

                    long differenceInMillis = ticket.getEnterTime().getTime() - ticket.getExpectedTime().getTime();
                    long minutesDifference = differenceInMillis / (1000 * 60);

                    if (differenceInMillis < 0) {
                        minutesDifference *= -1;
                    }
                    ticket.setPunishment((int) (minutesDifference * ticket.getPunishment()));
                    if (ticket.getEnterTime().getTime() < ticket.getExpectedTime().getTime()) {
                        minutesDifference *= -1;
                    }
                    ticket.setDifference(minutesDifference);
                    cacheManager.getStorage().updateObject(ticket, new Request(
                            new Columns.Exclude("id"),
                            new Condition.Equals("id", ticket.getId())));
                }
                if (ticket.getExitTime() == null) {
                    Event event = cacheManager.getStorage().getObject(Event.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                        {
                            add(new Condition.Equals("type", "geofenceExit"));
                            add(new Condition.Equals("deviceid", salida.getDeviceId()));
                            add(new Condition.Equals("geofenceid", ticket.getGeofenceId()));
//                            add(new Condition.Between("eventtime", "from", GenericUtils.addTimeToDate(salida.getDate(), Calendar.MINUTE, -15), "to", GenericUtils.addTimeToDate(salida.getEndingDate(), Calendar.MINUTE, 10)));
                        }
                    })));

                    if (event == null) {
                        continue;
                    }

                    ticket.setExitTime(event.getEventTime());
                }
                first = false;
                cacheManager.getStorage().updateObject(ticket, new Request(
                        new Columns.Exclude("id"),
                        new Condition.Equals("id", ticket.getId())));
                if(ticket.getGeofenceId() == salida.getGeofenceId())stop=true;
                
                if(stop)break;
            }

        } catch (StorageException ex) {
            ex.printStackTrace();
            Logger.getLogger(TransporteUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void ajustarOldTickets(long salidaId, long itinerarioId, CacheManager cacheManager) throws StorageException {

        try {
            Salida salida = cacheManager.getStorage().getObject(Salida.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                {
                    add(new Condition.Equals("id", salidaId));
                }
            })));
//            LOGGER.info(salida);
            if (salida == null) {
                return;
            }
            if (salida.getFinished()) {
                return;
            }

            Itinerario itinerario = cacheManager.getStorage().getObject(Itinerario.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                {
                    add(new Condition.Equals("id", itinerarioId));
                }
            })));
//            LOGGER.info(itinerario);
            if (itinerario == null) {
                return;
            }
            if (itinerario.getDays() <= 0) {
                return;
            }

            List<Ticket> tickets = cacheManager.getStorage().getObjects(Ticket.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                {
                    add(new Condition.Equals("salidaId", salida.getId()));
                }
            })));
//            LOGGER.info(tickets);
            boolean first = true;
            for (Ticket ticket : tickets) {
                if (!first) {
                    break;
                }
                if (ticket.getEnterTime() == null) {
                    List<Event> events = cacheManager.getStorage().getObjects(Event.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                        {
                            add(new Condition.Equals("type", "geofenceEnter"));
                            add(new Condition.Equals("deviceid", salida.getDeviceId()));
                            add(new Condition.Equals("geofenceid", ticket.getGeofenceId()));
                            add(new Condition.Between("eventtime", "from", GenericUtils.addTimeToDate(salida.getDate(), Calendar.MINUTE, -15), "to", new Date()));
                        }
                    })));
                    Event event = events.get(events.size() - 1);
//                    LOGGER.info(event);

                    if (event == null) {
                        first = false;
                        continue;
                    }

                    Date newDate = new Date(tickets.get(0).getExpectedTime().getTime());

                    Date parsedDate = event.getEventTime();
                    newDate.setHours(parsedDate.getHours());
                    newDate.setMinutes(parsedDate.getMinutes());

                    long differenceInMillis = newDate.getTime() - tickets.get(0).getExpectedTime().getTime();

                    long minutesDifference = differenceInMillis / (1000 * 60);

                    for (int i = 0; i < tickets.size(); i++) {
                        Ticket ticket2 = tickets.get(i);

                        ticket2.setExpectedTime(GenericUtils.addTimeToDate(ticket.getExpectedTime(), Calendar.MINUTE, (int) minutesDifference));

                        cacheManager.getStorage().updateObject(ticket2, new Request(
                                new Columns.Exclude("id"),
                                new Condition.Equals("id", ticket.getId())));
                    }
                }

                first = false;
            }

        } catch (StorageException ex) {
            Logger.getLogger(TransporteUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static Itinerario findClosestObject(List<Itinerario> objects, Date date, int rangeInMinutes, Storage storage, boolean lookupInitial) throws ParseException, StorageException, IOException {
        LOGGER.info("find closest");
        LOGGER.info(objects.stream().map(Object::toString)
                .collect(Collectors.joining(", ")));
        Itinerario closestObject = null;
        long closestDifference = Long.MAX_VALUE;
        List<Itinerario> filteredByDay = objects;
        List<Itinerario> filteredByHours = filterByHours(filteredByDay);
        Map<Long, Date> inicios = startHour(date, objects, objects.get(0).getGeofenceId(), storage);
        LOGGER.info("filtered by hours");
        LOGGER.info(filteredByHours.stream().map(Object::toString)
                .collect(Collectors.joining(", ")));

        for (Itinerario obj : filteredByHours) {
            if (obj.getHorasId() > 0) {
                for (HoraSalida h : storage.getObjects(HoraSalida.class, new Request(new Columns.All(), new Condition.Equals("name", storage.getObject(HoraSalida.class, new Request(new Columns.All(), new Condition.Equals("id", obj.getHorasId()))).getName())))) {
                    h.getHour().setDate(new Date().getDate());
                    h.getHour().setYear(new Date().getYear());
                    h.getHour().setMonth(new Date().getMonth());

                    LOGGER.info(h.toString());
                    long difference = Math.abs((lookupInitial ? inicios.getOrDefault(obj.getId(), date) : date).getTime() - h.getHour().getTime());

//                    if (difference <= rangeInMinutes * 60000 && difference < closestDifference) {
                    if (difference < closestDifference) {
                        closestObject = obj;
                        closestObject.getAttributes().put("horaFinal", h.getHour());
                        closestDifference = difference;
                    }
                }  // Parse "desde" value to Date

            } else {
                if (closestObject == null) {
                    closestObject = obj;
                }
            }
        }

        return closestObject;
    }

    private static List<Itinerario> filterByHours(List<Itinerario> i) throws ParseException, IOException {
        List<Itinerario> filterd = new ArrayList<>();
        int[] utc = GenericUtils.fetchUTCDate();
        LOGGER.info(String.valueOf(utc[0]));
        LOGGER.info(String.valueOf(utc[1]));

        i.forEach((iti) -> {

            if (iti.hasAttribute("hours")) {
                LOGGER.info("hours");
                JSONObject obj = new JSONObject(iti.getAttributes().get("hours").toString()
                        .replaceAll("1=", "\"1\":")
                        .replaceAll(" 2=", " \"2\":")
                        .replaceAll(" 4=", " \"4\":")
                        .replaceAll(" 8=", " \"8\":")
                        .replaceAll(" 16=", " \"16\":")
                        .replaceAll(" 32=", " \"32\":")
                        .replaceAll(" 64=", " \"64\":")
                        .replaceAll("desde=", "\"desde\":")
                        .replaceAll("hasta=", "\"hasta\":")
                        .replaceAll("=", ":"));

                if (obj.has(String.valueOf(GenericUtils.getDayValue(new Date())))) {
                    JSONObject horas = obj.getJSONObject(String.valueOf(GenericUtils.getDayValue(new Date())));
                    LOGGER.info(horas.toMap().toString());
                    int[] a = new int[2];
                    a[0] = horas.getJSONArray("desde").optInt(0);
                    a[1] = horas.getJSONArray("desde").optInt(1);
                    int[] b = new int[2];
                    b[0] = horas.getJSONArray("hasta").optInt(0);
                    b[1] = horas.getJSONArray("hasta").optInt(1);
                    if (GenericUtils.isTimeInRange(utc, a, b)) {
                        filterd.add(iti);
                    }
                }
            }
        });
        return filterd;
    }

    private static Map<Long, Date> startHour(Date evt, List<Itinerario> itinerarios, Long geo, Storage storage) throws StorageException {
        Map<Long, Date> inicios = new HashMap<>();
        for (Itinerario i : itinerarios) {
            List<Tramo> tramos = storage.getObjectsByQuery(Tramo.class, String.format("select * from tc_tramos where id in (select tramoid from tc_itinerario_tramo where itinerarioid = %d)", i.getId()));
            Date initial = evt;
            long t = tramos.stream().filter(item -> item.getGeofenceId() == geo).count();
            if (t > 0) {
                for (Tramo tr : tramos) {
                    initial = GenericUtils.addTimeToDate(initial, Calendar.MINUTE, tr.getMinTime() * -1);
                    if (tr.getGeofenceId() == geo) {
                        break;
                    }
                }
            }
            inicios.put(i.getId(), initial);
        }
        return inicios;
    }
//    public static void terminarSalidas(CacheManager cacheManager) {
//        try {
//            List<Salida> salidas = cacheManager.getStorage().getObjects(Salida.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
//                {
//                    add(new Condition.Equals("finished", false));
//                }
//            })));
//            for (Salida salida : salidas) {
//                if (salida.getEndingDate().getTime() > new Date().getTime()) {
//                    salida.setFinished(true);
//                    cacheManager.getStorage().updateObject(salida, new Request(
//                            new Columns.Exclude("id"),
//                            new Condition.Equals("id", salida.getId())));
//                }
//            }
//        } catch (StorageException ex) {
//            Logger.getLogger(TransporteUtils.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

    public static JSONObject obtenerTickets(long deviceId, Date from, Date to, Storage storage) throws StorageException {
        JSONObject response = new JSONObject("{}");
        Salida salida = storage.getObject(Salida.class,
                new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                    {
                        add(new Condition.Equals("deviceId", deviceId));
                        add(new Condition.Equals("finished", false));
                    }
                })));
        List<Salida> salidas = storage.getObjects(Salida.class,
                new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                    {
                        add(new Condition.Equals("deviceId", deviceId));
                        add(new Condition.Between("date", "from", from, "to", to));
                    }
                })));
        response.put("vueltas", salidas.size());
        List<Driver> choferes = new ArrayList<>();
        List<Long> permisos = storage.getPermissions(Device.class, Driver.class).stream()
                .filter((p) -> p.getOwnerId() == deviceId).map((p) -> p.getPropertyId()).collect(Collectors.toList());
        permisos.forEach((id) -> {
            try {
                choferes.add(storage.getObject(Driver.class,
                        new Request(new Columns.All(), new Condition.Equals("id", id))));
            } catch (StorageException ex) {
                Logger.getLogger(DeviceResource.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        response.put("choferes", choferes);
        if (salida != null) {
            response.put("salida", salida);
            List<Ticket> tickets = storage.getObjects(Ticket.class,
                    new Request(new Columns.All(), new Condition.Equals("salidaId", salida.getId())));
            response.put("ticket", tickets);
            List<Geofence> geoNames = new ArrayList<>();
            Map<Long, Object> otros = new HashMap<>();

            List<Salida> otras_salidas = storage.getObjects(Salida.class,
                    new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                        {
                            add(new Condition.Equals("scheduleId", salida.getScheduleId()));
                            add(new Condition.Between("date", "from", from, "to", to));
                        }
                    })));

            for (int i = 0; i < tickets.size(); i++) {
                Ticket ticket = tickets.get(i);
                Geofence g;
                List<Ticket> t;
                try {
                    g = storage.getObject(Geofence.class,
                            new Request(new Columns.All(), new Condition.Equals("id", ticket.getGeofenceId())));
                    geoNames.add(g);

                    t = storage.getObjects(Ticket.class,
                            new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                                {
                                    add(new Condition.Equals("geofenceId", g.getId()));
                                    add(new Condition.Between("enterTime", "from", from, "to", to));
                                }
                            })));

                    if (!t.isEmpty()) {

                    }
                } catch (StorageException ex) {
                    Logger.getLogger(DeviceResource.class.getName()).log(Level.SEVERE, null, ex);
                }

                response.put("geofencesNames", geoNames);
                response.put("geofencesPassed", otros);
            }
        }

        return response;
    }

    private static Date findFirstEventTime(long salidaId, long itinerarioId, CacheManager cacheManager) {
        try {
            Salida salida = cacheManager.getStorage().getObject(Salida.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                {
                    add(new Condition.Equals("id", salidaId));
                }
            })));
//            LOGGER.info(salida);
            if (salida == null) {
                return null;
            }
            if (salida.getFinished()) {
                return null;
            }

            Itinerario itinerario = cacheManager.getStorage().getObject(Itinerario.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                {
                    add(new Condition.Equals("id", itinerarioId));
                }
            })));
//            LOGGER.info(itinerario);
            final Itinerario b_i = itinerario;
            if (itinerario == null) {
                return null;
            }
            if (itinerario.getDays() <= 0) {
                return null;
            }

            List<Permission> permisos = cacheManager.getStorage().getPermissions(Itinerario.class, Tramo.class);

            List<Tramo> tramos = new ArrayList<>();
            permisos.forEach((p) -> {
                try {
                    Tramo t = cacheManager.getStorage().getObject(Tramo.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                        {
                            add(new Condition.Equals("id", p.getPropertyId()));
                        }
                    })));

                    if (t != null && p.getOwnerId() == b_i.getId()) {
                        tramos.add(t);
                    }
                } catch (StorageException ex) {
                    Logger.getLogger(TransporteUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            if (tramos.isEmpty()) {
                return null;
            }

//            LOGGER.info(tramos);
            if (!tramos.isEmpty()) {
                Tramo tramo = tramos.get(0);
                List<Event> events = cacheManager.getStorage().getObjects(Event.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                    {
                        add(new Condition.Equals("type", "geofenceExit"));
                        add(new Condition.Equals("deviceid", salida.getDeviceId()));
                        add(new Condition.Equals("geofenceid", tramo.getGeofenceId()));
//                        add(new Condition.Between("eventtime", "from", GenericUtils.addTimeToDate(salida.getDate(), Calendar.MINUTE, -15), "to", new Date()));

                    }
                })));
                Event event = events.get(events.size() - 1);
//                LOGGER.info(event);

                if (event == null) {
                    return null;
                }
                LOGGER.info("Esta debe de ser la hora " + event.getEventTime());
                return event.getEventTime();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
//        LOGGER.info("Algo salio mal y no se encontro la hora inicial");
        return null;
    }

    /**
     * Revisar si existe unsa salida pendiente, del mismo dispositivo en la
     * misma geocerca. Cancelar la salida, es un falso
     *
     * @param geofenceId id de la geocerca donde se inicia la salida.
     * @param deviceId The second integer.
     * @param cacheManager The second integer.
     */
    public static void cleanSalidas(long geofenceId, long deviceId, CacheManager cacheManager) {
        ProccessLogger logger = new ProccessLogger(LOGGER);
        try {
            logger.info("Revisando salidas invalidas");
            Salida salida = cacheManager.getStorage().getObject(Salida.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                {
                    add(new Condition.Equals("finished", false));
                    add(new Condition.Equals("deviceId", deviceId));
                    add(new Condition.Equals("geofenceid", geofenceId));
                }
            })));

            if (salida == null) {
                logger.info("Sin salidas anteriores");
                return;
            }

            salida.setFinished(true);
            salida.setValid(false);
            cacheManager.getStorage().updateObject(salida, new Request(
                    new Columns.Exclude("id"),
                    new Condition.Equals("id", salida.getId())));
            logger.info("Anulando salida: " + salida);

        } catch (StorageException ex) {
            ex.printStackTrace();
            Logger.getLogger(TransporteUtils.class.getName()).log(Level.INFO, "Algo salio mal");
        }
    }

    public static void generarSalida(long deviceId, long itinerario, Date start, Storage storage) throws ParseException {
        try {

            //obtener dispositivo
            Device device = storage.getObject(Device.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                {
                    add(new Condition.Equals("id", deviceId));
                }
            })));

            if (device == null) {
                return;
            }

            Itinerario itinerarioSelected = storage.getObject(Itinerario.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                {
                    add(new Condition.Equals("id", itinerario));
                }
            })));

            //encontrar puntos de control
            final Itinerario finalItinerarioSelected = itinerarioSelected;
            List<Permission> permisos = storage.getPermissions(Itinerario.class, Tramo.class);

            List<Tramo> tramos = new ArrayList<>();
            permisos.forEach((p) -> {
                try {
                    Tramo t = storage.getObject(Tramo.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                        {
                            add(new Condition.Equals("id", p.getPropertyId()));
                        }
                    })));

                    if (t != null && p.getOwnerId() == itinerario) {
//                        LOGGER.info(p);
//                        LOGGER.info(t);
                        tramos.add(t);
                    }
                } catch (StorageException ex) {
                    Logger.getLogger(TransporteUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            if (tramos.isEmpty()) {
                return;
            }

            //crear nueva salida
            Date today = new Date();
            Date endDate = today;
            Salida newSalida = new Salida();
            newSalida.setValid(true);
            newSalida.setDate(today);
            newSalida.setDeviceId(deviceId);
            newSalida.setFinished(false);
            newSalida.setScheduleId(itinerarioSelected.getId());
            for (Tramo tramo : tramos) {
                endDate = GenericUtils.addTimeToDate(endDate, Calendar.MINUTE, tramo.getMinTime());
            }
            newSalida.setEndingDate(endDate);
            newSalida.setGroupId(device.getGroupId());
            newSalida.setSubrouteId(itinerarioSelected.getSubrouteId());
            newSalida.setId(storage.addObject(newSalida, new Request(new Columns.Exclude("id"))));

            //crear tickets
            Date ticketStart = start;

            boolean isFirstTicket = true;

            Ticket ticket = null;
            for (Tramo tramo : tramos) {
                ticket = new Ticket();
                ticketStart = GenericUtils.addTimeToDate(ticketStart, Calendar.MINUTE, tramo.getMinTime());
                ticket.setExpectedTime(ticketStart);
                ticket.setGeofenceId(tramo.getGeofenceId());
                ticket.setPunishment(tramo.getPunishment());
                ticket.setSalidaId(newSalida.getId());
                ticket.setTramo(tramo.getId());
                ticket.setId(storage.addObject(ticket, new Request(new Columns.Exclude("id"))));

            }
            return;
        } catch (StorageException ex) {
            ex.printStackTrace();
            Logger.getLogger(TransporteUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

class ProccessLogger {

    org.slf4j.Logger LOGGER;
    String code;

    public ProccessLogger(org.slf4j.Logger LOGGER) {
        this.LOGGER = LOGGER;
        this.code = GenericUtils.generateRandomCode(10);
    }

    public void info(String msg) {
        LOGGER.info(String.format("[%s] %s", code, msg));
    }
}
