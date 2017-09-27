package nz.ac.auckland.concert.client.service;

import java.awt.Image;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nz.ac.auckland.concert.common.dto.BookingDTO;
import nz.ac.auckland.concert.common.dto.ConcertDTO;
import nz.ac.auckland.concert.common.dto.CreditCardDTO;
import nz.ac.auckland.concert.common.dto.PerformerDTO;
import nz.ac.auckland.concert.common.dto.ReservationDTO;
import nz.ac.auckland.concert.common.dto.ReservationRequestDTO;
import nz.ac.auckland.concert.common.dto.SeatDTO;
import nz.ac.auckland.concert.common.dto.UserDTO;
import nz.ac.auckland.concert.service.services.ConcertResource;
import nz.ac.auckland.concert.service.util.ReservationMapper;
import nz.ac.auckland.concert.service.util.TheatreUtility;


public class DefaultService implements ConcertService {

	private static Logger _logger = LoggerFactory
			.getLogger(DefaultService.class);
	
	private AtomicLong _reservationIdCounter = new AtomicLong();
	
	private static String CONCERT_SERVICE_URI = "http://localhost:10000/services/concerts";
	private static String USER_SERVICE_URI = "http://localhost:10000/services/users";
	private static String PERFORMER_SERVICE_URI = "http://localhost:10000/services/performers";
	
	private static Client _client;
	
	@Override
	public Set<ConcertDTO> getConcerts() throws ServiceException {
		_client = ClientBuilder.newClient();
		Builder builder = _client.target(CONCERT_SERVICE_URI).request();
		Response response = null;
		
		response = builder.get();
		int responseCode = response.getStatus();
		
		List<ConcertDTO> concertList = response.readEntity(new GenericType<List<ConcertDTO>>(){});
		
		Set<ConcertDTO> concertSet = new HashSet<>(concertList);
		return concertSet;
	}

	@Override
	public Set<PerformerDTO> getPerformers() throws ServiceException {
		_client = ClientBuilder.newClient();
		Builder builder = _client.target(PERFORMER_SERVICE_URI).request();
		Response response = null;
		
		response = builder.get();
		int responseCode = response.getStatus();
		List<PerformerDTO> list = response.readEntity(new GenericType<List<PerformerDTO>>(){});
		Set<PerformerDTO> set = new HashSet<>(list);
		return set;
	}

	@Override
	public UserDTO createUser(UserDTO newUser) throws ServiceException {
		_client = ClientBuilder.newClient();
		Builder builder = _client.target(USER_SERVICE_URI).request();
		Response response = null;
		
		response = builder.post(Entity.entity(newUser,MediaType.APPLICATION_XML));
		int responseCode = response.getStatus();
		
		switch (responseCode) {
		case 400: 
		case 500:
			throw new ServiceException(response.readEntity(String.class));
		}
		UserDTO fullUser = (UserDTO) response.getEntity();
		return fullUser;
	}

	@Override
	public UserDTO authenticateUser(UserDTO user) throws ServiceException {
		_client = ClientBuilder.newClient();
		Builder builder = _client.target(USER_SERVICE_URI+"/authenticate").request();
		Response response = null;
		
		response = builder.post(Entity.entity(user,MediaType.APPLICATION_XML));
		int responseCode = response.getStatus();
		
		switch (responseCode) {
		case 400: 
		case 500:
			throw new ServiceException(response.readEntity(String.class));
		}
		UserDTO fullUser = response.readEntity(UserDTO.class);
		return fullUser;
	}

	@Override
	public Image getImageForPerformer(PerformerDTO performer) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReservationDTO reserveSeats(ReservationRequestDTO reservationRequest) throws ServiceException {
		
		Set<SeatDTO> seats = TheatreUtility.findAvailableSeats(reservationRequest.getNumberOfSeats(), reservationRequest.getSeatType(), new HashSet<SeatDTO>());
		
		ReservationDTO reservation = new ReservationDTO(_reservationIdCounter.incrementAndGet(), reservationRequest, seats);
		
		return reservation;
	}

	@Override
	public void confirmReservation(ReservationDTO reservation) throws ServiceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerCreditCard(CreditCardDTO creditCard) throws ServiceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<BookingDTO> getBookings() throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void subscribeForNewsItems(NewsItemListener listener) {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void cancelSubscription() {
		throw new UnsupportedOperationException();
	}

}
