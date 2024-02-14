/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.model;

import java.util.Date;
import org.traccar.storage.StorageName;

/**
 *
 * @author USER
 */
@StorageName("tc_excuses")
public class Excuse extends BaseModel{

    private String description;
    Date applyFrom;
    Date applyTo;
    private Date availability;
    private int route;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getApplyFrom() {
        return applyFrom;
    }

    public void setApplyFrom(Date applyFrom) {
        this.applyFrom = applyFrom;
    }

    public Date getApplyTo() {
        return applyTo;
    }

    public void setApplyTo(Date applyTo) {
        this.applyTo = applyTo;
    }

    public Date getAvailability() {
        return availability;
    }

    public void setAvailability(Date availability) {
        this.availability = availability;
    }

    public int getRoute() {
        return route;
    }

    public void setRoute(int route) {
        this.route = route;
    }

    @Override
    public String toString() {
        return "Excuse{" + "description=" + description + ", applyFrom=" + applyFrom + ", applyTo=" + applyTo + ", availability=" + availability + ", route=" + route + '}';
    }   
}
