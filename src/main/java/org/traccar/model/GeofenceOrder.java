/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.model;

/**
 *
 * @author K
 */
public class GeofenceOrder {

    private int geofenceid;
    private int order;
    private boolean exits;

    public GeofenceOrder(int geofenceid, int order, boolean exits) {
        this.geofenceid = geofenceid;
        this.order = order;
        this.exits = exits;
    }

    public int getGeofenceid() {
        return geofenceid;
    }

    public void setGeofenceid(int geofenceid) {
        this.geofenceid = geofenceid;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isExits() {
        return exits;
    }

    public void setExits(boolean exits) {
        this.exits = exits;
    }

    @Override
    public String toString() {
        return "GeofenceOrder{" + "geofenceid=" + geofenceid + ", order=" + order + ", exits=" + exits + '}';
    }    
}
