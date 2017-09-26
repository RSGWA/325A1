package nz.ac.auckland.concert.service.services;

import java.net.URI;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nz.ac.auckland.concert.common.dto.ConcertDTO;
import nz.ac.auckland.concert.common.dto.PerformerDTO;
import nz.ac.auckland.concert.common.dto.UserDTO;

@Path("/concerts")
@Consumes(MediaType.APPLICATION_XML)
public class ConcertResource {

	private static Logger _logger = LoggerFactory
			.getLogger(ConcertResource.class);
	
	EntityManager em = PersistenceManager.instance().createEntityManager();
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_XML)
	public Response retrieveConcert(@PathParam("id") Long id) {
		em.getTransaction().begin();
		ConcertDTO concert = em.find(ConcertDTO.class, id);
		
		Response.ResponseBuilder builder = null;
		if (concert == null) {
			builder = Response.status(404);
		} else {
			builder = Response.status(200).entity(concert);
		}
		
		return builder.build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response retrieveAllConcerts() {
		em.getTransaction().begin();
		
		TypedQuery<ConcertDTO> concertQuery = em.createQuery("select c from ConcertDTO c", ConcertDTO.class);
		List<ConcertDTO> concerts = concertQuery.getResultList();
		
		Response.ResponseBuilder builder = null;
		GenericEntity<List<ConcertDTO>> genericEntity = new GenericEntity<List<ConcertDTO>>(concerts) {};
		
		for (ConcertDTO concertRetrieved : concerts) {
			if (concertRetrieved == null) {
				builder = Response.status(404);
				return builder.build();
			}
		}
		
		builder = Response.ok(genericEntity);
		
		return builder.build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response retrieveAllPerformers() {
		em.getTransaction().begin();
		
		TypedQuery<PerformerDTO> performerQuery = em.createQuery("select p from PerformerDTO p", PerformerDTO.class);
		List<PerformerDTO> performers = performerQuery.getResultList();
		
		Response.ResponseBuilder builder = null;
		GenericEntity<List<PerformerDTO>> genericEntity = new GenericEntity<List<PerformerDTO>>(performers) {};
		
		for (PerformerDTO performerRetrieved : performers) {
			if (performerRetrieved == null) {
				builder = Response.status(404);
				return builder.build();
			}
		}
		
		builder = Response.ok(genericEntity);
		
		return builder.build();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public Response createUser(UserDTO newUser) {
		em.getTransaction().begin();
		
		em.persist(newUser);
		
		Response.ResponseBuilder builder = Response.status(201).location(URI.create("/concerts"));
		
		em.getTransaction().commit();
		return builder.build();
	}

}
