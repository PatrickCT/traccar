/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.transporte.model;

import java.sql.Time;
import java.util.Date;
import org.traccar.storage.StorageName;

/**
 *
 * @author K
 */
@StorageName("jornadasdetrabajo")
public class jornadasdetrabajo {
    private int IDJORTRABAJO;
    private int IDUSUARIO;
    private int IDITINERARIO;
    private Date FECHA;
    private Time HORAINICIO;
    private Time HORAFIN;

    public int getIDJORTRABAJO() {
        return IDJORTRABAJO;
    }

    public void setIDJORTRABAJO(int IDJORTRABAJO) {
        this.IDJORTRABAJO = IDJORTRABAJO;
    }

    public int getIDUSUARIO() {
        return IDUSUARIO;
    }

    public void setIDUSUARIO(int IDUSUARIO) {
        this.IDUSUARIO = IDUSUARIO;
    }

    public int getIDITINERARIO() {
        return IDITINERARIO;
    }

    public void setIDITINERARIO(int IDITINERARIO) {
        this.IDITINERARIO = IDITINERARIO;
    }

    public Date getFECHA() {
        return FECHA;
    }

    public void setFECHA(Date FECHA) {
        this.FECHA = FECHA;
    }

    public Time getHORAINICIO() {
        return HORAINICIO;
    }

    public void setHORAINICIO(Time HORAINICIO) {
        this.HORAINICIO = HORAINICIO;
    }

    public Time getHORAFIN() {
        return HORAFIN;
    }

    public void setHORAFIN(Time HORAFIN) {
        this.HORAFIN = HORAFIN;
    }
    
    
}
