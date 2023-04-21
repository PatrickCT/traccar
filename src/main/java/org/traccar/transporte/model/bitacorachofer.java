/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.transporte.model;

/**
 *
 * @author K
 */
import java.util.Date;
import org.traccar.model.BaseModel;
import org.traccar.storage.StorageName;

@StorageName("bitacorachofer")
public class bitacorachofer extends BaseModel{
    private int IDBITACORA;
    private int IDCHOFER;
    private String NOTA;
    private Date FECHA;
    private int USUARIOALTA;

    public int getIDBITACORA() {
        return IDBITACORA;
    }

    public void setIDBITACORA(int IDBITACORA) {
        this.IDBITACORA = IDBITACORA;
    }

    public int getIDCHOFER() {
        return IDCHOFER;
    }

    public void setIDCHOFER(int IDCHOFER) {
        this.IDCHOFER = IDCHOFER;
    }

    public String getNOTA() {
        return NOTA;
    }

    public void setNOTA(String NOTA) {
        this.NOTA = NOTA;
    }

    public Date getFECHA() {
        return FECHA;
    }

    public void setFECHA(Date FECHA) {
        this.FECHA = FECHA;
    }

    public int getUSUARIOALTA() {
        return USUARIOALTA;
    }

    public void setUSUARIOALTA(int USUARIOALTA) {
        this.USUARIOALTA = USUARIOALTA;
    }
    
    
}
