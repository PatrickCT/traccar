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
    private String de;
    
    //! Se cambio en la base de datos el nombre a ("a")
    private String a;
    
    private long subrouteId;

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

    public String getDe() {
        return de;
    }

    public void setDe(String de) {
        this.de = de;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }
    
    public long getSubrouteId() {
        return subrouteId;
    }

    public void setSubrouteId(long subrouteId) {
        this.subrouteId = subrouteId;
    }
    
    
}
