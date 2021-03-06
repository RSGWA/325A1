package nz.ac.auckland.concert.service.services;

import java.net.URI;
import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nz.ac.auckland.concert.common.dto.CreditCardDTO;
import nz.ac.auckland.concert.common.dto.UserDTO;
import nz.ac.auckland.concert.common.message.Messages;
import nz.ac.auckland.concert.service.util.Config;

@Path("/users")
public class UserResource {
	
	private static Logger _logger = LoggerFactory
			.getLogger(ConcertResource.class);
	
	private NewCookie newToken;
	
	EntityManager em = PersistenceManager.instance().createEntityManager();
	
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public Response createUser(UserDTO newUser, @CookieParam(Config.TOKEN) Cookie token) {
		ResponseBuilder builder = null;
		em.getTransaction().begin();
		
		if (validUser(newUser)) {
			if (usernameExists(newUser)) {
				em.persist(newUser);
				_logger.debug("New User Created: " +newUser.getUsername());
				newToken = createToken(token, newUser.getUsername());
			
				builder = Response.status(201).location(URI.create("/users"));
				builder.cookie(newToken);
			} else {
				_logger.debug("Non-unique username");
				builder = Response.status(Status.BAD_REQUEST).entity(Messages.CREATE_USER_WITH_NON_UNIQUE_NAME);
			}
		} else {
			_logger.debug("Missing Fields");
			builder = Response.status(Status.BAD_REQUEST).entity(Messages.CREATE_USER_WITH_MISSING_FIELDS);
		}
		em.getTransaction().commit();
		return builder.build();
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	public Response registerCreditCard(CreditCardDTO creditCard, @CookieParam(Config.TOKEN) Cookie token) {
		Response.ResponseBuilder builder = null;
		em.getTransaction().begin();
		
		if (token == null) {
			_logger.debug("No authentication token");
			builder = Response.status(Status.UNAUTHORIZED).entity(Messages.UNAUTHENTICATED_REQUEST);
		} else {
			
			UserDTO loggedInUser = em.find(UserDTO.class, token.getValue());
			if (loggedInUser == null) {
				builder = Response.status(Status.BAD_REQUEST).entity(Messages.BAD_AUTHENTICATON_TOKEN);
			} else {
				em.persist(creditCard);
				_logger.debug("Credit Card : "+creditCard.getNumber()+" Registered under " + loggedInUser.getUsername());
				builder = Response.ok();
			}
		}
		
		em.getTransaction().commit();
		return builder.build();
	}
	
	@POST
	@Path("/authenticate")
	@Produces(MediaType.APPLICATION_XML)
	public Response authenticateUser(UserDTO user, @CookieParam(Config.TOKEN) Cookie token) {
		Response.ResponseBuilder builder = null;
		em.getTransaction().begin();
		
		_logger.debug("Authenticating " +user.getUsername()+"...");
		
		if (token == null) {
			_logger.debug("No authentication token");
			builder = Response.status(Status.BAD_REQUEST).entity(Messages.AUTHENTICATE_NON_EXISTENT_USER);
		} else {
			if (!validUser(user)) {
				_logger.debug("Missing Fields");
				builder = Response.status(Status.BAD_REQUEST).entity(Messages.AUTHENTICATE_USER_WITH_MISSING_FIELDS);
			} else {
				UserDTO fullUser = em.find(UserDTO.class, user.getUsername());
				if (fullUser == null) {
					builder = Response.status(Status.BAD_REQUEST).entity(Messages.AUTHENTICATE_NON_EXISTENT_USER);
				} else {
					if (!fullUser.getPassword().equals(user.getPassword())) {
						_logger.debug("Incorrect Password");
						builder = Response.status(Status.BAD_REQUEST).entity(Messages.AUTHENTICATE_USER_WITH_ILLEGAL_PASSWORD);
					} else {
						_logger.debug("Authenticated user");
						builder = Response.ok(fullUser);
					}
				}
			}
		}
		em.getTransaction().commit();
		
		return builder.build();
	}
	
	private NewCookie createToken(Cookie token, String username){
		NewCookie newToken = null;
		
		if(token == null) {
			newToken = new NewCookie(Config.TOKEN, username);
			_logger.debug("Generated Token: " + newToken.getValue());
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
