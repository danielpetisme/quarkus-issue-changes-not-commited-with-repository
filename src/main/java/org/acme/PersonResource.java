package org.acme;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.UriBuilder.fromPath;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path(value = "/api/persons")
public class PersonResource {

    private final Logger log = LoggerFactory.getLogger(PersonResource.class);

    @POST
    @Transactional
    public Response createFruit(@Valid Person person) {
        Person.persist(person);
        return Response
                .created(fromPath("api/persons").path(person.id.toString()).build())
                .entity(person).build();
    }

    @GET
    @Path("/{id}")
    @Transactional
    public Response getOne(@PathParam(value = "id") Long id) {
        Person result = Person.findById(id);
        if (result == null) {
            throw new WebApplicationException("Not found", NOT_FOUND);
        }
        return Response.ok(result).build();
    }

    @PUT
    @Transactional
    public Response updateFruit(@Valid Person person) {
        Person updated = Person.findById(person.id);
        if (updated == null) {
            throw new WebApplicationException("Person with id of " + person.id + " does not exist.", NOT_FOUND);
        }
        updated.name = person.name;
        return Response.ok(updated).build();
    }
}
