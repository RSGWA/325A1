package nz.ac.auckland.concert.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.joda.time.DateTime;

import nz.ac.auckland.concert.common.Concert;

public class ConcertServant extends UnicastRemoteObject implements Concert {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long _id;
	private String _title;
	private DateTime _date;
	
	public ConcertServant(String title, DateTime date) throws RemoteException {
		super();
		_title = title;
		_date = date;
	}
	
	public ConcertServant(Long id, String title, DateTime date) throws RemoteException {
		super();
		_id = id;
		_title = title;
		_date = date;
	}

	@Override
	public Long getId() throws RemoteException {
		return _id;
	}

	@Override
	public String getTitle() throws RemoteException {
		return _title;
	}

	@Override
	public DateTime getDate() throws RemoteException {
		return _date;
	}
	
	@Override
	public boolean equals(Concert other) throws RemoteException {
		if (!(other instanceof Concert)) return false;
		if (other == this) return true;
		
		if (this.getId().equals(other.getId())) {
			if (this.getTitle().equals(other.getTitle())) {
				return true;
			}
		}
		
		return false;
	}
	
}
