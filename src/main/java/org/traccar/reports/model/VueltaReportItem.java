/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.reports.model;

import java.util.Date;
import java.util.List;

/**
 *
 * @author K
 */
public class VueltaReportItem {
    private int id;
    private int itinerarioId;
    private List<VueltaDataItem> data;
    private int deviceId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getItinerarioId() {
        return itinerarioId;
    }

    public void setItinerarioId(int itinerarioId) {
        this.itinerarioId = itinerarioId;
    }

    public List<VueltaDataItem> getData() {
        return data;
    }

    public void setData(List<VueltaDataItem> data) {
        this.data = data;
    }
    
    public class VueltaDataItem{
        private long salida;
        private long dispositivo;
        private Date hora;
        private boolean asignado;

        public VueltaDataItem() {
        }                

        public long getSalida() {
            return salida;
        }

        public void setSalida(long salida) {
            this.salida = salida;
        }

        public long getDispositivo() {
            return dispositivo;
        }

        public void setDispositivo(long dispositivo) {
            this.dispositivo = dispositivo;
        }

        public Date getHora() {
            return hora;
        }

        public void setHora(Date hora) {
            this.hora = hora;
        }

        public boolean isAsignado() {
            return asignado;
        }

        public void setAsignado(boolean asignado) {
            this.asignado = asignado;
        }               

        @Override
        public String toString() {
            return "VueltaDataItem{" + "salida=" + salida + ", dispositivo=" + dispositivo + ", hora=" + hora + ", asignado=" + asignado + '}';
        }
                
    }
    
}
