package nz.ac.auckland.concert.service.services;

import javax.persistence.EntityManager;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nz.ac.auckland.concert.service.util.Config;

@Path("/reservations")
public class ReservationResource {

	private static Logger _logger = LoggerFactory
			.getLogger(ReservationResource.class);
	
	EntityManager em = PersistenceManager.instance().createEntityManager();
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response getBookings(@CookieParam(Config.TOKEN) Cookie token) {
		Response.ResponseBuilder builder = null;
		em.getTransaction().begin();
		
		
		em.getTransaction().commit();
		return builder.build();
	}
}
