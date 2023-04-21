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
@StorageName("detcorridas")
public class detcorridas {
    private int IDDETCORRIDAS;
    private int IDADMCORRIDAS;
    private int IDPUNTOCONTROL;
    private Time HORAMIN;
    private Time HORAMAX;
    private Time HORAREAL;

    public int getIDDETCORRIDAS() {
        return IDDETCORRIDAS;
    }

    public void setIDDETCORRIDAS(int IDDETCORRIDAS) {
        this.IDDETCORRIDAS = IDDETCORRIDAS;
    }

    public int getIDADMCORRIDAS() {
        return IDADMCORRIDAS;
    }

    public void setIDADMCORRIDAS(int IDADMCORRIDAS) {
        this.IDADMCORRIDAS = IDADMCORRIDAS;
    }

    public int getIDPUNTOCONTROL() {
        return IDPUNTOCONTROL;
    }

    public void setIDPUNTOCONTROL(int IDPUNTOCONTROL) {
        this.IDPUNTOCONTROL = IDPUNTOCONTROL;
    }

    public Time getHORAMIN() {
        return HORAMIN;
    }

    public void setHORAMIN(Time HORAMIN) {
        this.HORAMIN = HORAMIN;
    }

    public Time getHORAMAX() {
        return HORAMAX;
    }

    public void setHORAMAX(Time HORAMAX) {
        this.HORAMAX = HORAMAX;
    }

    public Time getHORAREAL() {
        return HORAREAL;
    }

    public void setHORAREAL(Time HORAREAL) {
        this.HORAREAL = HORAREAL;
    }
    
    
}
