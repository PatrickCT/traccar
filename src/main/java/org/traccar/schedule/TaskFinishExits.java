/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.schedule;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.traccar.database.NotificationManager;
import org.traccar.model.Event;
import org.traccar.model.Position;
import org.traccar.model.Salida;
import org.traccar.storage.Storage;
import org.traccar.storage.StorageException;
import org.traccar.storage.query.Columns;
import org.traccar.storage.query.Condition;
import org.traccar.storage.query.Request;

/**
 *
 * @author K
 */
public class TaskFinishExits implements ScheduleTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskDeviceInactivityCheck.class);

    private static final long CHECK_PERIOD_MINUTES = 1;

    private final Storage storage;
    private final NotificationManager notificationManager;

    @Inject
    public TaskFinishExits(Storage storage, NotificationManager notificationManager) {
        this.storage = storage;
        this.notificationManager = notificationManager;
    }

    @Override
    public void schedule(ScheduledExecutorService executor) {
        executor.scheduleAtFixedRate(this, CHECK_PERIOD_MINUTES, CHECK_PERIOD_MINUTES, TimeUnit.MINUTES);
    }

    @Override
    public void run() {       
        Map<Event, Position> events = new HashMap<>();

        try {

            List<Salida> salidas = storage.getObjects(Salida.class,
                    new Request(new Columns.All(), Condition.merge(new ArrayList<>() {
                        {
                            add(new Condition.Equals("finished", false));
                        }
                    })));
            Date today = new Date();
            for (Salida salida : salidas) {
                if (today.after(salida.getEndingDate())) { 
                    salida.setFinished(true);
                    storage.updateObject(salida, new Request(
                            new Columns.Exclude("id"),
                            new Condition.Equals("id", salida.getId())));
                }
            }

            
        } catch (StorageException e) {
            LOGGER.warn("Get salidas error", e);
        }

        notificationManager.updateEvents(events);
    }
}
