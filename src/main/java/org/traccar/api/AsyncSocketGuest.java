/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.SQLException;
import java.util.ArrayList;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.traccar.helper.model.PositionUtil;
import org.traccar.session.ConnectionManager;
import org.traccar.model.Device;
import org.traccar.model.Event;
import org.traccar.model.Position;
import org.traccar.storage.Storage;
import org.traccar.storage.StorageException;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.json.JSONObject;
import org.traccar.model.Link;
import org.traccar.session.cache.CacheManager;
import org.traccar.storage.query.Columns;
import org.traccar.storage.query.Condition;
import org.traccar.storage.query.Request;

/**
 *
 * @author K
 */
public class AsyncSocketGuest extends WebSocketAdapter implements ConnectionManager.UpdateListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncSocket.class);

    private static final String KEY_DEVICES = "devices";
    private static final String KEY_POSITIONS = "positions";
    private static final String KEY_EVENTS = "events";

    private final ObjectMapper objectMapper;
    private final ConnectionManager connectionManager;
    private final CacheManager cacheManager;
    private final Storage storage;
    private final long userId;
    private long linkId;
    private Link link;
    private List<Long> devices = new ArrayList<>();
    private List<Device> devicesObj = new ArrayList<>();
    private List<Position> positions = new ArrayList<>();

    public AsyncSocketGuest(ObjectMapper objectMapper, ConnectionManager connectionManager, Storage storage, long userId, CacheManager cacheManager, long linkId) {
        this.objectMapper = objectMapper;
        this.connectionManager = connectionManager;
        this.storage = storage;
        this.userId = userId;
        this.cacheManager = cacheManager;
        this.linkId = linkId;
        try {
            this.devices.addAll(storage.getObjectsByQuery(Device.class, "select * from tc_devices d "
                    + "inner join tc_link_device ld "
                    + "on ld.deviceid=d.id "
                    + "where ld.linkid=" + String.valueOf(linkId)).stream().map(item -> item.getId()).collect(Collectors.toList()));
            for (Long device : this.devices) {
                this.devicesObj.addAll(storage.getObjects(Device.class, new Request(new Columns.All(), new Condition.Equals("id", device))));
                this.positions.addAll(storage.getObjects(Position.class, new Request(new Columns.All(), new Condition.LatestPositions(device))));
            }
        } catch (StorageException ex) {
            java.util.logging.Logger.getLogger(AsyncSocketGuest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onWebSocketConnect(Session session) {
        super.onWebSocketConnect(session);

        try {
            connectionManager.addListener(userId, this);
            Map<String, Collection<?>> data = new HashMap<>();
            data.put(KEY_DEVICES, devicesObj);
            data.put(KEY_POSITIONS, positions);
            sendData(data);
        } catch (StorageException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);
        this.cacheManager.getSocketsLogged().get(userId).remove(this);
        connectionManager.removeListener(userId, this);
    }

    @Override
    public void onKeepalive() {
        sendData(new HashMap<>());
    }

    @Override
    public void onUpdateDevice(Device device) {
        Map<String, Collection<?>> data = new HashMap<>();
        if (!devices.contains(device.getId())) {
            return;
        }
        data.put(KEY_DEVICES, Collections.singletonList(device));
        sendData(data);
    }

    @Override
    public void onUpdatePosition(Position position) {
        Map<String, Collection<?>> data = new HashMap<>();
        if (!devices.contains(position.getDeviceId())) {
            return;
        }
        data.put(KEY_POSITIONS, Collections.singletonList(position));
        sendData(data);
    }

    @Override
    public void onUpdateEvent(Event event) {
        Map<String, Collection<?>> data = new HashMap<>();
        data.put(KEY_EVENTS, Collections.singletonList(event));
        sendData(data);
    }

    private void sendData(Map<String, Collection<?>> data) {
        if (isConnected()) {
            try {
                getRemote().sendString(objectMapper.writeValueAsString(data), null);
            } catch (JsonProcessingException e) {
                LOGGER.warn("Socket JSON formatting error", e);
            }
        }
    }

    private void sendCustomData(Map<String, String> data) {
        if (isConnected()) {
            try {
                getRemote().sendString(objectMapper.writeValueAsString(data), null);
            } catch (JsonProcessingException e) {
                LOGGER.warn("Socket JSON formatting error", e);
            }
        }
    }

    public void onUpdateCustom(JSONObject obj) {
        Map<String, String> data = new HashMap<>();
        data.put("custom", obj.toString());
        sendCustomData(data);
    }
}
