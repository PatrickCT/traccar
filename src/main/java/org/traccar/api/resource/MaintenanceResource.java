/*
 * Copyright 2018 Anton Tananaev (anton@traccar.org)
 * Copyright 2018 Andrey Kunitsyn (andrey@traccar.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar.api.resource;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.poi.ss.formula.functions.T;
import org.json.JSONObject;
import org.traccar.api.ExtendedObjectResource;
import org.traccar.model.*;
import org.traccar.storage.StorageException;
import org.traccar.storage.query.Columns;
import org.traccar.storage.query.Condition;
import org.traccar.storage.query.Request;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Path("maintenance")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MaintenanceResource extends ExtendedObjectResource<Maintenance> {

    public MaintenanceResource() {
        super(Maintenance.class);
    }

    @Path("{id}")
    @GET
    public Response getSingle(@PathParam("id") long id) throws StorageException {
        Maintenance entity = storage.getObject(Maintenance.class, new Request(
                new Columns.All(), new Condition.Equals("id", id)));
        if (entity != null) {
            JSONObject obj = new JSONObject();
            obj.put("id", entity.getId());
            obj.put("attributes", entity.getAttributes());
            obj.put("name", entity.getName());
            obj.put("period", entity.getPeriod());
            obj.put("start", entity.getStart());
            obj.put("type", entity.getType());
            List<Permission> p =storage.getPermissions(Device.class, Maintenance.class).stream().filter(m -> m.getPropertyId() == entity.getId()).collect(Collectors.toList());
            obj.put("device", (p.isEmpty()?null:storage.getObject(Device.class, new Request(new Columns.All(), new Condition.Equals("id", p.get(0).getOwnerId())))));
            return Response.ok(obj.toString()).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
