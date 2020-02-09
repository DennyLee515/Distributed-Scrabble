package scrabble.server;

/*
 *  Dean Pakravan: 757389
 *  Qijie Li: 927249 
 *  Luis Zapata: 938907 
 *  Dongming Li: 1002971
 *  Assignment 2: Distributed Systems - Sem2 2018
 */

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.AccessException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import scrabble.common.Utilities;

/**
 * Start the RMI server
 *
 */
public class Server {
	
	private int port = 0;
	
	// Build a server with the specified port
	public Server(int port) {
		this.port = port;
	}

	// Start the server
	public void start() throws RemoteException, AccessException, UnknownHostException {
		// Creates the game controller handler
		GameController gc = new GameController();
		
		// Creates a new local registry
		Registry registry = LocateRegistry.createRegistry(port);
		// Bind the game controller to the server
		registry.rebind(Utilities.SERVER_NAME, gc);

		String ip = InetAddress.getLocalHost().getHostAddress() ;
		
		System.out.println("Game model bound to rmi://" +ip+ ":" +port+ "/" + Utilities.SERVER_NAME);
		
		// Unregister clients when the server ends
		Thread shutDownHook = new Thread() {
			@Override public void run() {
				try { 
					Naming.unbind(Utilities.SERVER_NAME) ;
					System.out.println("Model is unbound");}
				catch( Throwable e ) {} 
			}
		};
		Runtime.getRuntime().addShutdownHook(shutDownHook) ;
	}
}
