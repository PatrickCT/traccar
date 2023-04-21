/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.transporte.model;

/**
 *
 * @author K
 */
import java.sql.Time;
import java.util.Date;
import org.traccar.model.BaseModel;
import org.traccar.storage.StorageName;

@StorageName("admcorridas")
public class admcorridas extends BaseModel{
    private int IDCORRIDA;
    private int IDITINERARIO;
    private int IDJORNADATRABAJO;
    private int IDUNIDAD;
    private Time HORASALIDA;
    private Date FECHA;
    private boolean FINALIZADA;

    public int getIDCORRIDA() {
        return IDCORRIDA;
    }

    public void setIDCORRIDA(int IDCORRIDA) {
        this.IDCORRIDA = IDCORRIDA;
    }

    public int getIDITINERARIO() {
        return IDITINERARIO;
    }

    public void setIDITINERARIO(int IDITINERARIO) {
        this.IDITINERARIO = IDITINERARIO;
    }

    public int getIDJORNADATRABAJO() {
        return IDJORNADATRABAJO;
    }

    public void setIDJORNADATRABAJO(int IDJORNADATRABAJO) {
        this.IDJORNADATRABAJO = IDJORNADATRABAJO;
    }

    public int getIDUNIDAD() {
        return IDUNIDAD;
    }

    public void setIDUNIDAD(int IDUNIDAD) {
        this.IDUNIDAD = IDUNIDAD;
    }

    public Time getHORASALIDA() {
        return HORASALIDA;
    }

    public void setHORASALIDA(Time HORASALIDA) {
        this.HORASALIDA = HORASALIDA;
    }

    public Date getFECHA() {
        return FECHA;
    }

    public void setFECHA(Date FECHA) {
        this.FECHA = FECHA;
    }

    public boolean isFINALIZADA() {
        return FINALIZADA;
    }

    public void setFINALIZADA(boolean FINALIZADA) {
        this.FINALIZADA = FINALIZADA;
    }
    
    
    
}
