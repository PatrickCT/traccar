/*
 * Copyright 2017 - 2023 Anton Tananaev (anton@traccar.org)
 * Copyright 2017 - 2018 Andrey Kunitsyn (andrey@traccar.org)
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
package org.traccar.notificators;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.traccar.database.StatisticsManager;
import org.traccar.model.Event;
import org.traccar.model.Notification;
import org.traccar.model.Position;
import org.traccar.model.User;
import org.traccar.notification.MessageException;
import org.traccar.notification.NotificationFormatter;
import org.traccar.sms.SmsManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.traccar.Main;
import org.traccar.model.ExtraPhone;
import org.traccar.storage.Storage;
import org.traccar.storage.StorageException;
import org.traccar.storage.query.Columns;
import org.traccar.storage.query.Condition;
import org.traccar.storage.query.Request;

@Singleton
public class NotificatorSms implements Notificator {

    private final SmsManager smsManager;
    private final NotificationFormatter notificationFormatter;
    private final StatisticsManager statisticsManager;

    @Inject
    public NotificatorSms(
            SmsManager smsManager, NotificationFormatter notificationFormatter, StatisticsManager statisticsManager) {
        this.smsManager = smsManager;
        this.notificationFormatter = notificationFormatter;
        this.statisticsManager = statisticsManager;
    }

    @Override
    public void send(Notification notification, User user, Event event, Position position) throws MessageException {
        if (user.getPhone() != null) {
            var shortMessage = notificationFormatter.formatMessage(user, event, position, "short");
            statisticsManager.registerSms();
            smsManager.sendMessage(user.getPhone(), shortMessage.getBody(), false);
        }
    }

    @Override
    public void send(Notification notification, User user, Event event, Position position, Storage storaqe) throws MessageException {
        if (user.getPhone() != null) {
            try {
                var shortMessage = notificationFormatter.formatMessage(user, event, position, "short");
                statisticsManager.registerSms();
                List<String> phones = new ArrayList<>();
                phones.add(user.getPhone());
                phones.addAll(storaqe.getObjects(ExtraPhone.class, new Request(
                        new Columns.Include("phone"),
                        new Condition.Equals("userid", user.getId())
                )).stream().map((ep) -> {
                    return ep.getPhone();
                }).collect(Collectors.toList()));
                Set<String> set = new LinkedHashSet<>(phones);
                String result = String.join(",", set.stream().map(s -> "\"" + s + "\"").toArray(String[]::new));
                smsManager.sendMessage(result, shortMessage.getBody(), false);
            } catch (StorageException ex) {
                Logger.getLogger(NotificatorSms.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void sendPublic(String phone, String message) throws MessageException {
        if (phone != null) {
            smsManager.sendMessage("\"" + phone + "\"",
                    message, false);
        }
    }



}
