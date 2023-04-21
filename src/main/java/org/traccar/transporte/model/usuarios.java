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
@StorageName("usuarios")
public class usuarios {

    private int IDUSUARIO;
    private int IDROLUSUARIO;
    private int IDEMPRESA;
    private String NOMBRE;
    private String APPATERNO;
    private String APMATERNO;
    private String CORREO;
    private String TELEFONO;
    private boolean ESTATUS;
    private String PASSWORD;

    public int getIDUSUARIO() {
        return IDUSUARIO;
    }

    public void setIDUSUARIO(int IDUSUARIO) {
        this.IDUSUARIO = IDUSUARIO;
    }

    public int getIDROLUSUARIO() {
        return IDROLUSUARIO;
    }

    public void setIDROLUSUARIO(int IDROLUSUARIO) {
        this.IDROLUSUARIO = IDROLUSUARIO;
    }

    public int getIDEMPRESA() {
        return IDEMPRESA;
    }

    public void setIDEMPRESA(int IDEMPRESA) {
        this.IDEMPRESA = IDEMPRESA;
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

    public String getCORREO() {
        return CORREO;
    }

    public void setCORREO(String CORREO) {
        this.CORREO = CORREO;
    }

    public String getTELEFONO() {
        return TELEFONO;
    }

    public void setTELEFONO(String TELEFONO) {
        this.TELEFONO = TELEFONO;
    }

    public boolean isESTATUS() {
        return ESTATUS;
    }

    public void setESTATUS(boolean ESTATUS) {
        this.ESTATUS = ESTATUS;
    }

    public String getPASSWORD() {
        return PASSWORD;
    }

    public void setPASSWORD(String PASSWORD) {
        this.PASSWORD = PASSWORD;
    }

    public int getId() {
        return this.IDUSUARIO;
    }
}
