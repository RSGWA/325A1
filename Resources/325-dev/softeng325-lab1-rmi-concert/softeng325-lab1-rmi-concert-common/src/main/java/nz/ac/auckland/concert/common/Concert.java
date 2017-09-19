package nz.ac.auckland.concert.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.joda.time.DateTime;

public interface Concert extends Remote {
	
	Long getId() throws RemoteException;
	String getTitle() throws RemoteException;
	DateTime getDate() throws RemoteException;
	boolean equals(Concert other) throws RemoteException;
	

}
