package nz.ac.auckland.concert.services;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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

import nz.ac.auckland.concert.common.Config;
import nz.ac.auckland.concert.domain.Concert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to implement a simple REST Web service for managing concerts
 * 
 * ConcertResource implements a WEB service with the following interface:
 * 
 * - GET    <base-uri>/concerts/id
 *          Retrieves a concert based on their unique id. The HTTP response code 
 *          is either 200 or 404, depending on the existence of the required concert. 
 *          
 * - GET    <base-uri>/concerts? start & size
 * 			Retrieves a List of Concerts where the start query
 * 			parameter specifies the id of the first Concert to retrieve, and the size
 *  		parameter is the maximum number of Concerts to return (with increasing ids).
 *          
 * - POST   <base-uri>/concerts
 * 			creates a new Concert. The HTTP request message should contain a
 * 			Concert for which to generate an id and store in the Web service. The 
 * 			response message should have a status code of 201 and a Location header giving 
 * 			the URI of the new Concert.
 *          
 * - DELETE <base-uri>/concerts
 *          Deletes all Concerts known to the Web service. The HTTP response message’s 
 *          status code should be 204.        
 *
 */
@Path("/concerts")
public class ConcertResource {

	private static Logger _logger = LoggerFactory
			.getLogger(ConcertResource.class);

	// Declare necessary instance variables.
	private Map<Long, Concert> _concertDB = new ConcurrentHashMap<Long, Concert>();
	private AtomicLong _idCounter = new AtomicLong();
	private NewCookie newCookie;
 
	/**
	 * Retrieves a Concert based on its unique id. The HTTP response message 
	 * has a status code of either 200 or 404, depending on whether the 
	 * specified Concert is found. 
	 * 
	 * When clientId is null, the HTTP request message doesn't contain a cookie 
	 * named clientId (Config.CLIENT_COOKIE), this method generates a new 
	 * cookie, whose value is a randomly generated UUID. This method returns 
	 * the new cookie as part of the HTTP response message.
	 * 
	 * This method maps to the URI pattern <base-uri>/concerts/{id}.
	 * 
	 * @param id the unique ID of the Concert.
	 * 
	 * @param clientId a cookie named Config.CLIENT_COOKIE that may be sent 
	 * by the client.
	 * 
	 * @return a Response object containing the required Concert.
	 */
	@GET
	@Path("/{id}")
	@Produces({MediaType.APPLICATION_XML, 
		SerializationMessageBodyReaderAndWriter.APPLICATION_JAVA_SERIALIZED_OBJECT})
	public Response retrieveConcert(@PathParam("id") long id, @CookieParam(Config.CLIENT_COOKIE) Cookie clientId) {
		_logger.info("Retrieving concert with id: " + id);
		Concert concert = _concertDB.get(id);
		
		newCookie = makeCookie(clientId);
		Response.ResponseBuilder responseBuilder = null;
		if (concert == null) {
			responseBuilder = Response.status(404);
			if (newCookie != null) {
				responseBuilder.cookie(newCookie);
			}
		} else {
			responseBuilder = Response.status(200).entity(concert);
			if (newCookie != null) {
				responseBuilder.cookie(newCookie);
			}
		}
		return responseBuilder.build();
		
	}

