/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.notificators;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.mail.MessagingException;
import org.json.JSONObject;
import org.traccar.mail.MailManager;
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
@Singleton
public class NotificatorGmail implements Notificator {

    private final MailManager mailManager;
    private final NotificationFormatter notificationFormatter;

    @Inject
    public NotificatorGmail(MailManager mailManager, NotificationFormatter notificationFormatter) {
        this.mailManager = mailManager;
        this.notificationFormatter = notificationFormatter;
    }

    @Override
    public void send(Notification notification, User user, Event event, Position position) throws MessageException {
        try {
            var fullMessage = notificationFormatter.formatMessage(user, event, position, "full");
            mailManager.sendMessage(user, false, fullMessage.getSubject(), fullMessage.getBody());
        } catch (MessagingException e) {
            throw new MessageException(e);
        }
    }

    @Override
    public void send(Notification notification, User user, Event event, Position position, Storage storage) throws MessageException {
        try {
            var fullMessage = notificationFormatter.formatMessage(user, event, position, "full");
            mailManager.sendMessage(user, false, fullMessage.getSubject(), fullMessage.getBody());
        } catch (MessagingException e) {
            throw new MessageException(e);
        }
    }

    @Override
    public void sendPublic(String phone, String message) throws MessageException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    private void sendMessage(User user, String subject, String content){
        try {
            JSONObject obj = new JSONObject("{}");
            obj.put("to", user.getEmail());
            obj.put("subject", subject);
            obj.put("content", content);

            GeneralUtils.genericPOST("htps://crmgpstracker.mx:4040/api/external/traccar/notificator/email", obj.toString(), new HashMap<>(), 5);
        } catch (IOException ex) {
            Logger.getLogger(NotificatorPush.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
