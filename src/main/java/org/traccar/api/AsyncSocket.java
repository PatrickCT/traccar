/*
 * Copyright 2015 - 2022 Anton Tananaev (anton@traccar.org)
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
package org.traccar.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.json.JSONObject;
import org.traccar.model.Salida;
import org.traccar.session.cache.CacheManager;

public class AsyncSocket extends WebSocketAdapter implements ConnectionManager.UpdateListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncSocket.class);

    private static final String KEY_DEVICES = "devices";
    private static final String KEY_POSITIONS = "positions";
    private static final String KEY_EVENTS = "events";

    private final ObjectMapper objectMapper;
    private final ConnectionManager connectionManager;
    private final CacheManager cacheManager;
    private final Storage storage;
    private final long userId;

    public AsyncSocket(ObjectMapper objectMapper, ConnectionManager connectionManager, Storage storage, long userId, CacheManager cacheManager) {
        this.objectMapper = objectMapper;
        this.connectionManager = connectionManager;
        this.storage = storage;
        this.userId = userId;
        this.cacheManager = cacheManager;
    }

    @Override
    public void onWebSocketConnect(Session session) {
        try {
            super.onWebSocketConnect(session);
            Map<String, Collection<?>> data = new HashMap<>();
            data.put(KEY_POSITIONS, PositionUtil.getLatestPositions(storage, userId));
            sendData(data);
            connectionManager.addListener(userId, this);
        } catch (StorageException e) {
            e.printStackTrace();
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
        if (storage.checkTable("tc_salidas")) {
            List<Salida> salidas = new ArrayList<>();
            try {
                salidas = storage.getObjectsByQuery(Salida.class, "select * from tc_salidas where finished = false and deviceid = " + device.getId());
            } catch (StorageException ex) {
                java.util.logging.Logger.getLogger(AsyncSocket.class.getName()).log(Level.SEVERE, null, ex);
            }
            device.getAttributes().put("Salida", !salidas.isEmpty());
        }
        data.put(KEY_DEVICES, Collections.singletonList(device));
        sendData(data);
    }

    @Override
    public void onUpdatePosition(Position position) {
        Map<String, Collection<?>> data = new HashMap<>();
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
