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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.apache.poi.ss.util.WorkbookUtil;
import org.json.JSONObject;
import org.traccar.api.resource.HorasSalidasResource;
import org.traccar.api.resource.ItinerarioResource;
import org.traccar.config.Config;
import org.traccar.config.Keys;
import org.traccar.model.Device;
import org.traccar.model.Geofence;
import org.traccar.model.Group;
import org.traccar.model.HoraSalida;
import org.traccar.model.Itinerario;
import org.traccar.model.Permission;
import org.traccar.model.Salida;
import org.traccar.model.Subroute;
import org.traccar.model.Ticket;
import org.traccar.reports.common.ReportUtils;
import org.traccar.reports.model.DeviceReportSection;
import org.traccar.reports.model.TicketReportItem;
import org.traccar.reports.model.VueltaReportItem;
import org.traccar.reports.model.VueltaReportSection;
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
            Date from, Date to) throws StorageException {
        ArrayList<VueltaReportItem> result = new ArrayList<>();

        for (long groupid : groupIds) {
            System.out.println("Grupo " + groupid);
            List<Subroute> subrutas = new ArrayList<>();
            try {
                subrutas.addAll(storage.getObjects(Subroute.class, new Request(new Columns.All(), new Condition.Equals("groupId", groupid))));
            } catch (StorageException ex) {
                Logger.getLogger(VueltasReportProvider.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Subrutas del grupo " + groupid + ": " + subrutas);
            List<Itinerario> itinerarios = new ArrayList<>();
            for (Subroute subruta : subrutas) {
                try {
                    itinerarios.addAll(storage.getObjects(Itinerario.class, new Request(new Columns.All(), new Condition.Equals("subrouteId", subruta.getId()))));
                } catch (StorageException ex) {
                    Logger.getLogger(VueltasReportProvider.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            System.out.println("Itinerarios del grupo " + groupid + ": " + itinerarios);
            for (Date date : GenericUtils.getDatesBetween(from, to)) {
                System.out.println(date);
                for (Itinerario itinerario : itinerarios) {
                    VueltaReportItem vri = new VueltaReportItem();
                    vri.setItinerarioId((int) itinerario.getId());
                    List<HoraSalida> horas = new ArrayList<>();
                    if (itinerario.getHorasId() > 0) {
                        System.out.println("Itinerario con tabla de horas " + itinerario.getHorasId());
                        HoraSalida hora = storage.getObject(HoraSalida.class, new Request(new Columns.All(), new Condition.Equals("id", itinerario.getHorasId())));
                        horas.addAll(storage.getObjects(HoraSalida.class, new Request(new Columns.All(), new Condition.Equals("name", hora.getName()))));

                        System.out.println("Tabla de horas \n\r " + horas);

                        List<VueltaReportItem.VueltaDataItem> objs = new ArrayList<>();

                        for (HoraSalida h : horas) {
                            Calendar calendar_date = Calendar.getInstance();
                            calendar_date.setTime(date);
                            Calendar calendar_hour_salida = Calendar.getInstance();
                            calendar_hour_salida.setTime(h.getHour());

                            calendar_hour_salida.set(Calendar.YEAR, calendar_date.get(Calendar.YEAR));
                            calendar_hour_salida.set(Calendar.MONTH, calendar_date.get(Calendar.MONTH));
                            calendar_hour_salida.set(Calendar.DAY_OF_MONTH, calendar_date.get(Calendar.DAY_OF_MONTH));

                            var obj = vri.new VueltaDataItem();
                            obj.setHora(GenericUtils.addTimeToDate(h.getHour(), Calendar.HOUR_OF_DAY, 1));

                            System.out.println("Buscando un ticket a la hora " + h + calendar_hour_salida.getTime() + " en la geocerca " + itinerario.getGeofenceId() + " ");
                            Ticket ticket = storage.getObject(Ticket.class,
                                    new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                                        {
                                            add(new Condition.Equals("geofenceId", itinerario.getGeofenceId()));
                                            add(new Condition.Equals("expectedTime", calendar_hour_salida.getTime()));
                                        }
                                    })));

                            System.out.println("Ticket encontrado " + ticket);

                            obj.setSalida(0);
                            obj.setDispositivo(0);
                            obj.setAsignado(false);

                            if (ticket != null) {
                                Salida salida = cacheManager.getStorage().getObject(Salida.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                                    {
                                        add(new Condition.Equals("id", ticket.getSalidaId()));
                                    }
                                })));
                                obj.setSalida(ticket.getSalidaId());
                                obj.setDispositivo(salida.getDeviceId());
                                obj.setAsignado(true);
                            }
                            objs.add(obj);
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
        ArrayList<VueltaReportSection> devicesTickets = new ArrayList<>();
        ArrayList<String> sheetNames = new ArrayList<>();
        Map<Long, String> geofenceNames = new HashMap<Long, String>();
        Map<Long, String> groupNames = new HashMap<Long, String>();
        Map<Long, String> subroutesNames = new HashMap<Long, String>();
        Map<Long, Salida> salidasReportadas = new HashMap<Long, Salida>();
        Map<Long, VueltaReportSection> devicesReportados = new HashMap<Long, VueltaReportSection>();

        for (long groupid : groupIds) {
            System.out.println("Grupo " + groupid);
            List<Subroute> subrutas = new ArrayList<>();
            try {
                subrutas.addAll(storage.getObjects(Subroute.class, new Request(new Columns.All(), new Condition.Equals("groupId", groupid))));
            } catch (StorageException ex) {
                Logger.getLogger(VueltasReportProvider.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Subrutas del grupo " + groupid + ": " + subrutas);
            List<Itinerario> itinerarios = new ArrayList<>();
            for (Subroute subruta : subrutas) {
                try {
                    itinerarios.addAll(storage.getObjects(Itinerario.class, new Request(new Columns.All(), new Condition.Equals("subrouteId", subruta.getId()))));
                } catch (StorageException ex) {
                    Logger.getLogger(VueltasReportProvider.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            System.out.println("Itinerarios del grupo " + groupid + ": " + itinerarios);
            for (Date date : GenericUtils.getDatesBetween(from, to)) {
                System.out.println(date);
                for (Itinerario itinerario : itinerarios) {
                    VueltaReportItem vri = new VueltaReportItem();
                    vri.setItinerarioId((int) itinerario.getId());
                    List<HoraSalida> horas = new ArrayList<>();
                    if (itinerario.getHorasId() > 0) {
                        System.out.println("Itinerario con tabla de horas " + itinerario.getHorasId());
                        HoraSalida hora = storage.getObject(HoraSalida.class, new Request(new Columns.All(), new Condition.Equals("id", itinerario.getHorasId())));
                        horas.addAll(storage.getObjects(HoraSalida.class, new Request(new Columns.All(), new Condition.Equals("name", hora.getName()))));

                        System.out.println("Tabla de horas \n\r " + horas);

                        List<VueltaReportItem.VueltaDataItem> objs = new ArrayList<>();

                        for (HoraSalida h : horas) {
                            Calendar calendar_date = Calendar.getInstance();
                            calendar_date.setTime(date);
                            Calendar calendar_hour_salida = Calendar.getInstance();
                            calendar_hour_salida.setTime(h.getHour());

                            calendar_hour_salida.set(Calendar.YEAR, calendar_date.get(Calendar.YEAR));
                            calendar_hour_salida.set(Calendar.MONTH, calendar_date.get(Calendar.MONTH));
                            calendar_hour_salida.set(Calendar.DAY_OF_MONTH, calendar_date.get(Calendar.DAY_OF_MONTH));

                            var obj = vri.new VueltaDataItem();
                            obj.setHora(GenericUtils.addTimeToDate(h.getHour(), Calendar.HOUR_OF_DAY, 1));

                            System.out.println("Buscando un ticket a la hora " + h + calendar_hour_salida.getTime() + " en la geocerca " + itinerario.getGeofenceId() + " ");
                            Ticket ticket = storage.getObject(Ticket.class,
                                    new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                                        {
                                            add(new Condition.Equals("geofenceId", itinerario.getGeofenceId()));
                                            add(new Condition.Equals("expectedTime", calendar_hour_salida.getTime()));
                                        }
                                    })));

                            System.out.println("Ticket encontrado " + ticket);

                            obj.setSalida(0);
                            obj.setDispositivo(0);
                            obj.setAsignado(false);

                            if (ticket != null) {
                                Salida salida = cacheManager.getStorage().getObject(Salida.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                                    {
                                        add(new Condition.Equals("id", ticket.getSalidaId()));
                                    }
                                })));
                                obj.setSalida(ticket.getSalidaId());
                                obj.setDispositivo(salida.getDeviceId());
                                obj.setAsignado(true);
                            }
                            objs.add(obj);
                        }

                        vri.setData(objs);
                    }
                    result.add(vri);
                }
            }
        }

        File file = Paths.get(config.getString(Keys.TEMPLATES_ROOT), "export", unify ? "salidas.xlsx" : "salidas.xlsx").toFile();

        System.out.println("File");
        System.out.println(file);
        sheetNames.add(WorkbookUtil.createSafeSheetName("Todos"));
        sheetNames.add(WorkbookUtil.createSafeSheetName("Todos2"));
        sheetNames.add(WorkbookUtil.createSafeSheetName("Todos3"));
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
