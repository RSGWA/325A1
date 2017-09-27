package nz.ac.auckland.concert.service.services;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nz.ac.auckland.concert.common.dto.ConcertDTO;

@Path("/concerts")
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
			builder = Response.status(Status.NOT_FOUND);
		} else {
			builder = Response.ok().entity(concert);
		}
		
		return builder.build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response retrieveAllConcerts() {
		em.getTransaction().begin();
		
		TypedQuery<ConcertDTO> concertQuery = em.createQuery("select c from ConcertDTO c", ConcertDTO.class);
		List<ConcertDTO> concerts = concertQuery.getResultList();
		ResponseBuilder builder = null;
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
	
}
