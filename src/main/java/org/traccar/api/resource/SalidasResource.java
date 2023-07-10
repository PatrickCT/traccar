/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.api.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import org.traccar.api.BaseObjectResource;
import org.traccar.api.BaseResource;
import org.traccar.model.Salida;

/**
 *
 * @author K
 */
@Path("salidas")
public class SalidasResource extends BaseObjectResource<Salida>{
    
    public SalidasResource() {
        super(Salida.class);
    }
    
}
