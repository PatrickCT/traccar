/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.model;

import java.util.Date;
import org.traccar.storage.StorageName;

/**
 *
 * @author K
 */
@StorageName("tc_tickets")
public class Ticket extends BaseModel {
    private long salidaId;
    private long geofenceId;
    private Date expectedTime;
    private Date realTime;
    private double difference;
    private int punishment;
}
