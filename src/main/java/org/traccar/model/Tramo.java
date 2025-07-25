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
    private boolean forceBacktrackSearch;
    private boolean forceUseExitTime;

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

    public boolean getForceBacktrackSearch() {
        return forceBacktrackSearch;
    }

    public void setForceBacktrackSearch(boolean forceBacktrackSearch) {
        this.forceBacktrackSearch = forceBacktrackSearch;
    }

    public boolean getForceUseExitTime() {
        return forceUseExitTime;
    }

    public void setForceUseExitTime(boolean forceUseExitTime) {
        this.forceUseExitTime = forceUseExitTime;
    }

    @Override
    public String toString() {
        return "Tramo{" + "name=" + name + ", minTime=" + minTime + ", delay=" + delay + ", geofenceId=" + geofenceId + ", punishment=" + punishment + ", forceBacktrackSearch=" + forceBacktrackSearch + '}';
    }    
}
