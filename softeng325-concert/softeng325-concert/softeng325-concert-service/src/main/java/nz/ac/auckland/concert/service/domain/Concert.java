package nz.ac.auckland.concert.service.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
	
	public Concert() {}
	
	public Concert(Long id, String title) {
		_id = id;
		_title = title;
	}
	
	public Long getId() {
		return _id;
	}

	public String getTitle() {
		return _title;
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
            isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31). 
	            append(_title).
	            hashCode();
	}

	@Override
	public int compareTo(Concert concert) {
		return _title.compareTo(concert.getTitle());
	}

}
