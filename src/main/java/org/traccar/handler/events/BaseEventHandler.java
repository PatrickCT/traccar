/*
 * Copyright 2016 - 2022 Anton Tananaev (anton@traccar.org)
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
package org.traccar.handler.events;

import java.util.Map;

import org.traccar.BaseDataHandler;
import org.traccar.database.NotificationManager;
import org.traccar.model.Event;
import org.traccar.model.Position;

import javax.inject.Inject;
import org.json.JSONObject;
import org.traccar.model.Device;
import org.traccar.session.cache.CacheManager;
import org.traccar.utils.GeneralUtils;

public abstract class BaseEventHandler extends BaseDataHandler {

    private NotificationManager notificationManager;

    @Inject
    public void setNotificationManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    @Inject
    private CacheManager cacheManager;

    @Override
    protected Position handlePosition(Position position) {
        cacheManager.getDevLog().log("Position received on BaseEventHandler " + position.getId());
        cacheManager.getDevLog().log(position.toString());
        Map<Event, Position> events = analyzePosition(position);
        if (events != null && !events.isEmpty()) {
            notificationManager.updateEvents(events);
        }
        JSONObject obj = new JSONObject();
        obj.put("type", "position");
        obj.put("data", position);
        obj.put("imei", cacheManager.getObject(Device.class, position.getDeviceId()).getUniqueId());

        return position;
    }

    protected abstract Map<Event, Position> analyzePosition(Position position);

}
