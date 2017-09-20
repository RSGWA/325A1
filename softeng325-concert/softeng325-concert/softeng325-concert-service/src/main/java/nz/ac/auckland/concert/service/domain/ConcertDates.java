package nz.ac.auckland.concert.service.domain;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
@Table(name="CONCERT_DATES")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ConcertDates {

	@Id
	@XmlAttribute(name="id")
	private Long _id;
	
	@XmlElement(name="dates")
	@ElementCollection
	@CollectionTable(
			name = "DATES", 
			joinColumns = @JoinColumn(name = "ID"))
	@Column(name = "DATES")
	private Set<LocalDateTime> _dates;
	
	public ConcertDates() {}
	
	public ConcertDates(Long id, Set<LocalDateTime> dates) {
		_id = id;
		_dates = dates;
	}
	
	public Long getId() {
		return _id;
	}
	
	public Set<LocalDateTime> getDates() {
		return Collections.unmodifiableSet(_dates);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ConcertDates))
            return false;
        if (obj == this)
            return true;

        ConcertDates rhs = (ConcertDates) obj;
        return new EqualsBuilder().
            append(_dates, rhs._dates).
            isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31). 
	            append(_dates).
	            hashCode();
	}
	
}
