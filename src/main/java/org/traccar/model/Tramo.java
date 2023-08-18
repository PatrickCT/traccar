/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.model;

import org.traccar.storage.StorageName;

/**
 *
 * @author K
 */
@StorageName("tc_tramos")
public class Tramo extends BaseModel {

    private String name;
    private int minTime;
    private int delay;
    private long geofenceId;
    private int punishment;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMinTime() {
        return minTime;
    }

    public void setMinTime(int minTime) {
        this.minTime = minTime;
    }

    public long getGeofenceId() {
        return geofenceId;
    }

    public void setGeofenceId(long geofenceId) {
        this.geofenceId = geofenceId;
    }

    public int getPunishment() {
        return punishment;
    }

    public void setPunishment(int punishment) {
        this.punishment = punishment;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }   

    @Override
    public String toString() {
        return "Tramo{" + "name=" + name + ", minTime=" + minTime + ", delay=" + delay + ", geofenceId=" + geofenceId + ", punishment=" + punishment + '}';
    }       

}