	/**
	 * Retrieves a collection of Concerts, where the "start" query parameter
     * identifies an index position, and "size" represents the maximum number
     * of successive Concerts to return. The HTTP response message returns 200.
     * 
     * When clientId is null, the HTTP request message doesn't contain a cookie 
	 * named clientId (Config.CLIENT_COOKIE), this method generates a new 
	 * cookie, whose value is a randomly generated UUID. This method returns 
	 * the new cookie as part of the HTTP response message.
	 * 
	 * This method maps to the URI pattern <base-uri>/concerts?start&size.
	 * 
	 * @param start the ID of a Concert from which to start retrieving 
	 * Concerts.
	 * 
	 * @param size the maximum number of Concerts to retrieve.
	 * 
	 * @param clientId a cookie named Config.CLIENT_COOKIE that may be sent 
	 * by the client.
	 * 
	 * @return a Response object containing a List of Concerts. The List may be
	 * empty.
	 */
	@GET
	@Produces({MediaType.APPLICATION_XML, 
		SerializationMessageBodyReaderAndWriter.APPLICATION_JAVA_SERIALIZED_OBJECT})
	public Response retrieveConcerts(@QueryParam("start") long start, @QueryParam("size") int size, 
			@CookieParam(Config.CLIENT_COOKIE) Cookie clientId) {
		
		// If newCookie returns null, there is no need to send new cookie with response message
		newCookie = makeCookie(clientId);
		
		List<Concert> concerts = new ArrayList<Concert>();
		GenericEntity<List<Concert>> entity = new GenericEntity<List<Concert>>(concerts) {};
		ResponseBuilder builder = Response.ok(entity);
		
		_idCounter.set(start);
		
		for (int i = 0; i < size; i++) {
			Concert concertToAdd = _concertDB.get(_idCounter.get());
			if (concertToAdd != null) {
				concerts.add(concertToAdd);
			}
			_idCounter.incrementAndGet();
		}
		
		if (newCookie != null) {
			builder.cookie(newCookie);
		}
		return builder.build();
		// The Response object should store an ArrayList<Concert> entity. The 
		// ArrayList can be empty depending on the start and size arguments, 
		// and Concerts stored.
		//
		// Because of type erasure with Java Generics, any generically typed 
		// entity needs to be wrapped by a javax.ws.rs.core.GenericEntity that 
		// stores the generic type information. Hence to add an ArrayList as a
		// Response object's entity, you should use the following code:
		//
		// List<Concert> concerts = new ArrayList<Concert>();
		// GenericEntity<List<Concert>> entity = new GenericEntity<List<Concert>>(concerts) {};
		// ResponseBuilder builder = Response.ok(entity);
	}
	
	
	/**
	 * Creates a new Concert. This method assigns an ID to the new Concert and
	 * stores it in memory. The HTTP Response message returns a Location header 
	 * with the URI of the new Concert and a status code of 201.
	 * 
	 * When clientId is null, the HTTP request message doesn't contain a cookie 
	 * named clientId (Config.CLIENT_COOKIE), this method generates a new 
	 * cookie, whose value is a randomly generated UUID. This method returns 
	 * the new cookie as part of the HTTP response message.
	 * 
	 * This method maps to the URI pattern <base-uri>/concerts.
	 * 
	 * @param concert the new Concert to create.
	 * 
	 * @param clientId a cookie named Config.CLIENT_COOKIE that may be sent 
	 * by the client.
	 * 
	 * @return a Response object containing the status code 201 and a Location
	 * header.
	 */
	@POST
	@Consumes({MediaType.APPLICATION_XML, 
		SerializationMessageBodyReaderAndWriter.APPLICATION_JAVA_SERIALIZED_OBJECT})
	public Response createConcert(Concert concert, @CookieParam(Config.CLIENT_COOKIE) Cookie clientId) {
		
		newCookie = makeCookie(clientId);
		
		Concert newConcert = new Concert(_idCounter.incrementAndGet(),concert.getTitle(), concert.getDate());
		_concertDB.put(newConcert.getId(), newConcert);
		_logger.info("Created a concert with id: " + newConcert.getId());
		
		Response.ResponseBuilder builder = Response.status(201).location(URI.create("/concerts/"+newConcert.getId()));
		
		if (newCookie != null) {
			builder.cookie(newCookie);
		}
		return builder.build();
	}


	/**
	 * Deletes all Concerts, returning a status code of 204.  
	 * 
	 * When clientId is null, the HTTP request message doesn't contain a cookie 
	 * named clientId (Config.CLIENT_COOKIE), this method generates a new 
	 * cookie, whose value is a randomly generated UUID. This method returns 
	 * the new cookie as part of the HTTP response message.
	 * 
	 * This method maps to the URI pattern <base-uri>/concerts.
	 * 
	 * @param clientId a cookie named Config.CLIENT_COOKIE that may be sent 
	 * by the client.
	 * 
	 * @return a Response object containing the status code 204.
	 */
	@DELETE
	public Response deleteAllConcerts(@CookieParam(Config.CLIENT_COOKIE) Cookie clientId) {
		
		newCookie = makeCookie(clientId);
		
		_concertDB.clear();
		_idCounter = new AtomicLong();
		
		Response.ResponseBuilder builder = Response.status(204);
		
		if (newCookie != null) {
			builder.cookie(newCookie);
		}
		
		return builder.build();
	}
}
