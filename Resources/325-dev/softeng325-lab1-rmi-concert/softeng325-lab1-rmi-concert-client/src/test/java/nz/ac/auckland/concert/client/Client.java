package nz.ac.auckland.concert.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nz.ac.auckland.concert.common.Concert;
import nz.ac.auckland.concert.common.ConcertService;
import nz.ac.auckland.concert.common.Config;
import nz.ac.auckland.concert.server.ConcertServant;

public class Client {
	
	private static ConcertService _proxy;

	/**
	 * One-time setup method to retrieve the ConcertService proxy from the RMI 
	 * Registry.
	 */
	@BeforeClass
	public static void getProxy() {
		try {
			// Instantiate a proxy object for the RMI Registry, expected to be
			// running on the local machine and on a specified port. 
			Registry lookupService = LocateRegistry.getRegistry("localhost", Config.REGISTRY_PORT);
			
			// Retrieve a proxy object representing the ShapeFactory.
			_proxy = (ConcertService)lookupService.lookup(Config.SERVICE_NAME);
			
		} catch (RemoteException e) {
			System.out.println("Unable to connect to the RMI Registry");
		} catch (NotBoundException e) {
			System.out.println("Unable to acquire a proxy for the Concert service");
		}
	}
	
	@Before
	public void init() throws RemoteException {
		_proxy.clear();
	}
	
	@Test
	public void testCreate() throws RemoteException {
		
		Concert uzi = new ConcertServant("XOLif3TouR", new DateTime(0));
		Concert savage = new ConcertServant("SlaughterGang", new DateTime(0));
		Concert concertA = _proxy.createConcert(uzi);
		Concert concertB = _proxy.createConcert(savage);
		
		// Query the remote factory.
		List<Concert> remoteConcerts = _proxy.getAllConcerts();
		
		assertTrue(remoteConcerts.contains(concertA));
		assertTrue(remoteConcerts.contains(concertB));
		assertEquals(2, remoteConcerts.size());
		
	}
	
	@Test
	public void testGetConcert() throws RemoteException {
		Concert migosConcert = new ConcertServant("MIGOS",new DateTime(0));
		_proxy.createConcert(migosConcert);
		Concert concertB = _proxy.getConcert(0l);
		
		if (concertB == null) {
			fail();
		}
		assertEquals(concertB.getTitle(), "MIGOS");
	}
	
	@Test
	public void testUpdateConcert() throws RemoteException {
		Concert uzi = new ConcertServant(0l,"XOLif3TouR", new DateTime(0));
		_proxy.createConcert(uzi);
		if (!_proxy.updateConcert(uzi)) {
			fail();
		}
	}
	
	@Test
	public void testDeleteConcert() throws RemoteException {
		Concert uzi = new ConcertServant("XOLif3TouR", new DateTime(0));
		Concert savage = new ConcertServant("SlaughterGang", new DateTime(0));
		_proxy.createConcert(uzi);
		_proxy.createConcert(savage);
		
		if (!_proxy.deleteConcert(0l)) {
			fail();
		}
		
	}
	
	@Test
	public void testGetAllConcerts() throws RemoteException {
		Concert uzi = new ConcertServant("XOLif3TouR", new DateTime(0));
		Concert savage = new ConcertServant("SlaughterGang", new DateTime(0));
		_proxy.createConcert(uzi);
		_proxy.createConcert(savage);
		Concert migosConcert = new ConcertServant("MIGOS",new DateTime(0));
		_proxy.createConcert(migosConcert);
		
		List<Concert> remoteConcerts = _proxy.getAllConcerts();
		
		assertEquals(3, remoteConcerts.size());
		
	}
	@Test
	public void clearConcerts() throws RemoteException {
		Concert uzi = new ConcertServant("XOLif3TouR", new DateTime(0));
		Concert savage = new ConcertServant("SlaughterGang", new DateTime(0));
		_proxy.createConcert(uzi);
		_proxy.createConcert(savage);
		Concert migosConcert = new ConcertServant("MIGOS",new DateTime(0));
		_proxy.createConcert(migosConcert);
		
		_proxy.clear();
		
		List<Concert> remoteConcerts = _proxy.getAllConcerts();
		assertEquals(0, remoteConcerts.size());
	}
}
