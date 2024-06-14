/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import org.traccar.storage.QueryIgnore;
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
    private Date enterTime;
    private Date exitTime;
    private double difference;
    private int punishment;
    private boolean passed;
    private long tramo;
    private String excuse;
    private boolean globalExcuse;    

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

    public Date getEnterTime() {
        return enterTime;
    }

    public void setEnterTime(Date enterTime) {
        this.enterTime = enterTime;
    }

    public Date getExitTime() {
        return exitTime;
    }

    public void setExitTime(Date exitTime) {
        this.exitTime = exitTime;
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

    public boolean getPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public long getTramo() {
        return tramo;
    }

    public void setTramo(long tramo) {
        this.tramo = tramo;
    }

    public String getExcuse() {
        return excuse;
    }

    public void setExcuse(String excuse) {
        this.excuse = excuse;
    }

    public boolean getGlobalExcuse() {
        return globalExcuse;
    }

    public void setGlobalExcuse(boolean globalExcuse) {
        this.globalExcuse = globalExcuse;
    }

    @Override
    public String toString() {
        return "Ticket{" + "salidaId=" + salidaId + ", geofenceId=" + geofenceId + ", expectedTime=" + expectedTime + ", enterTime=" + enterTime + ", exitTime=" + exitTime + ", difference=" + difference + ", punishment=" + punishment + ", passed=" + passed + ", tramo=" + tramo + ", excuse=" + excuse + ", globalExcuse=" + globalExcuse + '}';
    }                                
}
