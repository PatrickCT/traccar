/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.reports.model;

import java.util.List;

/**
 *
 * @author K
 */
public class VueltaReportSection {
    private long itinerario;
    private String deviceName;
    private String groupName = "";
    private List<?> objects;

    public long getItinerario() {
        return itinerario;
    }

    public void setItinerario(long itinerario) {
        this.itinerario = itinerario;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<?> getObjects() {
        return objects;
    }

    public void setObjects(List<?> objects) {
        this.objects = objects;
    }
    
}
