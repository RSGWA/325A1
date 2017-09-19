package nz.ac.auckland.concert.services;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/services")
public class PersistenceApplication extends Application {

	private Set<Object> singletons = new HashSet<Object>();
	
	private Set<Class<?>> classes = new HashSet<Class<?>>();
	
	public PersistenceApplication() {
		singletons.add(new PersistenceManager());
		classes.add(Resource.class);
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
