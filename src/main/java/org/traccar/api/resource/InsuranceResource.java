/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.traccar.api.resource;

import java.util.Collection;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.traccar.api.BaseObjectResource;
import org.traccar.model.InsuranceCompany;
import org.traccar.storage.StorageException;
import org.traccar.storage.query.Columns;
import org.traccar.storage.query.Request;

/**
 *
 * @author K
 */
@Path("insurance")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InsuranceResource extends BaseObjectResource<InsuranceCompany>{
    
    public InsuranceResource() {
        super(InsuranceCompany.class);
    }
    
    @GET
    @Path("companies")
    public Collection<InsuranceCompany> companies() throws StorageException{
        List<InsuranceCompany> companies = storage.getObjects(InsuranceCompany.class, new Request(new Columns.All()));
        
        return companies;
    }
}
