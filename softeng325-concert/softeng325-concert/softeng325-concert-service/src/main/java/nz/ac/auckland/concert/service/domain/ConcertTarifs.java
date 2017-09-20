package nz.ac.auckland.concert.service.domain;

import java.math.BigDecimal;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Id;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import nz.ac.auckland.concert.common.types.PriceBand;

@Entity
@Table(name="CONCERT_TARIFS")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ConcertTarifs {

	@Id
	@XmlAttribute(name="id")
	private Long _id;

	@ElementCollection
	@CollectionTable(name="Tarif")
	@MapKeyColumn(name="PRICEBAND")
	@Column(name="PRICE")
	@MapKeyEnumerated(EnumType.STRING)
	private Map<PriceBand, BigDecimal> _tariff;
	
	public ConcertTarifs() {}
	
	public ConcertTarifs(Long id, Map<PriceBand, BigDecimal> tariff) {
		_id = id;
		_tariff = tariff;
	}
}
