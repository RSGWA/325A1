package nz.ac.auckland.concert.service.domain;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import nz.ac.auckland.concert.common.types.PriceBand;

@Entity
@Table(name="CONCERTS")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Concert implements Comparable<Concert> {
	
	@Id
	@XmlAttribute(name="id")
	private Long _id;
	
	@XmlElement(name="title")
	private String _title;
	
	@XmlElement(name="dates")
	@ElementCollection
	@CollectionTable(
			name = "CONCERT_DATES",
			joinColumns = @JoinColumn( name = "CONCERT_ID" ) )
			@Column( name = "DATE" )
	private Set<LocalDateTime> _dates = new HashSet<LocalDateTime>();
	
	@ElementCollection
	@CollectionTable( name = "CONCERT_TARIFS" )
	@MapKeyColumn( name = "PRICEBAND" )
	@MapKeyEnumerated(EnumType.STRING)
	@Column( name = "COST_PER_TICKET")
	private Map<PriceBand, BigDecimal> _tariff;
	
	@XmlElement(name="performerIds")
	@ElementCollection
	@CollectionTable(
			name = "CONCERT_PERFORMER",
			joinColumns = @JoinColumn( name = "CONCERT_ID" ) )
			@Column( name = "PERFORMER_ID" )
	private Set<Long> _performerIds;
	
	public Concert() {}
	
	public Concert(Long id, String title, Set<LocalDateTime> dates,
			Map<PriceBand, BigDecimal> ticketPrices, Set<Long> performerIds) {
		_id = id;
		_title = title;
		_dates = new HashSet<LocalDateTime>(dates);
		_tariff = new HashMap<PriceBand, BigDecimal>(ticketPrices);
		_performerIds = new HashSet<Long>(performerIds);
	}
	
	public Long getId() {
		return _id;
	}

	public String getTitle() {
		return _title;
	}

	public Set<LocalDateTime> getDates() {
		return Collections.unmodifiableSet(_dates);
	}

	public BigDecimal getTicketPrice(PriceBand seatType) {
		return _tariff.get(seatType);
	}

	public Set<Long> getPerformerIds() {
		return Collections.unmodifiableSet(_performerIds);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Concert))
            return false;
        if (obj == this)
            return true;

        Concert rhs = (Concert) obj;
        return new EqualsBuilder().
            append(_title, rhs._title).
            append(_dates, rhs._dates).
            append(_tariff, rhs._tariff).
            append(_performerIds, rhs._performerIds).
            isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31). 
	            append(_title).
	            append(_dates).
	            append(_tariff).
	            append(_performerIds).
	            hashCode();
	}

	@Override
	public int compareTo(Concert concert) {
		return _title.compareTo(concert.getTitle());
	}

}
