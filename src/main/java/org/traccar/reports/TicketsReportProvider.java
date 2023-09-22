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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import org.apache.poi.ss.util.WorkbookUtil;
import org.traccar.config.Config;
import org.traccar.config.Keys;
import org.traccar.helper.model.DeviceUtil;
import org.traccar.reports.common.ReportUtils;
import org.traccar.reports.model.TicketReportItem;
import org.traccar.storage.Storage;
import org.traccar.storage.StorageException;
import org.traccar.model.Device;
import org.traccar.model.Geofence;
import org.traccar.model.Salida;
import org.traccar.model.Ticket;
import org.traccar.reports.model.DeviceReportSection;
import org.traccar.session.cache.CacheManager;
import org.traccar.storage.query.Columns;
import org.traccar.storage.query.Condition;
import org.traccar.storage.query.Request;

/**
 *
 * @author K
 */
public class TicketsReportProvider {
    
    private final Config config;
    private final ReportUtils reportUtils;
    private final Storage storage;
    
    @Inject
    public TicketsReportProvider(Config config, ReportUtils reportUtils, Storage storage) {
        this.config = config;
        this.reportUtils = reportUtils;
        this.storage = storage;
    }
    
    @Inject
    private CacheManager cacheManager;
    
    public Collection<TicketReportItem> getObjects(long userId, Collection<Long> deviceIds, Collection<Long> groupIds,
            Date from, Date to) throws StorageException {
        ArrayList<TicketReportItem> result = new ArrayList<>();
        reportUtils.checkPeriodLimit(from, to);
        List<Ticket> tickets = new ArrayList<>();
        for (Device device : DeviceUtil.getAccessibleDevices(storage, userId, deviceIds, groupIds)) {
            List<Salida> salidas = storage.getObjects(Salida.class,
                    new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                        {
                            add(new Condition.Equals("deviceId", device.getId()));
                            add(new Condition.Between("date", "from", from, "to", to));
                        }
                    })));
            
            salidas.forEach((salida) -> {
                try {
                    tickets.addAll(storage.getObjects(Ticket.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                        {
                            add(new Condition.Equals("salidaId", salida.getId()));
                        }
                    }))));
                } catch (StorageException ex) {
                    Logger.getLogger(TicketsReportProvider.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            
            Map<Long, String> geofenceNames = new HashMap<Long, String>();
            
            for (Ticket ticket : tickets) {
                if (!geofenceNames.containsKey(ticket.getGeofenceId())) {
                    Geofence g = storage.getObject(Geofence.class, new Request(new Columns.All(), new Condition.Equals("id", ticket.getGeofenceId())));
                    geofenceNames.putIfAbsent(ticket.getGeofenceId(), g != null ? g.getName() : "Desconocida");
                }
                TicketReportItem tri = new TicketReportItem();
                tri.setDifference(ticket.getDifference());
                tri.setEnterTime(ticket.getEnterTime());
                tri.setExitTime(ticket.getExitTime());
                tri.setExpectedTime(ticket.getExpectedTime());
                tri.setId(ticket.getId());
                tri.setPunishment(ticket.getPunishment());
                tri.setSalida(ticket.getSalidaId());
                tri.setGeofence(geofenceNames.get(ticket.getGeofenceId()));
                tri.setDevice(device.getId());
                
                result.add(tri);
            }
        }
        
        return result;
    }

//
    public void getExcel(OutputStream outputStream,
            long userId, Collection<Long> deviceIds, Collection<Long> groupIds,
            Date from, Date to) throws StorageException, IOException {
        reportUtils.checkPeriodLimit(from, to);
        
        ArrayList<TicketReportItem> result = new ArrayList<>();
        ArrayList<DeviceReportSection> devicesTickets = new ArrayList<>();
        ArrayList<String> sheetNames = new ArrayList<>();
        
        List<Ticket> tickets = new ArrayList<>();
        for (Device device : DeviceUtil.getAccessibleDevices(storage, userId, deviceIds, groupIds)) {
            result.clear();
            
            DeviceReportSection deviceTickets = new DeviceReportSection();
            deviceTickets.setDeviceName(device.getName());
            sheetNames.add(WorkbookUtil.createSafeSheetName(deviceTickets.getDeviceName()));
            
            List<Salida> salidas = storage.getObjects(Salida.class,
                    new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                        {
                            add(new Condition.Equals("deviceId", device.getId()));
                            add(new Condition.Between("date", "from", from, "to", to));
                        }
                    })));
            
            salidas.forEach((salida) -> {
                try {
                    tickets.addAll(storage.getObjects(Ticket.class, new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                        {
                            add(new Condition.Equals("salidaId", salida.getId()));
                        }
                    }))));
                } catch (StorageException ex) {
                    Logger.getLogger(TicketsReportProvider.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            
            Map<Long, String> geofenceNames = new HashMap<Long, String>();
            
            for (Ticket ticket : tickets) {
                if (!geofenceNames.containsKey(ticket.getGeofenceId())) {
                    Geofence g = storage.getObject(Geofence.class, new Request(new Columns.All(), new Condition.Equals("id", ticket.getGeofenceId())));
                    geofenceNames.putIfAbsent(ticket.getGeofenceId(), g != null ? g.getName() : "Desconocida");
                }
                TicketReportItem tri = new TicketReportItem();
                tri.setDifference(ticket.getDifference());
                tri.setEnterTime(ticket.getEnterTime());
                tri.setExitTime(ticket.getExitTime());
                tri.setExpectedTime(ticket.getExpectedTime());
                tri.setId(ticket.getId());
                tri.setPunishment(ticket.getPunishment());
                tri.setSalida(ticket.getSalidaId());
                tri.setGeofence(geofenceNames.get(ticket.getGeofenceId()));
                tri.setDevice(device.getId());
                tri.setDeviceName(device.getName());
                result.add(tri);
            }
            deviceTickets.setObjects(result);
            devicesTickets.add(deviceTickets);
        }
        
        File file = Paths.get(config.getString(Keys.TEMPLATES_ROOT), "export", "tickets.xlsx").toFile();
        
        try (InputStream inputStream = new FileInputStream(file)) {
            var context = reportUtils.initializeContext(userId);
            context.putVar("tickets", devicesTickets);
            context.putVar("sheetNames", sheetNames);
            context.putVar("from", from);
            context.putVar("to", to);
            reportUtils.processTemplateWithSheets(inputStream, outputStream, context);
        }
    }
}
