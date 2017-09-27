package nz.ac.auckland.concert.service.services;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nz.ac.auckland.concert.common.dto.UserDTO;
import nz.ac.auckland.concert.common.message.Messages;
import nz.ac.auckland.concert.service.domain.Booking;
import nz.ac.auckland.concert.service.domain.Reservation;
import nz.ac.auckland.concert.service.util.Config;

@Path("/reservations")
public class ReservationResource {

	private static Logger _logger = LoggerFactory
			.getLogger(ReservationResource.class);
	
	EntityManager em = PersistenceManager.instance().createEntityManager();
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response getBookings(@CookieParam(Config.TOKEN) Cookie token) {
		ResponseBuilder builder = null;
		em.getTransaction().begin();
		
		if (token == null) {
			_logger.debug("No authentication token/Request made by unauthorized user");
			builder = Response.status(Status.UNAUTHORIZED).entity(Messages.UNAUTHENTICATED_REQUEST);
		} else {
			UserDTO user = em.find(UserDTO.class, token.getValue());
			if (user == null) {
				builder = Response.status(Status.BAD_REQUEST).entity(Messages.BAD_AUTHENTICATON_TOKEN);
			} else {
				TypedQuery<Booking> bookingsQuery = em.createQuery("select b from BookingDTO b", Booking.class);
				List<Booking> allBookings = bookingsQuery.getResultList();
				List<Booking> bookings = new ArrayList<>();
				for (Booking booking : allBookings) {
					if (token.getValue().equals(booking.getUsername())) {
						bookings.add(booking);
					}
				}
				GenericEntity<List<Booking>> genericEntity = new GenericEntity<List<Booking>>(bookings) {};
				
				builder = Response.ok(genericEntity);
			}
			
			em.getTransaction().commit();
		}
		return builder.build();
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_XML)
	public Response getAllBookingsByConcert(@PathParam("id") Long id) {
		ResponseBuilder builder = null;
		em.getTransaction().begin();
		
		
		TypedQuery<Booking> bookingsQuery = em.createQuery("select b from BookingDTO b", Booking.class);
		List<Booking> allBookings = bookingsQuery.getResultList();
		List<Booking> bookings = new ArrayList<>();
		for (Booking booking : allBookings) {
			if (id == booking.getConcertId()) {
				bookings.add(booking);
			}
		}
		GenericEntity<List<Booking>> genericEntity = new GenericEntity<List<Booking>>(bookings) {};
				
		builder = Response.ok(genericEntity);
	
		em.getTransaction().commit();
		return builder.build();
	}
	
	@PUT
	@Produces(MediaType.APPLICATION_XML)
	public Response reserveSeats(Reservation reservation, @CookieParam(Config.TOKEN) Cookie token) {
		ResponseBuilder builder = null;
		em.getTransaction().begin();
		
		if (token == null) {
			_logger.debug("No authentication token/Request made by unauthorized user");
			builder = Response.status(Status.UNAUTHORIZED).entity(Messages.UNAUTHENTICATED_REQUEST);
		} else {
			UserDTO user = em.find(UserDTO.class, token.getValue());
			if (user == null) {
				builder = Response.status(Status.BAD_REQUEST).entity(Messages.BAD_AUTHENTICATON_TOKEN);
			} else {
				reservation.setUsername(token.getValue());
				em.persist(reservation);
				builder = Response.ok(reservation);
			}
			em.getTransaction().commit();
		}
		return builder.build();
	}
}
