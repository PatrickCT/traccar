/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.transporte.model;

import java.math.BigDecimal;
import java.sql.Time;
import org.traccar.storage.StorageName;

/**
 *
 * @author K
 */
@StorageName("itinerarios")
public class itinerarios {

    private int IDITINERARIO;
    private int IDSUBRUTA;
    private String NOMBREITINERARIO;
    private BigDecimal MULTAECONOMICA;
    private Time HRINCIO;
    private Time HRFIN;
    private String DIAS;

    public int getIDITINERARIO() {
        return IDITINERARIO;
    }

    public void setIDITINERARIO(int IDITINERARIO) {
        this.IDITINERARIO = IDITINERARIO;
    }

    public int getIDSUBRUTA() {
        return IDSUBRUTA;
    }

    public void setIDSUBRUTA(int IDSUBRUTA) {
        this.IDSUBRUTA = IDSUBRUTA;
    }

    public String getNOMBREITINERARIO() {
        return NOMBREITINERARIO;
    }

    public void setNOMBREITINERARIO(String NOMBREITINERARIO) {
        this.NOMBREITINERARIO = NOMBREITINERARIO;
    }

    public BigDecimal getMULTAECONOMICA() {
        return MULTAECONOMICA;
    }

    public void setMULTAECONOMICA(BigDecimal MULTAECONOMICA) {
        this.MULTAECONOMICA = MULTAECONOMICA;
    }

    public Time getHRINCIO() {
        return HRINCIO;
    }

    public void setHRINCIO(Time HRINCIO) {
        this.HRINCIO = HRINCIO;
    }

    public Time getHRFIN() {
        return HRFIN;
    }

    public void setHRFIN(Time HRFIN) {
        this.HRFIN = HRFIN;
    }

    public String getDIAS() {
        return DIAS;
    }

    public void setDIAS(String DIAS) {
        this.DIAS = DIAS;
    }

}
