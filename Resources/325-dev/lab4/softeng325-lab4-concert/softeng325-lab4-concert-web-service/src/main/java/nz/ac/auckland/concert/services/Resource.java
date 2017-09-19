package nz.ac.auckland.concert.services;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import nz.ac.auckland.concert.domain.Concert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/concerts")
public class Resource {

	private static Logger _logger = LoggerFactory
			.getLogger(Resource.class);

	EntityManager em = PersistenceManager.instance().createEntityManager();
	/**
	 * Retrieves a representation of a Concert, identified by its unique ID.
	 *	The HTTP response message should have a status code of either 200 or 404, depending on
	 *	whether the specified Concert is found.
	 * @param id
	 * @return
	 */
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_XML)
	public Response retrieveConcert(@PathParam("id") Long id) {
		em.getTransaction().begin();
		Concert concert = em.find(Concert.class, id);
		
		Response.ResponseBuilder builder = null;
		if (concert == null) {
			builder = Response.status(404);
		} else {
			builder = Response.status(200).entity(concert);
		}
		
		return builder.build();
	}

	/**
	 * Creates a Concert. The body of the HTTP request message contains
	 *	a representation of the new Concert (less unique ID) to create. The service generates the
	 *	Concert’s ID via the database, and returns a HTTP response of 201 with a Location header
	 *	storing the URI for the newly created Concert.
	 * @param concert
	 * @return
	 */
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public Response createConcert(Concert concert) {
		em.getTransaction().begin();
		
		Concert newConcert = new Concert(concert.getTitle(), 
				concert.getDate(), concert.getPerformer());
		
		em.persist(newConcert);
		
		Response.ResponseBuilder builder = Response.status(201).location(URI.create("/concerts/"+newConcert.getId()));
		
		em.getTransaction().commit();
		return builder.build();
	}
	
	/**
	 * Updates an existing Concert. A representation of the modified Concert
	 * 	is stored in the body of the HTTP request message. Being an existing Concert that was
	 * 	earlier created by the Web service, it should include a unique ID value. The HTTP status
	 * 	code should be 204 on success, or 404 where the Concert isn’t known to the Web service.
	 * @param concert
	 * @return
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	public Response updateConcert(Concert concert) {
		em.getTransaction().begin();
		
		Concert concertToFind = em.find(concert.getClass(), concert.getId());
		
		if (concertToFind == null) {
			return Response.status(404).build();
		}
		
		em.merge(concert);
		
		Response.ResponseBuilder builder = Response.status(204);
		
		em.getTransaction().commit();
		return builder.build();
	}

	/**
	 * Deletes a Concert, where the Concert to delete is specified by a
	 *	unique ID. This operation returns either 204 or 404, depending on whether the Concert
	 *	exists.
	 * @param id
	 * @return
	 */
	@DELETE
	@Produces(MediaType.APPLICATION_XML)
	@Path("/{id}")
	public Response deleteById(@PathParam("id") Long id) {
		em.getTransaction().begin();
		
		Concert concertToDelete = em.find(Concert.class, id);
		if (concertToDelete == null) {
			return Response.status(404).build();
		}
		
		em.remove(concertToDelete);
		em.getTransaction().commit();
		return Response.status(204).build();
	}
	
	/**
	 * Deletes all Concerts, and returns a 204 status code.
	 * @return
	 */
	@DELETE
	@Produces(MediaType.APPLICATION_XML)
	public Response deleteAllConcerts() {
		em.getTransaction().begin();
		TypedQuery<Concert> concertQuery = em.createQuery("select c from Concert c", Concert.class);
		List<Concert> concerts = concertQuery.getResultList();
		
		for (Concert concertToDelete : concerts) {
			em.remove(concertToDelete);
		}
		
		em.getTransaction().commit();
		return Response.status(204).build();
	}
	
}

