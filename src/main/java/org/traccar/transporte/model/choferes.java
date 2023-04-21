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
@StorageName("choferes")
public class choferes {
    private int IDCHOFER;
    private String NOMBRE;
    private String APPATERNO;
    private String APMATERNO;
    private String NUMLIC;
    private Date VIGENCIALIC;
    private String CURP;
    private boolean ESTATUS;
    private int IDRUTA;

    public int getIDCHOFER() {
        return IDCHOFER;
    }

    public void setIDCHOFER(int IDCHOFER) {
        this.IDCHOFER = IDCHOFER;
    }

    public String getNOMBRE() {
        return NOMBRE;
    }

    public void setNOMBRE(String NOMBRE) {
        this.NOMBRE = NOMBRE;
    }

    public String getAPPATERNO() {
        return APPATERNO;
    }

    public void setAPPATERNO(String APPATERNO) {
        this.APPATERNO = APPATERNO;
    }

    public String getAPMATERNO() {
        return APMATERNO;
    }

    public void setAPMATERNO(String APMATERNO) {
        this.APMATERNO = APMATERNO;
    }

    public String getNUMLIC() {
        return NUMLIC;
    }

    public void setNUMLIC(String NUMLIC) {
        this.NUMLIC = NUMLIC;
    }

    public Date getVIGENCIALIC() {
        return VIGENCIALIC;
    }

    public void setVIGENCIALIC(Date VIGENCIALIC) {
        this.VIGENCIALIC = VIGENCIALIC;
    }

    public String getCURP() {
        return CURP;
    }

    public void setCURP(String CURP) {
        this.CURP = CURP;
    }

    public boolean isESTATUS() {
        return ESTATUS;
    }

    public void setESTATUS(boolean ESTATUS) {
        this.ESTATUS = ESTATUS;
    }

    public int getIDRUTA() {
        return IDRUTA;
    }

    public void setIDRUTA(int IDRUTA) {
        this.IDRUTA = IDRUTA;
    }
    
    
    
}
