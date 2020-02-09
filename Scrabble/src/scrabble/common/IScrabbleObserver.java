package scrabble.common;

/*
 *  Dean Pakravan: 757389
 *  Qijie Li: 927249 
 *  Luis Zapata: 938907 
 *  Dongming Li: 1002971
 *  Assignment 2: Distributed Systems - Sem2 2018
 */

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Represents an RMI client
 *
 */
public interface IScrabbleObserver extends Remote {
	/**
	 * Send the message to the client with the state of the game updated
	 * @param state
	 * @throws RemoteException
	 */
	public void Update(GameState state) throws RemoteException;
	/**
	 * Check if the client is alive
	 * @return
	 * @throws RemoteException
	 */
	public boolean IsActive() throws RemoteException;;
}
