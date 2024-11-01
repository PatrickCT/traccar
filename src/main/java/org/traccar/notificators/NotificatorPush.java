/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.notificators;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import org.json.JSONObject;
import org.traccar.config.Config;
import org.traccar.config.Keys;
import org.traccar.model.Event;
import org.traccar.model.Notification;
import org.traccar.model.Position;
import org.traccar.model.User;
import org.traccar.notification.MessageException;
import org.traccar.notification.NotificationFormatter;
import org.traccar.storage.Storage;
import org.traccar.utils.GeneralUtils;

/**
 *
 * @author K
 */
public class NotificatorPush implements Notificator {

    private final NotificationFormatter notificationFormatter;
    private final String url;

    @Inject
    public NotificatorPush(Config config, NotificationFormatter notificationFormatter) {
        this.notificationFormatter = notificationFormatter;
        this.url = config.getString(Keys.NOTIFICATOR_PUSH_URL);
    }

    @Override
    public void send(Notification notification, User user, Event event, Position position) throws MessageException {
        var shortMessage = notificationFormatter.formatMessage(user, event, position, "short");
        sendPush(user.getPushId(), shortMessage.getBody());
    }

    @Override
    public void send(Notification notification, User user, Event event, Position position, Storage storage) throws MessageException {
        var shortMessage = notificationFormatter.formatMessage(user, event, position, "short");
        sendPush(user.getPushId(), shortMessage.getBody());
    }

    @Override
    public void sendPublic(String phone, String message) throws MessageException {

    }

    private void sendPush(String pushId, String message) {
        try {
            JSONObject obj = new JSONObject("{}");
            obj.put("userId", pushId);
            obj.put("message", message);

            GeneralUtils.genericPOST(this.url, obj.toString(), new HashMap<>(), 5);
        } catch (IOException ex) {
            Logger.getLogger(NotificatorPush.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
