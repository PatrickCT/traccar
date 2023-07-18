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

    public long getSalidaId() {
        return salidaId;
    }

    public void setSalidaId(long salidaId) {
        this.salidaId = salidaId;
    }

    public long getGeofenceId() {
        return geofenceId;
    }

    public void setGeofenceId(long geofenceId) {
        this.geofenceId = geofenceId;
    }

    public Date getExpectedTime() {
        return expectedTime;
    }

    public void setExpectedTime(Date expectedTime) {
        this.expectedTime = expectedTime;
    }

    public Date getRealTime() {
        return realTime;
    }

    public void setRealTime(Date realTime) {
        this.realTime = realTime;
    }

    public double getDifference() {
        return difference;
    }

    public void setDifference(double difference) {
        this.difference = difference;
    }

    public int getPunishment() {
        return punishment;
    }

    public void setPunishment(int punishment) {
        this.punishment = punishment;
    }

    @Override
    public String toString() {
        return "Ticket{" + "salidaId=" + salidaId + ", geofenceId=" + geofenceId + ", expectedTime=" + expectedTime + ", realTime=" + realTime + ", difference=" + difference + ", punishment=" + punishment + '}';
    }
}
