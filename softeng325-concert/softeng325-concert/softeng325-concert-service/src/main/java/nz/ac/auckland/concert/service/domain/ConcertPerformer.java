package nz.ac.auckland.concert.service.domain;

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
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name="CONCERT_PERFORMER")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ConcertPerformer {

	@Id
	@XmlAttribute(name="id")
	private Long _id; // ID of Concert
	
	@XmlElementWrapper(name="performerIds")
	@XmlElement(name="id")
	@ElementCollection
	@CollectionTable(
			name = "PERFORMERS", 
			joinColumns = @JoinColumn(name = "CONCERT_ID"))
	@Column(name = "PERFORMER_ID")
	private Set<Long> _performerIds;
	
	public ConcertPerformer() {}
	
	public ConcertPerformer(Long id, Set<Long> performerIds) {
		_id = id;
		_performerIds = performerIds;
	}
}
