/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.transporte.model;

import java.util.Date;
import org.traccar.storage.StorageName;

/**
 *
 * @author K
 */
@StorageName("bitacorarecorridos")
public class bitacorarecorridos {
    private int IDBITRECORRIDOS;
    private Long IMEI;
    private double LATITUD;
    private double LONGITUD;
    private Date HORA;
    private float VEL;

    public int getIDBITRECORRIDOS() {
        return IDBITRECORRIDOS;
    }

    public void setIDBITRECORRIDOS(int IDBITRECORRIDOS) {
        this.IDBITRECORRIDOS = IDBITRECORRIDOS;
    }

    public Long getIMEI() {
        return IMEI;
    }

    public void setIMEI(Long IMEI) {
        this.IMEI = IMEI;
    }

    public double getLATITUD() {
        return LATITUD;
    }

    public void setLATITUD(double LATITUD) {
        this.LATITUD = LATITUD;
    }

    public double getLONGITUD() {
        return LONGITUD;
    }

    public void setLONGITUD(double LONGITUD) {
        this.LONGITUD = LONGITUD;
    }

    public Date getHORA() {
        return HORA;
    }

    public void setHORA(Date HORA) {
        this.HORA = HORA;
    }

    public float getVEL() {
        return VEL;
    }

    public void setVEL(float VEL) {
        this.VEL = VEL;
    }
    
    
}
