package org.acme;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.UriBuilder.fromPath;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path(value = "/api/fruits")
public class FruitResource {

    private final Logger log = LoggerFactory.getLogger(FruitResource.class);

    @Inject
    FruitRepository fruitRepository;

    @POST
    @Transactional
    public Response createFruit(@Valid Fruit fruit) {
        fruitRepository.persistAndFlush(fruit);
        return Response
                .created(fromPath("api/fruits").path(fruit.id.toString()).build())
                .entity(fruit).build();
    }

    @GET
    @Path("/{id}")
    @Transactional
    public Response getOne(@PathParam(value = "id") Long id) {
        Fruit result = fruitRepository.findById(id);
        if (result == null) {
            throw new WebApplicationException("Not found", NOT_FOUND);
        }
        return Response.ok(result).build();
    }

    @PUT
    @Transactional
    public Response updateFruit(@Valid Fruit fruit) {
        Fruit updated = fruitRepository.findById(fruit.id);
        if (updated == null) {
            throw new WebApplicationException("Fruit with id of " + fruit.id + " does not exist.", NOT_FOUND);
        }
        updated.name = fruit.name;
//        log.info(updated + "is persistent: " + fruitRepository.isPersistent(updated));
//        try {
//            fruitRepository.persist(updated);
//            fruitRepository.flush();
//        } catch (PersistenceException e) {
//            e.printStackTrace();
//        }
        return Response.ok(updated).build();
    }
}
