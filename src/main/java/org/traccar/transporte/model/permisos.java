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
@StorageName("permisos")
public class permisos {
    private int IDPERMISOS;
    private int IDROLUSUARIO;
    private int IDMODULO;
    private String TIPOPERMISO;
    private boolean CONCEDIDO;

    public int getIDPERMISOS() {
        return IDPERMISOS;
    }

    public void setIDPERMISOS(int IDPERMISOS) {
        this.IDPERMISOS = IDPERMISOS;
    }

    public int getIDROLUSUARIO() {
        return IDROLUSUARIO;
    }

    public void setIDROLUSUARIO(int IDROLUSUARIO) {
        this.IDROLUSUARIO = IDROLUSUARIO;
    }

    public int getIDMODULO() {
        return IDMODULO;
    }

    public void setIDMODULO(int IDMODULO) {
        this.IDMODULO = IDMODULO;
    }

    public String getTIPOPERMISO() {
        return TIPOPERMISO;
    }

    public void setTIPOPERMISO(String TIPOPERMISO) {
        this.TIPOPERMISO = TIPOPERMISO;
    }

    public boolean isCONCEDIDO() {
        return CONCEDIDO;
    }

    public void setCONCEDIDO(boolean CONCEDIDO) {
        this.CONCEDIDO = CONCEDIDO;
    }
    
    
}
