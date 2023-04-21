/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.transporte.model;

import org.traccar.storage.StorageName;

/**
 *
 * @author K
 */
@StorageName("ruta")
public class ruta {
    private int IDRUTA;
    private String TIPORUTA;
    private String NOMBRE;
    private String COLOR;

    public int getIDRUTA() {
        return IDRUTA;
    }

    public void setIDRUTA(int IDRUTA) {
        this.IDRUTA = IDRUTA;
    }

    public String getTIPORUTA() {
        return TIPORUTA;
    }

    public void setTIPORUTA(String TIPORUTA) {
        this.TIPORUTA = TIPORUTA;
    }

    public String getNOMBRE() {
        return NOMBRE;
    }

    public void setNOMBRE(String NOMBRE) {
        this.NOMBRE = NOMBRE;
    }

    public String getCOLOR() {
        return COLOR;
    }

    public void setCOLOR(String COLOR) {
        this.COLOR = COLOR;
    }
    
    
}
