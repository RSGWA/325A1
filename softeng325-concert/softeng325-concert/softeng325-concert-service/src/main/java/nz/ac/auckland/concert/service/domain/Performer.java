package nz.ac.auckland.concert.service.domain;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import nz.ac.auckland.concert.common.types.Genre;

@Entity
@Table(name="PERFORMERS")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Performer {

	@Id
	@XmlAttribute(name="id")
	private Long _id;
	
	@XmlElement(name="name")
	private String _name;
	
	@XmlElement(name="imageName")
	private String _imageName;
	
	@Enumerated(EnumType.STRING)
	@XmlElement(name="genre")
	private Genre _genre;
	
	public Performer() {}
	
	public Performer(Long id, String name, String imageName, Genre genre) {
		_id = id;
		_name = name;
		_imageName = imageName;
		_genre = genre;
	}
	
	public Long getId() {
		return _id;
	}
	
	public String getName() {
		return _name;
	}
	
	public String getImageName() {
		return _imageName;
	}
	
	public Genre getGenre() {
		return _genre;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Performer))
            return false;
        if (obj == this)
            return true;

        Performer rhs = (Performer) obj;
        return new EqualsBuilder().
            append(_name, rhs._name).
            append(_imageName, rhs._imageName).
            append(_genre, rhs._genre).
            isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31). 
	            append(_name).
	            append(_imageName).
	            append(_genre).
	            hashCode();
	}
	
}
