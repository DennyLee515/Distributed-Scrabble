package scrabble.client;

/*
 *  Dean Pakravan: 757389
 *  Qijie Li: 927249 
 *  Luis Zapata: 938907 
 *  Dongming Li: 1002971
 *  Assignment 2: Distributed Systems - Sem2 2018
 */

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;

import scrabble.common.GameState;
import scrabble.common.IScrabbleObserver;

// This class is the remote object implements the IScrabbleObserver interface
public class ClientNotifier extends UnicastRemoteObject implements IScrabbleObserver {

	private static final long serialVersionUID = 6202300743083130136L;
	
	// Value to check if we reading the most updated version.
	private int lastCount = 0;

	private LinkedList<IClientNotifierObserver> observers = new LinkedList<IClientNotifierObserver>();
	
	protected ClientNotifier() throws RemoteException {
		super();
	}

	/**
	 * Updates the observers if it is a next movement
	 */
	@Override
	public synchronized void Update(GameState state) throws RemoteException {
		if (lastCount < state.getMovementCount()) {
			lastCount = state.getMovementCount();
			
			// Commit the update method for every client
			observers.forEach(x -> x.update(state));
		}
	}

	/**
	 * add an observer
	 * @param observer
	 */
	public void addObserver(IClientNotifierObserver observer) {
		observers.add(observer);
	}

	/**
	 * function for remove a observer
	 * @param observer
	 */
	public void removeObserver(IClientNotifierObserver observer) {
		if (observers.contains(observer)) {
			observers.remove(observer);
		}
	}

	/**
	 * verify if a observer is active
	 * @return
	 * @throws RemoteException
	 */
	@Override
	public boolean IsActive() throws RemoteException {
		return true;
	}

}
