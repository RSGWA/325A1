package nz.ac.auckland.concert.service.services;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nz.ac.auckland.concert.common.dto.PerformerDTO;

@Path("/performers")
public class PerformerResource {

	private static Logger _logger = LoggerFactory
			.getLogger(PerformerResource.class);
	
	EntityManager em = PersistenceManager.instance().createEntityManager();
	
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
}
