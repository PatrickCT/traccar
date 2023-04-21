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
@StorageName("rolesusuario")
public class rolesusuario {

    private int IDROLUSUARIO;
    private String NOMBREROL;

    public int getIDROLUSUARIO() {
        return IDROLUSUARIO;
    }

    public void setIDROLUSUARIO(int IDROLUSUARIO) {
        this.IDROLUSUARIO = IDROLUSUARIO;
    }

    public String getNOMBREROL() {
        return NOMBREROL;
    }

    public void setNOMBREROL(String NOMBREROL) {
        this.NOMBREROL = NOMBREROL;
    }

}
