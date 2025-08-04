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
 * @author K
 */
public class TransporteUtils {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TransporteUtils.class);

    public static void generarSalida(long deviceId, long geofenceId, Date time, CacheManager cacheManager) {
        try {
            //pasos
            cacheManager.getDeviceLog().log(deviceId, "Nueva salida");
            cacheManager.getDeviceLog().log(deviceId, String.format("geofenceId: %s, time: %s", geofenceId, time.toString()));
            //1
            verifyActiveExits(cacheManager.getStorage(), deviceId);
            cacheManager.getDeviceLog().log(deviceId, "Paso 1 correcto: sin salida activa");
            //2
            Device device = getDevice(cacheManager.getStorage(), deviceId);
            cacheManager.getDeviceLog().log(deviceId, "Paso 2 correcto: el dispositivo existe");
            cacheManager.getDeviceLog().log(deviceId, "Paso 2 " + device.toString());
            //3
            Group group = getGroup(cacheManager.getStorage(), device.getGroupId());
            cacheManager.getDeviceLog().log(deviceId, "Paso 3 correcto: el grupo existe");
            cacheManager.getDeviceLog().log(deviceId, "Paso 3 " + group.toString());
            //4
            List<Subroute> subroutes = getSubroutes(cacheManager.getStorage(), group.getId());
            cacheManager.getDeviceLog().log(deviceId, "Paso 4 correcto: obtenidas subrutas del grupo " + group.getId());
            cacheManager.getDeviceLog().log(deviceId, "Paso 4 " + objectListToLogString(subroutes));
            //5
            List<Itinerario> schedules = getSchedules(cacheManager.getStorage(), subroutes, geofenceId);
            cacheManager.getDeviceLog().log(deviceId, "Paso 5 correcto: obtenidos los itinerarios disparados por la geocerca " + geofenceId);
            cacheManager.getDeviceLog().log(deviceId, "Paso 5 " + objectListToLogString(schedules));
            //6
            Itinerario selectedSchedule = getSelectedSchedule(cacheManager.getStorage(), group, schedules, deviceId, geofenceId, time, cacheManager);
            cacheManager.getDeviceLog().log(deviceId, "Paso 6 correcto: seleccionar un itinerario adecuado");
            cacheManager.getDeviceLog().log(deviceId, "Paso 6 " + selectedSchedule.toString());
            //7
            List<Tramo> sections = getSections(cacheManager.getStorage(), selectedSchedule);
            cacheManager.getDeviceLog().log(deviceId, "Paso 7 correcto: obtener los tramos del itinerario");
            cacheManager.getDeviceLog().log(deviceId, "Paso 7 " + objectListToLogString(sections));
            //8
            Salida dispatch = createDispatch(cacheManager.getStorage(), group, selectedSchedule, sections, time, deviceId, geofenceId);
            cacheManager.getDeviceLog().log(deviceId, "Paso 8 correcto: generar una nueva salida");
            cacheManager.getDeviceLog().log(deviceId, "Paso 8 " + dispatch.toString());
            //9
            List<Ticket> tickets = createTickets(cacheManager.getStorage(), sections, selectedSchedule, dispatch, dispatch.getDate(), geofenceId);
            cacheManager.getDeviceLog().log(deviceId, "Paso 9 correcto: crear los tickets para la salida");
            cacheManager.getDeviceLog().log(deviceId, "Paso 9 " + objectListToLogString(tickets));
            //10
            boolean isFirstTicket = geofenceId == sections.get(0).getGeofenceId();
            if (!isFirstTicket) {
                updateOldTramos(dispatch.getId(), selectedSchedule.getId(), cacheManager, geofenceId);
            }
            //11
            if (sections.stream().anyMatch(Tramo::getForceBacktrackSearch)) {
                updateForcedTramos(dispatch.getId(), selectedSchedule.getId(), cacheManager, geofenceId);
            }
            //12
            updateSalida(deviceId, geofenceId, selectedSchedule.getForceCreateOnEnter()?dispatch.getDate():time, cacheManager, true);
        } catch (Exception e) {
            cacheManager.getDeviceLog().log(deviceId, String.format("Error al generar nueva salida %s", e.getMessage()));
            return;
        }
    }

    private static void verifyActiveExits(Storage storage, long deviceId) throws StorageException {
        List<Salida> salidas = storage.getObjectsByQuery(Salida.class, String.format("SELECT * FROM tc_salidas WHERE finished=false AND deviceId = %s", deviceId));
        if (!salidas.isEmpty()) throw new RuntimeException("Salida activa encontrada");
    }

    private static Device getDevice(Storage storage, long deviceId) throws StorageException {
        List<Device> devices = storage.getObjectsByQuery(Device.class, String.format("SELECT * FROM tc_devices WHERE id = %s", deviceId));
        if (devices.isEmpty()) throw new RuntimeException("Dispositivo no encontrado en base de datos");
        return devices.get(0);
    }

    private static Group getGroup(Storage storage, long groupId) throws StorageException {
        List<Group> groups = storage.getObjectsByQuery(Group.class, String.format("SELECT * FROM tc_groups WHERE id = %s", groupId));
        if (groups.isEmpty()) throw new RuntimeException("Grupo no encontrado");
        return groups.get(0);
    }

    private static List<Subroute> getSubroutes(Storage storage, long groupId) throws StorageException {
        List<Subroute> subroutes = storage.getObjectsByQuery(Subroute.class, String.format("SELECT * FROM tc_subroutes WHERE groupId=%s", groupId));
        return subroutes;
    }

    private static List<Itinerario> getSchedules(Storage storage, List<Subroute> subroutes, long geofenceId) throws StorageException {
        Set<Long> subrouteIds = subroutes.stream().map(Subroute::getId).collect(Collectors.toSet());
        List<Itinerario> schedules = storage.getObjectsByQuery(Itinerario.class, String.format("SELECT * FROM tc_itinerarios WHERE geofenceId=%s", geofenceId));
        if (schedules.isEmpty()) throw new RuntimeException("No se encontraron itinerarios");

        int todayDayValue = GenericUtils.getDayValue(
                GenericUtils.addTimeToDate(new Date(), Calendar.HOUR_OF_DAY, -8)
        );

        List<Itinerario> todaySchedules = schedules.stream()
                .filter(it -> subrouteIds.contains(it.getSubrouteId()))
                .filter(it -> GenericUtils.isDaySelected(it.getDays(), todayDayValue))
                .collect(Collectors.toList());

        return todaySchedules;
    }

    private static Itinerario getSelectedSchedule(Storage storage, Group group, List<Itinerario> schedules, long deviceId, long geofenceId, Date time, CacheManager cacheManager) throws StorageException, ParseException, IOException {
        Itinerario itinerarioSelected = null;

        if (group.hasAttribute("vp") && Boolean.TRUE.equals(group.getAttributes().get("vp"))) {
            String query = String.format(
                    "select * from tc_salidas where deviceid=%s and valid=true and date(tc_salidas.date) = curdate()",
                    deviceId
            );

            List<Salida> salidasToday = storage.getObjectsByQuery(Salida.class, query);

            if (salidasToday.isEmpty()) {
                cacheManager.getDeviceLog().log(deviceId, "Lista de salida para el día de hoy vacías en dispositivo " +deviceId);
                itinerarioSelected = findClosestObject(schedules, new Date(), 1, storage, true);

                if (itinerarioSelected == null)
                    throw new RuntimeException("No se encontro un itinerario valido para el grupo de villas del pedregal " + group.getId());

                HoraSalida horaBase = storage.getObject(HoraSalida.class,
                        new Request(new Columns.All(), new Condition.Equals("id", itinerarioSelected.getHorasId())));

                if (horaBase != null) {
                    List<HoraSalida> horas = storage.getObjects(HoraSalida.class,
                            new Request(new Columns.All(), new Condition.Equals("group_uuid", horaBase.getGroup_uuid())));

                    if (!horas.isEmpty()) {
                        itinerarioSelected.getAttributes().put("horaFinal", horas.get(0).getHour());
                    }
                }
            } else {
                cacheManager.getDeviceLog().log(deviceId, "Lista de salida para el día de hoy NO vacías en dispositivo " +deviceId);
                Salida firstSalida = salidasToday.get(0);
                Itinerario salidaItinerario = storage.getObject(Itinerario.class,
                        new Request(new Columns.All(), new Condition.Equals("id", firstSalida.getScheduleId())));

                if (salidaItinerario == null)
                    throw new RuntimeException("No se encontro una salida generada anteriormente para villas del pedregal que usar como base");

                if (salidaItinerario.getGeofenceId() == geofenceId) {
                    itinerarioSelected = salidaItinerario;
                } else {
                    Itinerario origen = storage.getObject(Itinerario.class,
                            new Request(new Columns.All(), new Condition.Equals("id", firstSalida.getScheduleId())));

                    if (origen != null) {
                        itinerarioSelected = storage.getObject(Itinerario.class,
                                new Request(new Columns.All(), new Condition.Equals("horasIdRel", origen.getHorasId())));
                    }
                }

                if (itinerarioSelected == null)
                    throw new RuntimeException("No se encontro un itinerario valido para el grupo de villas del pedregal " + group.getId());

                HoraSalida horaBase = storage.getObject(HoraSalida.class,
                        new Request(new Columns.All(), new Condition.Equals("id", itinerarioSelected.getHorasId())));

                if (horaBase != null) {
                    List<HoraSalida> horas = storage.getObjects(HoraSalida.class,
                            new Request(new Columns.All(), new Condition.Equals("group_uuid", horaBase.getGroup_uuid())));

                    int salidaIndex = salidasToday.size() / 2;

                    if (salidaIndex >= horas.size())
                        throw new RuntimeException("La salida encontrada esta fuera del indice valido");

                    itinerarioSelected.getAttributes().put("horaFinal", horas.get(salidaIndex).getHour());
                }

            }

        } else {
            itinerarioSelected = findClosestObject(schedules, time, 3, storage, false);
        }

        if (itinerarioSelected == null)
            throw new RuntimeException("No se encontro un itinerario valido para el grupo de villas del pedregal " + group.getId());

        return itinerarioSelected;
    }

    private static List<Tramo> getSections(Storage storage, Itinerario selectedSchedule) throws StorageException {
        List<Permission> permisos = storage.getPermissions(Itinerario.class, Tramo.class);

        long itinerarioId = selectedSchedule.getId();

        List<Long> tramoIds = permisos.stream()
                .filter(p -> p.getOwnerId() == itinerarioId)
                .map(Permission::getPropertyId)
                .collect(Collectors.toList());

        String idList = tramoIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        String query = String.format("SELECT * FROM tc_tramos WHERE id IN (%s)", idList);

        return storage.getObjectsByQuery(Tramo.class, query);
    }

    private static Salida createDispatch(Storage storage, Group group, Itinerario schedule, List<Tramo> sections, Date time, long deviceId, long geofenceId) throws StorageException {
        Date salidaDate = new Date();

        if (group.hasAttribute("vp") && Boolean.TRUE.equals(group.getAttributes().get("vp"))) {
            Date horaFinal = (Date) schedule.getAttributes().get("horaFinal");

            Calendar cal = Calendar.getInstance();
            cal.setTime(salidaDate);
            cal.set(Calendar.HOUR_OF_DAY, horaFinal.getHours());
            cal.set(Calendar.MINUTE, horaFinal.getMinutes());
            cal.set(Calendar.SECOND, 0);

            salidaDate = cal.getTime();
        } else {
            salidaDate = time;
            salidaDate.setSeconds(0);
        }

        if (schedule.getForceCreateOnEnter()) {
            List<Event> enteredEvents = storage.getObjectsByQuery(
                    Event.class,
                    String.format(
                            "SELECT * FROM tc_events WHERE deviceid = %s AND TYPE = 'geofenceEnter' AND geofenceid = %d ORDER BY eventtime DESC LIMIT 1",
                            deviceId,
                            geofenceId
                    )
            );

            if (!enteredEvents.isEmpty()) {
                salidaDate = enteredEvents.get(0).getEventTime();
            }
        }

        Date endDate = salidaDate;

        for (Tramo tramo : sections) {
            endDate = GenericUtils.addTimeToDate(endDate, Calendar.MINUTE, tramo.getMinTime());
        }

        Salida newSalida = new Salida();
        newSalida.setGeofenceId(geofenceId);
        newSalida.setValid(true);
        newSalida.setDate(salidaDate);
        newSalida.setDeviceId(deviceId);
        newSalida.setFinished(false);
        newSalida.setScheduleId(schedule.getId());
        newSalida.setEndingDate(endDate);
        newSalida.setGroupId(group.getId());
        newSalida.setSubrouteId(schedule.getSubrouteId());
        newSalida.setManual(false);

        long newSalidaId = storage.addObject(
                newSalida,
                new Request(new Columns.Exclude("id"))
        );

        newSalida.setId(newSalidaId);
        return newSalida;
    }

    private static List<Ticket> createTickets(Storage storage, List<Tramo> sections, Itinerario schedule, Salida dispatch, Date time, long geofenceId) throws StorageException {
        Date now = new Date();
        Date ticketStart = schedule.hasAttribute("horaFinal")
                ? new Date(((Date) schedule.getAttributes().get("horaFinal")).getTime())
                : new Date(time.getTime());

        ticketStart.setMonth(now.getMonth());
        ticketStart.setDate(now.getDate());
        ticketStart.setSeconds(0); // normalize seconds too if needed

        boolean isFirstTicket = geofenceId == sections.get(0).getGeofenceId();

        if (!isFirstTicket) {
            Date possibleStart = findFirstEventTime(dispatch.getId(), schedule.getId(), storage);

            if (possibleStart != null) {
                ticketStart = new Date(possibleStart.getTime());
            } else {
                for (Tramo tramo : sections) {
                    ticketStart = GenericUtils.addTimeToDate(ticketStart, Calendar.MINUTE, -tramo.getMinTime());
                    if (tramo.getGeofenceId() == geofenceId) break;
                }
            }

            if (schedule.getHorasId() > 0) {
                Date adjustedStart = findClosestHour(ticketStart, schedule, storage);
                if (adjustedStart != null) {
                    ticketStart = adjustedStart;
                }
            }

            // Update departure with recalculated time
            dispatch.setDate(ticketStart);
            storage.updateObject(
                    dispatch,
                    new Request(new Columns.Exclude("id"), new Condition.Equals("id", dispatch.getId()))
            );
        }

        List<Ticket> tickets = new ArrayList<>();
        for (Tramo tramo : sections) {
            ticketStart = GenericUtils.addTimeToDate(ticketStart, Calendar.MINUTE, tramo.getMinTime());

            Ticket ticket = new Ticket();
            ticket.setExpectedTime(ticketStart);
            ticket.setGeofenceId(tramo.getGeofenceId());
            ticket.setPunishment(tramo.getPunishment());
            ticket.setSalidaId(dispatch.getId());
            ticket.setTramo(tramo.getId());

            ticket.setId(storage.addObject(ticket, new Request(new Columns.Exclude("id"))));
            tickets.add(ticket);
        }
        return tickets;
    }

    private static String objectListToLogString(List<?> list) {
        if (list == null || list.isEmpty()) {
            return "[]";
        }

        return list.stream()
                .map(Object::toString)
                .collect(Collectors.joining(",\n", "[\n", "\n]"));
    }

    public static Itinerario findClosestObject(List<Itinerario> objects, Date date, int rangeInMinutes, Storage storage, boolean lookupInitial)
            throws ParseException, StorageException, IOException {

        if (objects == null || objects.isEmpty()) return null;

        Itinerario closestObject = null;
        long closestDifference = Long.MAX_VALUE;

        List<Itinerario> filteredByHours = filterByHours(objects);
        Map<Long, Date> startTimes = startHour(date, objects, objects.get(0).getGeofenceId(), storage);

        for (Itinerario itinerario : filteredByHours) {
            if (itinerario.getHorasId() > 0) {
                HoraSalida horaBase = storage.getObject(HoraSalida.class,
                        new Request(new Columns.All(), new Condition.Equals("id", itinerario.getHorasId())));
                if (horaBase == null) continue;

                List<HoraSalida> horas = storage.getObjects(HoraSalida.class,
                        new Request(new Columns.All(), new Condition.Equals("group_uuid", horaBase.getGroup_uuid())));

                for (HoraSalida h : horas) {
                    Date hora = (Date) h.getHour().clone();
                    hora.setDate(date.getDate());
                    hora.setYear(date.getYear());
                    hora.setMonth(date.getMonth());

                    Date ref = lookupInitial ? startTimes.getOrDefault(itinerario.getId(), date) : date;
                    long difference = Math.abs(ref.getTime() - hora.getTime());

                    if (difference < closestDifference) {
                        closestDifference = difference;
                        closestObject = itinerario;
                        closestObject.getAttributes().put("horaFinal", hora);
                    }
                }
            } else if (closestObject == null) {
                closestObject = itinerario; // fallback when no horaId
            }
        }

        return closestObject;
    }

    private static List<Itinerario> filterByHours(List<Itinerario> itinerarios) throws ParseException, IOException {
        List<Itinerario> filtered = new ArrayList<>();
        int[] utc = GenericUtils.fetchUTCDate();
        int today = GenericUtils.getDayValue(new Date());

        for (Itinerario iti : itinerarios) {
            if (!iti.hasAttribute("hours")) continue;

            String rawHours = iti.getAttributes().get("hours").toString()
                    .replaceAll("1=", "\"1\":")
                    .replaceAll(" 2=", " \"2\":")
                    .replaceAll(" 4=", " \"4\":")
                    .replaceAll(" 8=", " \"8\":")
                    .replaceAll(" 16=", " \"16\":")
                    .replaceAll(" 32=", " \"32\":")
                    .replaceAll(" 64=", " \"64\":")
                    .replaceAll("desde=", "\"desde\":")
                    .replaceAll("hasta=", "\"hasta\":")
                    .replaceAll("=", ":");

            JSONObject obj = new JSONObject(rawHours);
            if (!obj.has(String.valueOf(today))) continue;

            JSONObject horas = obj.getJSONObject(String.valueOf(today));
            int[] desde = {
                    horas.getJSONArray("desde").optInt(0),
                    horas.getJSONArray("desde").optInt(1)
            };
            int[] hasta = {
                    horas.getJSONArray("hasta").optInt(0),
                    horas.getJSONArray("hasta").optInt(1)
            };

            if (GenericUtils.isTimeInRange(utc, desde, hasta)) {
                filtered.add(iti);
            }
        }

        return filtered;
    }

    private static Map<Long, Date> startHour(Date eventDate, List<Itinerario> itinerarios, Long geofenceId, Storage storage)
            throws StorageException {

        Map<Long, Date> startTimes = new HashMap<>();

        for (Itinerario iti : itinerarios) {
            List<Tramo> tramos = storage.getObjectsByQuery(Tramo.class,
                    String.format("SELECT * FROM tc_tramos WHERE id IN (SELECT tramoid FROM tc_itinerario_tramo WHERE itinerarioid = %d)", iti.getId()));

            Date adjusted = (Date) eventDate.clone();
            for (Tramo tramo : tramos) {
                adjusted = GenericUtils.addTimeToDate(adjusted, Calendar.MINUTE, -tramo.getMinTime());
                if (tramo.getGeofenceId() == geofenceId) {
                    break;
                }
            }

            startTimes.put(iti.getId(), adjusted);
        }

        return startTimes;
    }

    // old methods

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

            Ticket ticket = tickets.stream().filter((t) -> t.getGeofenceId() == geofenceId && (first ? t.getEnterTime() == null : t.getExitTime() == null)).findFirst().orElse(null);
            logger.info("Ticket " + ticket);
            if (ticket == null) {
                logger.info("No ticket");
                return;
            }
            boolean isLastTicket = ticket.getId() == tickets.get(tickets.size() - 1).getId();

            int time = 29;
            Group device_group = cacheManager.getStorage().getObject(Group.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                {
                    add(new Condition.Equals("id", salida.getGroupId()));
                }
            })));

            if (device_group != null && device_group.getAttributes().containsKey("vp") && (boolean) device_group.getAttributes().get("vp")) {
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
            int origin = tickets.size();

            Ticket ticketStart = tickets.stream().filter((t) -> t.getGeofenceId() == geofenceId).findFirst().orElse(null);

            if (ticketStart != null) {
                int originIndexInList = tickets.indexOf(ticketStart);
                if (originIndexInList != -1) {
                    origin = originIndexInList;

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
                if (ticket.getGeofenceId() == salida.getGeofenceId()) {
                    stop = true;
                }

                if (stop) {
                    break;
                }
            }

        } catch (StorageException ex) {
            ex.printStackTrace();
            Logger.getLogger(TransporteUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void updateForcedTramos(long salidaId, long itinerarioId, CacheManager cacheManager, long geofenceId) {
        LOGGER.info("update forced for salidaId=" + salidaId + ", scheduleId=" + itinerarioId);
        try {
            boolean stop = false;
            Salida salida = cacheManager.getStorage().getObject(Salida.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                {
                    add(new Condition.Equals("id", salidaId));
                }
            })));

            if (salida == null) {
                return;
            }
            LOGGER.info(salida.toString());
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
            LOGGER.info(itinerario.toString());
            if (itinerario.getDays() <= 0) {
                return;
            }

            List<Ticket> tickets = cacheManager.getStorage().getObjects(Ticket.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                {
                    add(new Condition.Equals("salidaId", salida.getId()));
                }
            })));

            for (Ticket ticket : tickets) {
                Tramo tramo = cacheManager.getStorage().getObject(Tramo.class, new Request(new Columns.All(), new Condition.Equals("id", ticket.getTramo())));
                if (!tramo.getForceBacktrackSearch()) continue;
                if (ticket.getEnterTime() == null) {
                    List<Event> events = cacheManager.getStorage().getObjects(Event.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                        {
                            add(new Condition.Equals("type", "geofenceEnter"));
                            add(new Condition.Equals("deviceid", salida.getDeviceId()));
                            add(new Condition.Equals("geofenceid", ticket.getGeofenceId()));
                            add(new Condition.Between("eventtime", "from", GenericUtils.addTimeToDate(ticket.getExpectedTime(), Calendar.MINUTE, -90), "to", GenericUtils.addTimeToDate(ticket.getExpectedTime(), Calendar.MINUTE, 10)));
                        }
                    })));
                    LOGGER.info(events.toString());
                    Event event = events.get(events.size() - 1);
                    LOGGER.info(event.toString());

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

                    LOGGER.info("updated enterTime for: ticket= " + ticket.getId() + ", tramo=" + tramo.getId() + ", event=" + event.getId());
                }

                if (ticket.getExitTime() == null) {
                    Event event = cacheManager.getStorage().getObject(Event.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                        {
                            add(new Condition.Equals("type", "geofenceExit"));
                            add(new Condition.Equals("deviceid", salida.getDeviceId()));
                            add(new Condition.Equals("geofenceid", ticket.getGeofenceId()));
                            add(new Condition.Between("eventtime", "from", GenericUtils.addTimeToDate(ticket.getExpectedTime(), Calendar.MINUTE, -90), "to", GenericUtils.addTimeToDate(ticket.getExpectedTime(), Calendar.MINUTE, 10)));
                        }
                    })));

                    if (event == null) {
                        continue;
                    }

                    ticket.setExitTime(event.getEventTime());

                    cacheManager.getStorage().updateObject(ticket, new Request(
                            new Columns.Exclude("id"),
                            new Condition.Equals("id", ticket.getId())));

                    LOGGER.info("updated exitTime for: ticket= " + ticket.getId() + ", tramo=" + tramo.getId() + ", event=" + event.getId());
                }
            }

        } catch (StorageException ex) {
            ex.printStackTrace();
            Logger.getLogger(TransporteUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static Date findFirstEventTime(long salidaId, long itinerarioId, Storage storage) {
        try {
            // Load Salida
            Salida salida = storage.getObject(Salida.class, new Request(
                    new Columns.All(), new Condition.Equals("id", salidaId)
            ));

            if (salida == null || salida.getFinished()) {
                return null;
            }

            // Load Itinerario
            Itinerario itinerario = storage.getObject(Itinerario.class, new Request(
                    new Columns.All(), new Condition.Equals("id", itinerarioId)
            ));

            if (itinerario == null || itinerario.getDays() <= 0) {
                return null;
            }

            // Fetch tramos (segments) associated with the itinerario via permissions
            List<Permission> permisos = storage.getPermissions(Itinerario.class, Tramo.class);

            List<Long> tramoIds = permisos.stream()
                    .filter(p -> p.getOwnerId() == itinerario.getId())
                    .map(Permission::getPropertyId)
                    .collect(Collectors.toList());

            if (tramoIds.isEmpty()) {
                return null;
            }

            String idList = tramoIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));

            List<Tramo> tramos = storage.getObjectsByQuery(Tramo.class,
                    String.format("SELECT * FROM tc_tramos WHERE id IN (%s)", idList)
            );

            if (tramos.isEmpty()) {
                return null;
            }

            // Look for the last geofenceExit event for each tramo
            for (Tramo tramo : tramos) {
                List<Event> events = storage.getObjects(Event.class, new Request(
                        new Columns.All(),
                        Condition.merge(Arrays.asList(
                                new Condition.Equals("type", "geofenceExit"),
                                new Condition.Equals("deviceid", salida.getDeviceId()),
                                new Condition.Equals("geofenceid", tramo.getGeofenceId())
                        ))
                ));

                if (!events.isEmpty()) {
                    Event latestEvent = events.get(events.size() - 1);
                    return latestEvent.getEventTime();
                }
            }

        } catch (StorageException e) {
            e.printStackTrace(); // or use a centralized error handler
        }

        return null;
    }

    /**
     * Revisar si existe unsa salida pendiente, del mismo dispositivo en la
     * misma geocerca. Cancelar la salida, es un falso
     *
     * @param geofenceId   id de la geocerca donde se inicia la salida.
     * @param deviceId     The second integer.
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

            if (salida == null || salida.getManual()) {
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

    public static void finishOldSalidas(long geofenceId, long deviceId, CacheManager cacheManager) {
        try {
            ProccessLogger logger = new ProccessLogger(LOGGER);
            List<Itinerario> itinerarios = cacheManager.getStorage().getObjectsByQuery(Itinerario.class, "SELECT * FROM tc_itinerarios WHERE geofenceId = " + geofenceId + " and (days & (1 << (WEEKDAY(CURDATE())))) != 0;");
            //List<Itinerario> itinerarios = cacheManager.getStorage().getObjects(Itinerario.class, new Request(new Columns.All(), new Condition.Equals("geofenceId", geofenceId)));
            if (itinerarios.isEmpty()) {
                return;
            }

            List<Salida> salidas = cacheManager.getStorage().getObjects(Salida.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                {
                    add(new Condition.Equals("finished", false));
                    add(new Condition.Equals("deviceId", deviceId));
                }
            })));

            if (salidas.isEmpty()) {
                return;
            }
            for (Salida salida : salidas) {
                salida.setFinished(true);
                cacheManager.getStorage().updateObject(salida, new Request(
                        new Columns.Exclude("id"),
                        new Condition.Equals("id", salida.getId())));
                logger.info("Anulando salida: " + salida);
            }

        } catch (StorageException e) {
            cacheManager.getDeviceLog().log(deviceId, "Error anulando salidas anteriores");
        }
    }

    public static void generarSalida(long deviceId, long itinerario, Date start, Storage storage, boolean manual) throws ParseException {
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

            Subroute subruta = storage.getObject(Subroute.class, new Request(new Columns.All(), new Condition.Equals("id", itinerarioSelected.getSubrouteId())));

            //crear nueva salida
            Date today = new Date();
            Date endDate = today;

            if ((today.getDate() < start.getDate()) && manual) {
                start.setDate(today.getDate());
            }
            if (manual) {
                endDate = start;
            }

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
            if (subruta != null) {
                newSalida.setGroupId(subruta.getGroupId());
            }
            newSalida.setSubrouteId(itinerarioSelected.getSubrouteId());
            newSalida.setManual(manual);
            newSalida.setId(storage.addObject(newSalida, new Request(new Columns.Exclude("id"))));

            //crear tickets
            Date ticketStart = start;

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
        } catch (StorageException ex) {
            ex.printStackTrace();
            Logger.getLogger(TransporteUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static Date findClosestHour(Date base, Itinerario obj, Storage storage) throws StorageException {
        long closestDifference = Long.MAX_VALUE;
        Date closest = null;
        for (HoraSalida h : storage.getObjects(HoraSalida.class, new Request(new Columns.All(), new Condition.Equals("group_uuid", storage.getObject(HoraSalida.class, new Request(new Columns.All(), new Condition.Equals("id", obj.getHorasId()))).getGroup_uuid())))) {
            h.getHour().setDate(new Date().getDate());
            h.getHour().setYear(new Date().getYear());
            h.getHour().setMonth(new Date().getMonth());

            LOGGER.info(h.toString());
            long difference = Math.abs(base.getTime() - h.getHour().getTime());

//                    if (difference <= rangeInMinutes * 60000 && difference < closestDifference) {
            if (difference < closestDifference) {
                closest = h.getHour();
                closestDifference = difference;
            }
        }

        return closest;
    }

    public static void cancelarSalida(long salidaId, Storage storage) {
        try {
            Salida salida = storage.getObject(Salida.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                {
                    add(new Condition.Equals("id", salidaId));
                }
            })));

            if (salida != null) {
                salida.setFinished(true);
                salida.setValid(false);
                storage.updateObject(salida, new Request(
                        new Columns.Exclude("id"),
                        new Condition.Equals("id", salida.getId())));
            }
        } catch (Exception e) {
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
