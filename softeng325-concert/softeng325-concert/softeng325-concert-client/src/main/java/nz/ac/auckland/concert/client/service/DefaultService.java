package nz.ac.auckland.concert.client.service;

import java.awt.Image;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.ws.rs.core.GenericEntity;

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

	private AtomicLong _reservationIdCounter = new AtomicLong();
	private ConcertResource _resource = new ConcertResource();
	
	@Override
	public Set<ConcertDTO> getConcerts() throws ServiceException {
		List<ConcertDTO> concertList = (List<ConcertDTO>) _resource.retrieveAllConcerts().getEntity();
		Set<ConcertDTO> concertSet = new HashSet<>(concertList);
		return concertSet;
	}

	@Override
	public Set<PerformerDTO> getPerformers() throws ServiceException {
		List<PerformerDTO> list = (List<PerformerDTO>) _resource.retrieveAllPerformers().getEntity();
		Set<PerformerDTO> set = new HashSet<>(list);
		return set;
	}

	@Override
	public UserDTO createUser(UserDTO newUser) throws ServiceException {
		_resource.createUser(newUser);
		return null;
	}

	@Override
	public UserDTO authenticateUser(UserDTO user) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
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
