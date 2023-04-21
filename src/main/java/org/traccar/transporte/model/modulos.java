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
@StorageName("modulos")
public class modulos {
    private int IDMODULO;
    private String NOMMODULO;

    public int getIDMODULO() {
        return IDMODULO;
    }

    public void setIDMODULO(int IDMODULO) {
        this.IDMODULO = IDMODULO;
    }

    public String getNOMMODULO() {
        return NOMMODULO;
    }

    public void setNOMMODULO(String NOMMODULO) {
        this.NOMMODULO = NOMMODULO;
    }
    
    
}
