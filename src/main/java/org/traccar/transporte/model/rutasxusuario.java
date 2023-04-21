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
@StorageName("rutasxusuario")
public class rutasxusuario {
    private int IDRUTAXUSUARIO;
    private int IDUSUARIO;
    private int IDRUTA;

    public int getIDRUTAXUSUARIO() {
        return IDRUTAXUSUARIO;
    }

    public void setIDRUTAXUSUARIO(int IDRUTAXUSUARIO) {
        this.IDRUTAXUSUARIO = IDRUTAXUSUARIO;
    }

    public int getIDUSUARIO() {
        return IDUSUARIO;
    }

    public void setIDUSUARIO(int IDUSUARIO) {
        this.IDUSUARIO = IDUSUARIO;
    }

    public int getIDRUTA() {
        return IDRUTA;
    }

    public void setIDRUTA(int IDRUTA) {
        this.IDRUTA = IDRUTA;
    }
    
    
}
