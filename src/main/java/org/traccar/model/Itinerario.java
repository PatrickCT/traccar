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
@StorageName("tc_itinerarios")
public class Itinerario extends ExtendedModel {

    private String name;
    private int days;
    private long subrouteId;
    private long horasId;
    private long geofenceId;
    private long horasIdRel;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public long getSubrouteId() {
        return subrouteId;
    }

    public void setSubrouteId(long subrouteId) {
        this.subrouteId = subrouteId;
    }

    public long getHorasId() {
        return horasId;
    }

    public void setHorasId(long horasId) {
        this.horasId = horasId;
    }

    public long getGeofenceId() {
        return geofenceId;
    }

    public void setGeofenceId(long geofenceId) {
        this.geofenceId = geofenceId;
    }

    public long getHorasIdRel() {
        return horasIdRel;
    }

    public void setHorasIdRel(long horasIdRel) {
        this.horasIdRel = horasIdRel;
    }        

    @Override
    public String toString() {
        return "Itinerario{" + "name=" + name + ", days=" + days + ", subrouteId=" + subrouteId + ", horasId=" + horasId + ", geofenceId=" + geofenceId + ", attributes=" + getAttributes() + '}';
    }
    

}
