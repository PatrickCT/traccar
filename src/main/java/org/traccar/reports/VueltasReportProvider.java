/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.reports;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import org.apache.poi.ss.util.WorkbookUtil;
import org.traccar.config.Config;
import org.traccar.config.Keys;
import org.traccar.helper.model.UserUtil;
import org.traccar.model.HoraSalida;
import org.traccar.model.Itinerario;
import org.traccar.model.Salida;
import org.traccar.model.Subroute;
import org.traccar.model.Ticket;
import org.traccar.reports.common.ReportUtils;
import org.traccar.reports.model.VueltaReportItem;
import org.traccar.session.cache.CacheManager;
import org.traccar.storage.Storage;
import org.traccar.storage.StorageException;
import org.traccar.storage.query.Columns;
import org.traccar.storage.query.Condition;
import org.traccar.storage.query.Request;
import org.traccar.utils.GenericUtils;

/**
 *
 * @author K
 */
public class VueltasReportProvider {

    private final Config config;
    private final ReportUtils reportUtils;
    private final Storage storage;

    @Inject
    public VueltasReportProvider(Config config, ReportUtils reportUtils, Storage storage) {
        this.config = config;
        this.reportUtils = reportUtils;
        this.storage = storage;
    }

    @Inject
    private CacheManager cacheManager;

