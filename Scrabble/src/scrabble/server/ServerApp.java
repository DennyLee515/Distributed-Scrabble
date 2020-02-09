package scrabble.server;

/*
 *  Dean Pakravan: 757389
 *  Qijie Li: 927249 
 *  Luis Zapata: 938907 
 *  Dongming Li: 1002971
 *  Assignment 2: Distributed Systems - Sem2 2018
 */

import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class ServerApp {
	
	private static int sPort = 5555;
	
	/*
	 * Class to test the server part outside of the client
	 */
	public static void main(String[] args) {
		//System.setSecurityManager(new RMISecurityManager());
		Scanner in = new Scanner(System.in);
		
		System.out.println("Server is starting...");
		
		try {
			Server server = new Server(sPort);
			server.start();
			System.out.println("Press Enter to exit.") ;
			
			in.nextLine() ;
			
			//Naming.rebind(Utilities.SERVER_NAME, gc);
			System.out.println("Server is started");
		} catch (RemoteException | UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		in.close(); 
	}
}
