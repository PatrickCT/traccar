/*
 * Copyright 2023 Anton Tananaev (anton@traccar.org)
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
package org.traccar.handler;

import io.netty.channel.ChannelHandler;
import org.traccar.BaseDataHandler;
import org.traccar.config.Config;
import org.traccar.helper.model.GeofenceUtil;
import org.traccar.model.Position;
import org.traccar.session.cache.CacheManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
@ChannelHandler.Sharable
public class GeofenceHandler extends BaseDataHandler {

    private final Config config;
    private final CacheManager cacheManager;

    @Inject
    public GeofenceHandler(Config config, CacheManager cacheManager) {
        this.config = config;
        this.cacheManager = cacheManager;
    }

    @Override
    protected Position handlePosition(Position position) {
        List<Long> geofenceIds = GeofenceUtil.getCurrentGeofences(config, cacheManager, position);
        if (!geofenceIds.isEmpty()) {
            position.setGeofenceIds(geofenceIds);
        }
//        cacheManager.getDevLog().log("Position received on GeofenceHandler " + position.getId());
//        cacheManager.getDevLog().log(position.toString());
        return position;
    }

}
