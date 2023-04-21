/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.transporte.model;

import java.sql.Time;
import org.traccar.storage.StorageName;

/**
 *
 * @author K
 */
@StorageName("ditinerario")
public class ditinerario {
    private int IDDITINERARIO;
    private int IDITINERARIO;
    private int IDPUNTOCONTROL;
    private Time TLLEGADAMIN;
    private Time TLLEGADAMAX;
    private String NOMBRETRAMO;

    public int getIDDITINERARIO() {
        return IDDITINERARIO;
    }

    public void setIDDITINERARIO(int IDDITINERARIO) {
        this.IDDITINERARIO = IDDITINERARIO;
    }

    public int getIDITINERARIO() {
        return IDITINERARIO;
    }

    public void setIDITINERARIO(int IDITINERARIO) {
        this.IDITINERARIO = IDITINERARIO;
    }

    public int getIDPUNTOCONTROL() {
        return IDPUNTOCONTROL;
    }

    public void setIDPUNTOCONTROL(int IDPUNTOCONTROL) {
        this.IDPUNTOCONTROL = IDPUNTOCONTROL;
    }

    public Time getTLLEGADAMIN() {
        return TLLEGADAMIN;
    }

    public void setTLLEGADAMIN(Time TLLEGADAMIN) {
        this.TLLEGADAMIN = TLLEGADAMIN;
    }

    public Time getTLLEGADAMAX() {
        return TLLEGADAMAX;
    }

    public void setTLLEGADAMAX(Time TLLEGADAMAX) {
        this.TLLEGADAMAX = TLLEGADAMAX;
    }

    public String getNOMBRETRAMO() {
        return NOMBRETRAMO;
    }

    public void setNOMBRETRAMO(String NOMBRETRAMO) {
        this.NOMBRETRAMO = NOMBRETRAMO;
    }
    
    
}
