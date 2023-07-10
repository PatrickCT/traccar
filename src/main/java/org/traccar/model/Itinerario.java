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
public class Itinerario extends BaseModel {
    private String name;
    private int days;
    //! Se cambio en la base de datos el nombre a ("de")
    private String from;
    
    //! Se cambio en la base de datos el nombre a ("a")
    private String to;

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

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
    
    
}
