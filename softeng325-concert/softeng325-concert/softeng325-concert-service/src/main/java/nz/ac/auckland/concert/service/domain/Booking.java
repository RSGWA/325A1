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
@Table(name="BOOKINGS")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Booking {
	
	@Id
	@XmlAttribute(name="id")
	private Long _concertId;
	
	@XmlElement(name="concertTitle")
	private String _concertTitle;
	
	@XmlElement(name="dateTime")
	private LocalDateTime _dateTime;
	
	@ElementCollection
	@CollectionTable(name="BOOKED_SEATS", joinColumns=@JoinColumn(name="CONCERT_ID"))
	@XmlElement(name="seats")
	private Set<SeatDTO> _seats;
	
	@Enumerated
	@XmlElement(name="priceBand")
	private PriceBand _priceBand;

	public Booking() {}

	public Booking(Long concertId, String concertTitle,
			LocalDateTime dateTime, Set<SeatDTO> seats, PriceBand priceBand) {
		_concertId = concertId;
		_concertTitle = concertTitle;
		_dateTime = dateTime;
		_seats = new HashSet<SeatDTO>();
		_seats.addAll(seats);

		_priceBand = priceBand;
	}

	public Long getConcertId() {
		return _concertId;
	}

	public String getConcertTitle() {
		return _concertTitle;
	}

	public LocalDateTime getDateTime() {
		return _dateTime;
	}

	public Set<SeatDTO> getSeats() {
		return Collections.unmodifiableSet(_seats);
	}

	public PriceBand getPriceBand() {
		return _priceBand;
	}
}
