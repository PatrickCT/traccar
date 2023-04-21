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
@StorageName("tiempoextraitinerario")
public class tiempoextraitinerario {

    private int IDTIEMPO;
    private int IDITINERARIO;
    private Time TIEMPOADICIONAL;
    private Date FECHAALTA;
    private Date FECHAMODI;

    public int getIDTIEMPO() {
        return IDTIEMPO;
    }

    public void setIDTIEMPO(int IDTIEMPO) {
        this.IDTIEMPO = IDTIEMPO;
    }

    public int getIDITINERARIO() {
        return IDITINERARIO;
    }

    public void setIDITINERARIO(int IDITINERARIO) {
        this.IDITINERARIO = IDITINERARIO;
    }

    public Time getTIEMPOADICIONAL() {
        return TIEMPOADICIONAL;
    }

    public void setTIEMPOADICIONAL(Time TIEMPOADICIONAL) {
        this.TIEMPOADICIONAL = TIEMPOADICIONAL;
    }

    public Date getFECHAALTA() {
        return FECHAALTA;
    }

    public void setFECHAALTA(Date FECHAALTA) {
        this.FECHAALTA = FECHAALTA;
    }

    public Date getFECHAMODI() {
        return FECHAMODI;
    }

    public void setFECHAMODI(Date FECHAMODI) {
        this.FECHAMODI = FECHAMODI;
    }

}
