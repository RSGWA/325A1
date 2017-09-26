package nz.ac.auckland.concert.service.domain;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import nz.ac.auckland.concert.common.dto.SeatDTO;
import nz.ac.auckland.concert.common.types.PriceBand;

@Entity
@Table(name="RESERVATIONS")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Reservation {
	
	@Id
	@XmlAttribute(name="id")
	private Long _id;
	
	@ElementCollection
	@CollectionTable(name="RESERVED_SEATS", joinColumns=@JoinColumn(name="RESERVATION_ID"))
	@XmlElement(name="seats")
	private Set<SeatDTO> _seats;
	
	@XmlElement(name="numberOfSeats")
	private int _numberOfSeats;
	
	@Enumerated
	@XmlElement(name="seatType")
	private PriceBand _seatType;
	
	@XmlElement(name="concertId")
	private Long _concertId;
	
	@XmlElement(name="date")
	private LocalDateTime _date;
	
	public Reservation() {}
	
	public Reservation(Long id, Set<SeatDTO> seats, int numberOfSeats, 
			PriceBand seatType, Long concertId, LocalDateTime date) {
		_id = id;
		_seats = new HashSet<SeatDTO>(seats);
		_numberOfSeats = numberOfSeats;
		_seatType = seatType;
		_concertId = concertId;
		_date = date;
	}
	
	public Long getId() {
		return _id;
	}
	
	public Set<SeatDTO> getSeats() {
		return Collections.unmodifiableSet(_seats);
	}

	public int getNumberOfSeats() {
		return _numberOfSeats;
	}
	
	public PriceBand getSeatType() {
		return _seatType;
	}
	
	public Long getConcertId() {
		return _concertId;
	}
	
	public LocalDateTime getDate() {
		return _date;
	}
}
