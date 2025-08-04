/*
 * Copyright 2016 - 2022 Anton Tananaev (anton@traccar.org)
 * Copyright 2016 - 2018 Andrey Kunitsyn (andrey@traccar.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar.reports;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.util.WorkbookUtil;
import org.traccar.api.security.PermissionsService;
import org.traccar.config.Config;
import org.traccar.config.Keys;
import org.traccar.helper.model.DeviceUtil;
import org.traccar.helper.model.UserUtil;
import org.traccar.model.Device;
import org.traccar.model.Event;
import org.traccar.model.Geofence;
import org.traccar.model.Group;
import org.traccar.model.Maintenance;
import org.traccar.model.Position;
import org.traccar.reports.common.ReportUtils;
import org.traccar.reports.model.DeviceReportSection;
import org.traccar.storage.Storage;
import org.traccar.storage.StorageException;
import org.traccar.storage.query.Columns;
import org.traccar.storage.query.Condition;
import org.traccar.storage.query.Order;
import org.traccar.storage.query.Request;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.traccar.helper.DateUtil;
import org.traccar.model.GeofenceOrder;
import org.traccar.model.User;
import org.traccar.session.cache.CacheManager;
import org.traccar.utils.GenericUtils;
import org.traccar.utils.ReportesV2;

public class EventsReportProvider {

    private final Config config;
    private final ReportUtils reportUtils;
    private final Storage storage;
    private final CacheManager cacheManager;

    @Inject
    public EventsReportProvider(Config config, ReportUtils reportUtils, Storage storage, CacheManager cache) {
        this.config = config;
        this.reportUtils = reportUtils;
        this.storage = storage;
        this.cacheManager = cache;
    }

    private List<Event> getEvents(long deviceId, Date from, Date to) throws StorageException {
        return storage.getObjects(Event.class, new Request(
                new Columns.All(),
                new Condition.And(
                        new Condition.Equals("deviceId", deviceId),
                        new Condition.Between("eventTime", "from", from, "to", to)),
                new Order("eventTime")));
    }

    public Collection<Event> getObjects(
            long userId, Collection<Long> deviceIds, Collection<Long> groupIds,
            Collection<String> types, Date from, Date to) throws StorageException {
        reportUtils.checkPeriodLimit(from, to);

        ArrayList<Event> result = new ArrayList<>();
        for (Device device : DeviceUtil.getAccessibleDevices(storage, userId, deviceIds, groupIds)) {
            Collection<Event> events = getEvents(device.getId(), from, to);
            boolean all = types.isEmpty() || types.contains(Event.ALL_EVENTS);
            for (Event event : events) {
                if (all || types.contains(event.getType())) {
                    long geofenceId = event.getGeofenceId();
                    long maintenanceId = event.getMaintenanceId();
                    if ((geofenceId == 0 || reportUtils.getObject(userId, Geofence.class, geofenceId) != null)
                            && (maintenanceId == 0
                            || reportUtils.getObject(userId, Maintenance.class, maintenanceId) != null)) {
                        result.add(event);
                    }
                }
            }
        }
        return result;
    }

    public void getExcel(
            OutputStream outputStream, long userId, Collection<Long> deviceIds, Collection<Long> groupIds,
            Collection<String> types, Date from, Date to) throws StorageException, IOException {
        reportUtils.checkPeriodLimit(from, to);

        ArrayList<DeviceReportSection> devicesEvents = new ArrayList<>();
        ArrayList<String> sheetNames = new ArrayList<>();
        HashMap<Long, String> geofenceNames = new HashMap<>();
        HashMap<Long, String> maintenanceNames = new HashMap<>();
        HashMap<Long, Position> positions = new HashMap<>();
        for (Device device : DeviceUtil.getAccessibleDevices(storage, userId, deviceIds, groupIds)) {
            Collection<Event> events = getEvents(device.getId(), from, to);
            boolean all = types.isEmpty() || types.contains(Event.ALL_EVENTS);
            for (Iterator<Event> iterator = events.iterator(); iterator.hasNext(); ) {
                Event event = iterator.next();
                if (all || types.contains(event.getType())) {
                    long geofenceId = event.getGeofenceId();
                    long maintenanceId = event.getMaintenanceId();
                    if (geofenceId != 0) {
                        Geofence geofence = reportUtils.getObject(userId, Geofence.class, geofenceId);
                        if (geofence != null) {
                            geofenceNames.put(geofenceId, geofence.getName());
                        } else {
                            iterator.remove();
                        }
                    } else if (maintenanceId != 0) {
                        Maintenance maintenance = reportUtils.getObject(userId, Maintenance.class, maintenanceId);
                        if (maintenance != null) {
                            maintenanceNames.put(maintenanceId, maintenance.getName());
                        } else {
                            iterator.remove();
                        }
                    }
                } else {
                    iterator.remove();
                }
            }
            for (Event event : events) {
                long positionId = event.getPositionId();
                if (positionId > 0) {
                    Position position = storage.getObject(Position.class, new Request(
                            new Columns.All(), new Condition.Equals("id", positionId)));
                    positions.put(positionId, position);
                }
            }
            DeviceReportSection deviceEvents = new DeviceReportSection();
            deviceEvents.setDeviceName(device.getName());
            sheetNames.add(WorkbookUtil.createSafeSheetName(deviceEvents.getDeviceName()));
            if (device.getGroupId() > 0) {
                Group group = storage.getObject(Group.class, new Request(
                        new Columns.All(), new Condition.Equals("id", device.getGroupId())));
                if (group != null) {
                    deviceEvents.setGroupName(group.getName());
                }
            }
            deviceEvents.setObjects(events);
            devicesEvents.add(deviceEvents);
        }

        File file = Paths.get(config.getString(Keys.TEMPLATES_ROOT), "export", "events.xlsx").toFile();
        try (InputStream inputStream = new FileInputStream(file)) {
            var context = reportUtils.initializeContext(userId);
            context.putVar("devices", devicesEvents);
            context.putVar("sheetNames", sheetNames);
            context.putVar("geofenceNames", geofenceNames);
            context.putVar("maintenanceNames", maintenanceNames);
            context.putVar("positions", positions);
            context.putVar("from", from);

            context.putVar("to", to);
            reportUtils.processTemplateWithSheets(inputStream, outputStream, context);
        }
    }

    public static Date convertDateToTimeZone(Date date, TimeZone toTimeZone) {
        long timeInMillis = date.getTime();
        int fromOffset = TimeZone.getDefault().getOffset(timeInMillis);
        int toOffset = toTimeZone.getOffset(timeInMillis);
        return new Date(timeInMillis - fromOffset + toOffset);
    }


    public void getExcelHOptimized(OutputStream outputStream,
                                   long userId,
                                   Collection<Long> deviceIds,
                                   Collection<Long> groupIds,
                                   Collection<String> types,
                                   Date from,
                                   Date to) throws StorageException, IOException {

        //long totalStart = System.currentTimeMillis();
        reportUtils.checkPeriodLimit(from, to);
        //long start = System.currentTimeMillis();
        try {
            var server = reportUtils.getPermissionsService().getServer();
            var user = reportUtils.getPermissionsService().getUser(userId);
            var userTimeZone = UserUtil.getTimezone(server, user);


            SimpleDateFormat sdfFull = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");

            List<GeofenceOrder> geofenceOrders = getOrderedGeofences(userId);
            //System.out.println("[getExcelH] Geofence orders fetched in " + (System.currentTimeMillis() - start) + "ms");
            Set<Integer> geofenceIds = geofenceOrders.stream().map(GeofenceOrder::getGeofenceid).collect(Collectors.toSet());

            //start = System.currentTimeMillis();
            // Cache geofence names
            Map<Long, String> geofenceNames = storage.getObjectsByQuery(Geofence.class, "select * from tc_geofences").stream()
                    .filter(g -> geofenceIds.contains(g.getId()))
                    .collect(Collectors.toMap(Geofence::getId, Geofence::getName));
            //System.out.println("[getExcelH] Geofences fetched in " + (System.currentTimeMillis() - start) + "ms");
            List<String> headers = buildHeaders(geofenceOrders, geofenceNames);

            //start = System.currentTimeMillis();

            Set<Long> allDeviceIds = new HashSet<>(deviceIds);

            if (!groupIds.isEmpty()) {
                for (Long groupId : groupIds) {
                    List<Device> devices;
                    if (deviceIds.isEmpty()) {
                        devices = storage.getObjectsByQuery(Device.class, String.format("select * from tc_devices where groupId=%s", groupId));
                    } else {
                        String deviceIdsStr = deviceIds.stream().map(String::valueOf).collect(Collectors.joining(",", "(", ")"));
                        devices = storage.getObjectsByQuery(Device.class, String.format("select * from tc_devices where groupId=%s and id not in %s", groupId, deviceIdsStr));
                    }
                    allDeviceIds.addAll(devices.stream().map(Device::getId).collect(Collectors.toList()));
                }
            }

            //System.out.println("[getExcelH] Devices filtered fetched in " + (System.currentTimeMillis() - start) + "ms");
            //start = System.currentTimeMillis();
            // Cache all devices
            Map<Long, Device> deviceMap = storage.getObjectsByQuery(Device.class, "select * from tc_devices").stream()
                    .filter(d -> allDeviceIds.contains(d.getId()))
                    .collect(Collectors.toMap(Device::getId, Function.identity()));

            //System.out.println("[getExcelH] Devices fetched in " + (System.currentTimeMillis() - start) + "ms");

            String deviceIdStr = allDeviceIds.stream().map(String::valueOf).collect(Collectors.joining(",", "(", ")"));
            String typeStr = types.stream().map(t -> "'" + t + "'").collect(Collectors.joining(",", "(", ")"));

            //start = System.currentTimeMillis();
            Collection<Event> allEvents = storage.getObjectsByQuery(Event.class, String.format(
                    "select * from tc_events where deviceId in %s and type in %s and eventtime between '%s' and '%s'",
                    deviceIdStr,
                    typeStr,
                    sdfFull.format(from),
                    sdfFull.format(to)));


            //System.out.println("[getExcelH] Events fetched in " + (System.currentTimeMillis() - start) + "ms");
            // Group events by device
            Map<Long, List<Event>> eventsByDevice = allEvents.stream()
                    .collect(Collectors.groupingBy(Event::getDeviceId));

            List<String[]> data = new ArrayList<>();

            //start = System.currentTimeMillis();
            for (long deviceId : allDeviceIds) {
                Device device = deviceMap.get(deviceId);
                if (device == null) continue;

                List<Event> deviceEvents = eventsByDevice.getOrDefault(deviceId, Collections.emptyList());


                deviceEvents.sort(Comparator.comparing(Event::getEventTime));

                for (Event event : deviceEvents) {
                    Date localDate = convertDateToTimeZone(event.getEventTime(), userTimeZone);
                    event.setEventTime(localDate);
                }

                List<Event> entries = filterEvents(deviceEvents, Event.TYPE_GEOFENCE_ENTER);
                List<Event> exits = filterEvents(deviceEvents, Event.TYPE_GEOFENCE_EXIT);

                int rowCount = Math.max(entries.size(), exits.size()) + 1;
                List<String[]> deviceRows = IntStream.range(0, rowCount)
                        .mapToObj(i -> {
                            String[] row = new String[headers.size()];
                            Arrays.fill(row, "");
                            row[0] = device.getName();
                            return row;
                        }).collect(Collectors.toList());

                Map<Integer, Integer> geofenceIndexMap = new HashMap<>();
                int colIndex = 1;
                for (GeofenceOrder go : geofenceOrders) {
                    geofenceIndexMap.put(go.getGeofenceid(), colIndex);
                    colIndex += go.isExits() ? 2 : 1;
                }

                fillEventDataOptimized(deviceRows, entries, geofenceOrders, geofenceIndexMap, deviceMap, sdfTime, true);
                fillEventDataOptimized(deviceRows, exits, geofenceOrders, geofenceIndexMap, deviceMap, sdfTime, false);

                deviceRows.removeIf(row -> Arrays.stream(row).skip(1).allMatch(String::isEmpty));
                data.addAll(deviceRows);
            }

            //System.out.println("[getExcelH] Data proccessed in " + (System.currentTimeMillis() - start) + "ms");

            ReportesV2 reporter = new ReportesV2();
            reporter.createReporte("reporte", "devices", headers, data, outputStream,
                    String.format("%s - %s", DateUtil.formatDate(from), DateUtil.formatDate(to)));

            //System.out.println("[getExcelHOptimized] Completed in " + (System.currentTimeMillis() - totalStart) + "ms");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<GeofenceOrder> getOrderedGeofences(long userId) throws StorageException, JsonProcessingException {
        User user = storage.getObject(User.class, new Request(new Columns.All(), new Condition.Equals("id", userId)));
        List<GeofenceOrder> ordered = new ArrayList<>();

        // Step 1: Parse JSON array string into Java List<String>
        ObjectMapper mapper = new ObjectMapper();
        List<String> entries = mapper.readValue(user.getString("geofencesOrder", "[]"), List.class);

        // Step 2: Convert to List of Maps        
        int index = 0;
        for (String entry : entries) {
            String[] parts = entry.split("\\|");
            if (parts.length == 2) {
                Map<String, Boolean> map = new HashMap<>();
                ordered.add(new GeofenceOrder(Integer.parseInt(parts[0]), index, Boolean.parseBoolean(parts[1])));
            }
        }

        return ordered;
    }

    private List<String> buildHeaders(List<GeofenceOrder> geo, Map<Long, String> geofenceNames) {
        List<String> headers = new ArrayList<>();
        headers.add("Dispositivo");
        for (GeofenceOrder go : geo) {
            String name = getGeofenceName(go.getGeofenceid(), geofenceNames);
            headers.add(name + "(Entrada)");
            if (go.isExits()) {
                headers.add(name + "(Salida)");
            }
        }
        return headers;
    }

    private String getGeofenceName(long id, Map<Long, String> cache) {
        return cache.computeIfAbsent(id, gid -> {
            Geofence g = null;
            try {
                g = storage.getObject(Geofence.class, new Request(new Columns.All(), new Condition.Equals("id", gid)));
            } catch (StorageException ex) {
                Logger.getLogger(EventsReportProvider.class.getName()).log(Level.SEVERE, null, ex);
            }
            return g != null ? g.getName() : "";
        });
    }

    private List<Event> filterEvents(List<Event> events, String type) {
        return events.stream()
                .filter(e -> type.equals(e.getType()))
                .sorted(Comparator.comparing(Event::getEventTime))
                .collect(Collectors.toList());
    }

    private void fillEventDataOptimized(List<String[]> rows,
                                        List<Event> events,
                                        List<GeofenceOrder> geo,
                                        Map<Integer, Integer> geofenceIndexMap,
                                        Map<Long, Device> deviceMap,
                                        SimpleDateFormat sdf,
                                        boolean isEntry) {

        // Group rows by device name (can have multiple rows per device)
        Map<String, List<String[]>> rowMap = new HashMap<>();
        for (String[] row : rows) {
            rowMap.computeIfAbsent(row[0], k -> new ArrayList<>()).add(row);
        }

        for (Event e : events) {
            int geofenceId = (int) e.getGeofenceId();
            Optional<GeofenceOrder> orderOpt = geo.stream()
                    .filter(g -> g.getGeofenceid() == geofenceId)
                    .findFirst();
            if (!orderOpt.isPresent()) continue;

            GeofenceOrder go = orderOpt.get();
            if (!isEntry && !go.isExits()) continue;

            int colIndex = geofenceIndexMap.getOrDefault(geofenceId, -1);
            if (colIndex < 0) continue;
            if (!isEntry) colIndex += 1;

            Device device = deviceMap.get(e.getDeviceId());
            if (device == null) continue;

            List<String[]> deviceRows = rowMap.get(device.getName());
            if (deviceRows == null) continue;

            for (String[] row : deviceRows) {
                if (row[colIndex].isEmpty()) {
                    row[colIndex] = sdf.format(e.getEventTime());
                    break;
                }
            }
        }
    }
}
