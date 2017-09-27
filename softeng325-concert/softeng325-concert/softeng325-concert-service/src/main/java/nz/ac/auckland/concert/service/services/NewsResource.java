package nz.ac.auckland.concert.service.services;

import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nz.ac.auckland.concert.common.dto.NewsItemDTO;
import nz.ac.auckland.concert.common.message.Messages;

@Path("/news")
public class NewsResource {

	private static Logger _logger = LoggerFactory
			.getLogger(NewsResource.class);
	
	EntityManager em = PersistenceManager.instance().createEntityManager();
	
	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	public Response addNewsItem(NewsItemDTO newsItem) {
		Response.ResponseBuilder builder = null;
		em.getTransaction().begin();
		
		if (newsItem != null) {
			em.persist(newsItem);
			builder = Response.ok();
		} else {
			builder = Response.status(Status.INTERNAL_SERVER_ERROR).entity(Messages.SERVICE_COMMUNICATION_ERROR);
		}
		
		em.getTransaction().commit();
		return builder.build();
	}
}
