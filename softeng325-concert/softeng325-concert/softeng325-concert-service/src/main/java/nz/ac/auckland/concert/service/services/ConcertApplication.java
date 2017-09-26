package nz.ac.auckland.concert.service.services;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * JAX-RS Application subclass for the Concert Web service.
 * 
 * 
 *
 */
@ApplicationPath("/services")
public class ConcertApplication extends Application {

	// This property should be used by your Resource class. It represents the 
	// period of time, in seconds, that reservations are held for. If a
	// reservation isn't confirmed within this period, the reserved seats are
	// returned to the pool of seats available for booking.
	//
	// This property is used by class ConcertServiceTest.
	public static final int RESERVATION_EXPIRY_TIME_IN_SECONDS = 5;
	
	private Set<Object> singletons = new HashSet<Object>();
	
	private Set<Class<?>> classes = new HashSet<Class<?>>();
	
	// Constructor called by JAX-RS.
	public ConcertApplication () {
		singletons.add(new PersistenceManager());
		classes.add(ConcertResource.class);

		EntityManager em = null;
		try {
			em = PersistenceManager.instance().createEntityManager();
			/*
			em.getTransaction().begin();
		
			// Delete all existing entities of some type, e.g. MyEntity.
			em.createQuery("delete from Concert").executeUpdate();
		
			// Make many entities of some type.
			for () {
				for () {
					MyEntity e = new Concert();
					em. persist(e);
				}
				// Periodically flush and clear the persistence context.
				em.flush ();
				em.clear ();
			}
			em.getTransaction (). commit();
			*/
		
		} catch(Exception e) {
			// Process and log the exception .
		} finally {
			if(em != null && em.isOpen()) {
				em.close ();
			}
		}
	}
	
	@Override
	public Set<Object> getSingletons()
	{
		return singletons;
	}
	
	@Override
	public Set<Class<?>> getClasses() {
		return classes;
	}
}
