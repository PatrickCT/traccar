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
@StorageName("puntoscontrol")
public class puntoscontrol {

    private int IDPUNTOCONTROL;
    private int IDSUBRUTA;
    private double LONGITUD;
    private double LATITUD;
    private String NOMBRE;
    private int RADIO;
    private int ORDEN;

    public int getIDPUNTOCONTROL() {
        return IDPUNTOCONTROL;
    }

    public void setIDPUNTOCONTROL(int IDPUNTOCONTROL) {
        this.IDPUNTOCONTROL = IDPUNTOCONTROL;
    }

    public int getIDSUBRUTA() {
        return IDSUBRUTA;
    }

    public void setIDSUBRUTA(int IDSUBRUTA) {
        this.IDSUBRUTA = IDSUBRUTA;
    }

    public double getLONGITUD() {
        return LONGITUD;
    }

    public void setLONGITUD(double LONGITUD) {
        this.LONGITUD = LONGITUD;
    }

    public double getLATITUD() {
        return LATITUD;
    }

    public void setLATITUD(double LATITUD) {
        this.LATITUD = LATITUD;
    }

    public String getNOMBRE() {
        return NOMBRE;
    }

    public void setNOMBRE(String NOMBRE) {
        this.NOMBRE = NOMBRE;
    }

    public int getRADIO() {
        return RADIO;
    }

    public void setRADIO(int RADIO) {
        this.RADIO = RADIO;
    }

    public int getORDEN() {
        return ORDEN;
    }

    public void setORDEN(int ORDEN) {
        this.ORDEN = ORDEN;
    }

}
