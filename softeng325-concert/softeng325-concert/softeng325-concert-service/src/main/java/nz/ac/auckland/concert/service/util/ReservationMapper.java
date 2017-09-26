package nz.ac.auckland.concert.service.util;

import nz.ac.auckland.concert.common.dto.ReservationDTO;
import nz.ac.auckland.concert.common.dto.ReservationRequestDTO;
import nz.ac.auckland.concert.service.domain.Reservation;

/**
* Helper class to convert between domain-model and DTO objects representing
* Reservations
*
*/
public class ReservationMapper {

	static Reservation toDomainModel(ReservationDTO reservation, ReservationRequestDTO request) {
		Reservation domainReservation = new Reservation(reservation.getId(),
				reservation.getSeats(),
				request.getNumberOfSeats(),
				request.getSeatType(),
				request.getConcertId(),
				request.getDate());
		return domainReservation;
	}
	
	static ReservationDTO toDto(Reservation reservation) {
		ReservationRequestDTO request = new ReservationRequestDTO(reservation.getNumberOfSeats(),
				reservation.getSeatType(),
				reservation.getConcertId(),
				reservation.getDate());
		ReservationDTO reservationDTO = new ReservationDTO(reservation.getId(),
				request,
				reservation.getSeats());
		return reservationDTO;
	}
}