    public Collection<VueltaReportItem> getObjects(long userId, Collection<Long> deviceIds, Collection<Long> groupIds,
            Date from, Date to) throws StorageException, ParseException {
        var server = reportUtils.getPermissionsService().getServer();
        var user = reportUtils.getPermissionsService().getUser(userId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ArrayList<VueltaReportItem> result = new ArrayList<>();
        sdf.setTimeZone(UserUtil.getTimezone(server, user));

        for (long groupid : groupIds) {
//            System.out.println("Grupo " + groupid);
            List<Subroute> subrutas = new ArrayList<>();
            try {
                subrutas.addAll(storage.getObjects(Subroute.class, new Request(new Columns.All(), new Condition.Equals("groupId", groupid))));
            } catch (StorageException ex) {
                Logger.getLogger(VueltasReportProvider.class.getName()).log(Level.SEVERE, null, ex);
            }
//            System.out.println("Subrutas del grupo " + groupid + ": " + subrutas);
            List<Itinerario> itinerarios = new ArrayList<>();
            for (Subroute subruta : subrutas) {
                try {
                    itinerarios.addAll(storage.getObjects(Itinerario.class, new Request(new Columns.All(), new Condition.Equals("subrouteId", subruta.getId()))));
                } catch (StorageException ex) {
                    Logger.getLogger(VueltasReportProvider.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
//            System.out.println("Itinerarios del grupo " + groupid + ": " + itinerarios);
            for (Date date : GenericUtils.getDatesBetween(from, to)) {
//                System.out.println(date);
                for (Itinerario itinerario : itinerarios) {
                    VueltaReportItem vri = new VueltaReportItem();
                    vri.setItinerarioId((int) itinerario.getId());
                    List<HoraSalida> horas = new ArrayList<>();
                    if (itinerario.getHorasId() > 0) {
                        System.out.println("Itinerario con tabla de horas " + itinerario.getHorasId());
                        HoraSalida hora = storage.getObject(HoraSalida.class, new Request(new Columns.All(), new Condition.Equals("id", itinerario.getHorasId())));
                        horas.addAll(storage.getObjects(HoraSalida.class, new Request(new Columns.All(), new Condition.Equals("name", hora.getName()))));

//                        System.out.println("Tabla de horas \n\r " + horas);

                        List<VueltaReportItem.VueltaDataItem> objs = new ArrayList<>();

                        for (HoraSalida h : horas) {
                            Calendar calendar_date = Calendar.getInstance();
                            calendar_date.setTime(date);
                            Calendar calendar_hour_salida = Calendar.getInstance();
                            calendar_hour_salida.setTime(h.getHour());

                            calendar_hour_salida.set(Calendar.YEAR, calendar_date.get(Calendar.YEAR));
                            calendar_hour_salida.set(Calendar.MONTH, calendar_date.get(Calendar.MONTH));
                            calendar_hour_salida.set(Calendar.DAY_OF_MONTH, calendar_date.get(Calendar.DAY_OF_MONTH));

                            System.out.println("Buscando un ticket a la hora " + h + calendar_hour_salida.getTime() + " en la geocerca " + itinerario.getGeofenceId() + " ");
                            List<Ticket> tickets = storage.getObjects(Ticket.class,
                                    new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                                        {
                                            add(new Condition.Equals("geofenceId", itinerario.getGeofenceId()));
                                            add(new Condition.Equals("expectedTime", sdf.format(calendar_hour_salida.getTime())));
                                        }
                                    })));

                            if (tickets.isEmpty()) {
                                var obj = vri.new VueltaDataItem();
                                obj.setHora(calendar_hour_salida.getTime());
                                obj.setSalida(0);
                                obj.setDispositivo(0);
                                obj.setAsignado(false);
                                objs.add(obj);
                            } else {
                                boolean added = false;
                                for (Ticket ticket : tickets) {
                                    System.out.println(sdf.format(ticket.getExpectedTime()));
                                    if (!GenericUtils.isSameDate(calendar_date.getTime(), GenericUtils.addTimeToDate(ticket.getExpectedTime(), Calendar.HOUR_OF_DAY, -6))) {
//                                        System.out.println("Not same date");
                                        continue;
                                    }
                                    var obj = vri.new VueltaDataItem();
                                    obj.setHora(calendar_hour_salida.getTime());
                                    obj.setSalida(0);
                                    obj.setDispositivo(0);
                                    obj.setAsignado(false);
//                                    System.out.println("Ticket encontrado " + ticket);

                                    Salida salida = cacheManager.getStorage().getObject(Salida.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                                        {
                                            add(new Condition.Equals("id", ticket.getSalidaId()));
                                        }
                                    })));
                                    obj.setSalida(ticket.getSalidaId());
                                    obj.setDispositivo(salida.getDeviceId());
                                    obj.setAsignado(true);
                                    objs.add(obj);
                                    added = true;
                                }
                                if (!added) {
                                    var obj = vri.new VueltaDataItem();
                                    obj.setHora(calendar_hour_salida.getTime());
                                    obj.setSalida(0);
                                    obj.setDispositivo(0);
                                    obj.setAsignado(false);
                                    objs.add(obj);
                                }
                            }
                        }

                        vri.setData(objs);
                    }
                    result.add(vri);
                }
            }

        }
        return result;
    }

    public void getExcel(OutputStream outputStream,
            long userId, Collection<Long> deviceIds, Collection<Long> groupIds,
            Date from, Date to, boolean unify) throws StorageException, IOException {
        reportUtils.checkPeriodLimit(from, to);

        ArrayList<VueltaReportItem> result = new ArrayList<>();
        ArrayList<String> sheetNames = new ArrayList<>();

        for (long groupid : groupIds) {
//            System.out.println("Grupo " + groupid);
            List<Subroute> subrutas = new ArrayList<>();
            try {
                subrutas.addAll(storage.getObjects(Subroute.class, new Request(new Columns.All(), new Condition.Equals("groupId", groupid))));
            } catch (StorageException ex) {
                Logger.getLogger(VueltasReportProvider.class.getName()).log(Level.SEVERE, null, ex);
            }
//            System.out.println("Subrutas del grupo " + groupid + ": " + subrutas);
            List<Itinerario> itinerarios = new ArrayList<>();
            for (Subroute subruta : subrutas) {
                try {
                    itinerarios.addAll(storage.getObjects(Itinerario.class, new Request(new Columns.All(), new Condition.Equals("subrouteId", subruta.getId()))));
                } catch (StorageException ex) {
                    Logger.getLogger(VueltasReportProvider.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
//            System.out.println("Itinerarios del grupo " + groupid + ": " + itinerarios);
            for (Date date : GenericUtils.getDatesBetween(from, to)) {
//                System.out.println(date);
                for (Itinerario itinerario : itinerarios) {
                    VueltaReportItem vri = new VueltaReportItem();
                    vri.setItinerarioId((int) itinerario.getId());
                    List<HoraSalida> horas = new ArrayList<>();
                    if (itinerario.getHorasId() > 0) {
                        System.out.println("Itinerario con tabla de horas " + itinerario.getHorasId());
                        HoraSalida hora = storage.getObject(HoraSalida.class, new Request(new Columns.All(), new Condition.Equals("id", itinerario.getHorasId())));
                        horas.addAll(storage.getObjects(HoraSalida.class, new Request(new Columns.All(), new Condition.Equals("name", hora.getName()))));

//                        System.out.println("Tabla de horas \n\r " + horas);

                        List<VueltaReportItem.VueltaDataItem> objs = new ArrayList<>();

                        for (HoraSalida h : horas) {
                            Calendar calendar_date = Calendar.getInstance();
                            calendar_date.setTime(date);
                            Calendar calendar_hour_salida = Calendar.getInstance();
                            calendar_hour_salida.setTime(h.getHour());

                            calendar_hour_salida.set(Calendar.YEAR, calendar_date.get(Calendar.YEAR));
                            calendar_hour_salida.set(Calendar.MONTH, calendar_date.get(Calendar.MONTH));
                            calendar_hour_salida.set(Calendar.DAY_OF_MONTH, calendar_date.get(Calendar.DAY_OF_MONTH));

                            System.out.println("Buscando un ticket a la hora " + h + calendar_hour_salida.getTime() + " en la geocerca " + itinerario.getGeofenceId() + " ");
                            List<Ticket> tickets = storage.getObjects(Ticket.class,
                                    new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                                        {
                                            add(new Condition.Equals("geofenceId", itinerario.getGeofenceId()));
                                            add(new Condition.Equals("expectedTime", calendar_hour_salida.getTime()));
                                        }
                                    })));

                            if (tickets.isEmpty()) {
                                var obj = vri.new VueltaDataItem();
                                obj.setHora(calendar_hour_salida.getTime());
                                obj.setSalida(0);
                                obj.setDispositivo(0);
                                obj.setAsignado(false);
                                objs.add(obj);
                            } else {
                                boolean added = false;
                                for (Ticket ticket : tickets) {
                                    if (!GenericUtils.isSameDate(calendar_date.getTime(), GenericUtils.addTimeToDate(ticket.getExpectedTime(), Calendar.HOUR_OF_DAY, -6))) {
//                                        System.out.println("Not same date");
                                        continue;
                                    }
                                    var obj = vri.new VueltaDataItem();
                                    obj.setHora(calendar_hour_salida.getTime());
                                    obj.setSalida(0);
                                    obj.setDispositivo(0);
                                    obj.setAsignado(false);
//                                    System.out.println("Ticket encontrado " + ticket);

                                    Salida salida = cacheManager.getStorage().getObject(Salida.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                                        {
                                            add(new Condition.Equals("id", ticket.getSalidaId()));
                                        }
                                    })));
                                    obj.setSalida(ticket.getSalidaId());
                                    obj.setDispositivo(salida.getDeviceId());
                                    obj.setAsignado(true);
                                    objs.add(obj);
                                    added = true;
                                }
                                if (!added) {
                                    var obj = vri.new VueltaDataItem();
                                    obj.setHora(calendar_hour_salida.getTime());
                                    obj.setSalida(0);
                                    obj.setDispositivo(0);
                                    obj.setAsignado(false);
                                    objs.add(obj);
                                }
                            }
                        }

                        vri.setData(objs);
                    }
                    result.add(vri);
                }
            }

        }

        File file = Paths.get(config.getString(Keys.TEMPLATES_ROOT), "export", unify ? "salidas.xlsx" : "salidas.xlsx").toFile();

//        System.out.println("File");
//        System.out.println(file);
        sheetNames.add(WorkbookUtil.createSafeSheetName("Todos"));
        try (InputStream inputStream = new FileInputStream(file)) {
            var context = reportUtils.initializeContext(userId);
            context.putVar("tickets", result);
            context.putVar("sheetNames", sheetNames);
            context.putVar("from", from);
            context.putVar("to", to);
            reportUtils.processTemplateWithSheets(inputStream, outputStream, context, true);
        }

    }
}
