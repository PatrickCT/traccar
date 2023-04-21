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
@StorageName("usuario_center")
public class usuario_center {

    private int idusrcenter;
    private int idusuario;
    private double latitude;
    private double longitude;

    public int getIdusrcenter() {
        return idusrcenter;
    }

    public void setIdusrcenter(int idusrcenter) {
        this.idusrcenter = idusrcenter;
    }

    public int getIdusuario() {
        return idusuario;
    }

    public void setIdusuario(int idusuario) {
        this.idusuario = idusuario;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

}
