/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.reports.model;

import java.util.Date;
import org.traccar.model.Salida;

/**
 *
 * @author K
 */
public class TicketReportItem {

    private long id;
    private long salida;
    private String geofence;
    private Date expectedTime;
    private Date enterTime;
    private Date exitTime;
    private double difference;
    private int punishment;
    private long device;
    private String deviceName;
    private String group;
    private String subroute;
    private Salida s;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSalida() {
        return salida;
    }

    public void setSalida(long salida) {
        this.salida = salida;
    }

    public String getGeofence() {
        return geofence;
    }

    public void setGeofence(String geofence) {
        this.geofence = geofence;
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

    public long getDevice() {
        return device;
    }

    public void setDevice(long device) {
        this.device = device;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getSubroute() {
        return subroute;
    }

    public void setSubroute(String subroute) {
        this.subroute = subroute;
    }

    public Salida getS() {
        return s;
    }

    public void setS(Salida s) {
        this.s = s;
    }        
    @Override
    public String toString() {
        return "TicketReportItem{" + "id=" + id + ", salida=" + salida + ", geofence=" + geofence + ", expectedTime=" + expectedTime + ", enterTime=" + enterTime + ", exitTime=" + exitTime + ", difference=" + difference + ", punishment=" + punishment + ", device=" + device + ", deviceName=" + deviceName + ", group=" + group + ", subroute=" + subroute + '}';
    }
}
