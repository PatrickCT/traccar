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
public class Tramo extends BaseModel{
    private String name;    
    private int minTime;
    private int maxTime;
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

    public int getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
    }

    public long getGeofenceid() {
        return geofenceId;
    }

    public void setGeofenceid(long geofenceid) {
        this.geofenceId = geofenceid;
    }

    public int getPunishment() {
        return punishment;
    }

    public void setPunishment(int punishment) {
        this.punishment = punishment;
    }
    
    
}
