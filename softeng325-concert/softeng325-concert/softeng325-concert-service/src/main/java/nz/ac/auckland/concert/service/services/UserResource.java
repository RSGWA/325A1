package nz.ac.auckland.concert.service.services;

import java.net.URI;

import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nz.ac.auckland.concert.common.dto.UserDTO;
import nz.ac.auckland.concert.common.message.Messages;

@Path("/users")
public class UserResource {
	
	private static Logger _logger = LoggerFactory
			.getLogger(ConcertResource.class);
	
	public static final String USER_TOKEN = "userToken";
	EntityManager em = PersistenceManager.instance().createEntityManager();
	
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public Response createUser(UserDTO newUser, @CookieParam(USER_TOKEN) Cookie token) {
		ResponseBuilder builder = null;
		em.getTransaction().begin();

		
		if (validUser(newUser)) {
			if (usernameExists(newUser)) {
				em.persist(newUser);
				_logger.info("New User Created: " +newUser.getUsername());
				NewCookie newToken = createToken(token, newUser.getUsername());
			
				builder = Response.status(201).location(URI.create("/users/"+newUser.getUsername()));
				builder.cookie(newToken);
			} else {
				_logger.info("Non-unique username");
				builder = Response.status(Status.BAD_REQUEST).entity(Messages.CREATE_USER_WITH_NON_UNIQUE_NAME);
			}
		} else {
			_logger.info("Missing Fields");
			builder = Response.status(Status.BAD_REQUEST).entity(Messages.CREATE_USER_WITH_MISSING_FIELDS);
		}
		em.getTransaction().commit();
		return builder.build();
	}
	
	@POST
	@Path("/authenticate")
	@Produces(MediaType.APPLICATION_XML)
	public Response authenticateUser(UserDTO user, @CookieParam(USER_TOKEN) Cookie token) {
		Response.ResponseBuilder builder = null;
		em.getTransaction().begin();
		
		_logger.info("Authenticating: " +user.getUsername());
		
		if (token == null) {
			_logger.info("Unauthenticated user");
		} else {
			_logger.info("Authenticated user");
			UserDTO fullUser = em.find(UserDTO.class, user.getUsername());
			builder = Response.ok(fullUser);
		}
		
		em.getTransaction().commit();
		return builder.build();
	}
	
	private NewCookie createToken(Cookie token, String username){
		NewCookie newToken = null;
		
		if(token == null) {
			newToken = new NewCookie(USER_TOKEN, username);
			_logger.info("Generated Token: " + newToken.getValue());
		} 
		
		return newToken;
	}
	
	private boolean validUser(UserDTO user) {
		if (user.getUsername() == "" || user.getPassword() == "" || user.getFirstname() == "" || user.getLastname() == "") {
			return false;
		}
		
		if (user.getUsername() == null || user.getPassword() == null) {
			return false;
		}
		return true;
	}

	private boolean usernameExists(UserDTO user) {
		UserDTO domainUser = em.find(UserDTO.class, user.getUsername());
		if (domainUser == null) {
			return true;
		} else {
			return false;
		}
		
	}
}
