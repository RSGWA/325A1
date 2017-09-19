package nz.ac.auckland.concert.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import nz.ac.auckland.concert.common.Concert;
import nz.ac.auckland.concert.common.ConcertService;

public class ConcertServiceServant extends UnicastRemoteObject implements ConcertService {

	private static final long serialVersionUID = 1L;

	private List<Concert> _concerts;
	
	// Used to give new concert an id
	private Long _concertCounter;
	
	protected ConcertServiceServant() throws RemoteException {
		super();
		_concerts = new ArrayList<Concert>();
		_concertCounter = 0l;
	}

	@Override
	public synchronized Concert createConcert(Concert concert) throws RemoteException {
		
		Concert newConcert = new ConcertServant(_concertCounter, concert.getTitle(), concert.getDate());
		_concerts.add(newConcert);
		
		// Increment counter to increment id given to the next concert created
		_concertCounter++;
		
		return newConcert;
	}

	@Override
	public synchronized Concert getConcert(Long id) throws RemoteException {
		for (Concert concert: _concerts) {
			if (concert.getId().equals(id)) {
				return concert;
			}
		}
		return null;
	}

	@Override
	public synchronized boolean updateConcert(Concert concert) throws RemoteException {
		
		for (Concert concerts: _concerts) {
			if (concerts.equals(concert)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public synchronized boolean deleteConcert(Long id) throws RemoteException {
		
		for (int i = 0; i < _concerts.size(); i++) {
			if (_concerts.get(i).getId().equals(id)) {
				_concerts.remove(i);
				return true;
			}
		}
		
		return false;
	}

	@Override
	public synchronized List<Concert> getAllConcerts() throws RemoteException {
		return _concerts;
	}

	@Override
	public synchronized void clear() throws RemoteException {
		_concertCounter = 0l;
		_concerts.clear();
	}
	
}
