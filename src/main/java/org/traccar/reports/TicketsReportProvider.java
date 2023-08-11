/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.reports;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import javax.inject.Inject;
import org.traccar.config.Config;
import org.traccar.helper.model.DeviceUtil;
import org.traccar.reports.common.ReportUtils;
import org.traccar.reports.model.TicketReportItem;
import org.traccar.storage.Storage;
import org.traccar.storage.StorageException;
import org.traccar.model.Device;

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
    
//    public Collection<TicketReportItem> getObjects(
//            long userId, Collection<Long> deviceIds, Collection<Long> groupIds,
//            Date from, Date to) throws StorageException {
//        reportUtils.checkPeriodLimit(from, to);
//        
//        ArrayList<TicketReportItem> result = new ArrayList<>();
//        for (Device device: DeviceUtil.getAccessibleDevices(storage, userId, deviceIds, groupIds)) {
//            result.addAll(reportUtils.detectTripsAndStops(device, from, to, TripReportItem.class));
//        }
//        return result;
//    }
//
//    public void getExcel(OutputStream outputStream,
//            long userId, Collection<Long> deviceIds, Collection<Long> groupIds,
//            Date from, Date to) throws StorageException, IOException {
//        reportUtils.checkPeriodLimit(from, to);
//
//        ArrayList<DeviceReportSection> devicesTrips = new ArrayList<>();
//        ArrayList<String> sheetNames = new ArrayList<>();
//        for (Device device: DeviceUtil.getAccessibleDevices(storage, userId, deviceIds, groupIds)) {
//            Collection<TripReportItem> trips = reportUtils.detectTripsAndStops(device, from, to, TripReportItem.class);
//            DeviceReportSection deviceTrips = new DeviceReportSection();
//            deviceTrips.setDeviceName(device.getName());
//            sheetNames.add(WorkbookUtil.createSafeSheetName(deviceTrips.getDeviceName()));
//            if (device.getGroupId() > 0) {
//                Group group = storage.getObject(Group.class, new Request(
//                        new Columns.All(), new Condition.Equals("id", device.getGroupId())));
//                if (group != null) {
//                    deviceTrips.setGroupName(group.getName());
//                }
//            }
//            deviceTrips.setObjects(trips);
//            devicesTrips.add(deviceTrips);
//        }
//
//        File file = Paths.get(config.getString(Keys.TEMPLATES_ROOT), "export", "trips.xlsx").toFile();
//        try (InputStream inputStream = new FileInputStream(file)) {
//            var context = reportUtils.initializeContext(userId);
//            context.putVar("devices", devicesTrips);
//            context.putVar("sheetNames", sheetNames);
//            context.putVar("from", from);
//            context.putVar("to", to);
//            reportUtils.processTemplateWithSheets(inputStream, outputStream, context);
//        }
//    }
}
