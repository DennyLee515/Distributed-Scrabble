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
 * 
 * The interface for the RMI server class
 */
public interface IScrabbleServer extends Remote {
	/**
	 * Allows a player to join the game to the game room giving its player name
	 */
	public String JoinGame(String playerName, IScrabbleObserver newClient) throws RemoteException, OperationNotAllowedException;
	/**
	 * Used after players join the game, allow other players to be invited
	 */
	public void InvitePlayers(String clientId, String[] playerNames) throws RemoteException, OperationNotAllowedException;
	/**
	 * A player who was invited can accept the invitation
	 */
	public void AcceptInvitation(String clientId, boolean accepted) throws RemoteException, OperationNotAllowedException;
	/**
	 * For a user that is joined, it allows to leave the game room
	 */
	public void LeaveGameRoom(String clientId) throws RemoteException, OperationNotAllowedException;
	/**
	 * Allows a player to start the game
	 */
	public void StartGame(String clientId) throws RemoteException, OperationNotAllowedException;
	/**
	 * Allows a player to place a letter in the board
	 */
	public void PlaceLetter(String clientId, Movement movement, ClaimForWord claimForWord) throws RemoteException, OperationNotAllowedException;
	/**
	 *  Allows players to vote for a word
	 */
	public void VoteForWord(String clientId, boolean rightWord) throws RemoteException, OperationNotAllowedException;
	/**
	 * Allows a client to pass the turn
	 */
	public void PassTurn(String clientId) throws RemoteException, OperationNotAllowedException;
	/**
	 * Allows a player to end the game
	 */
	public void EndGame(String clientId) throws RemoteException, OperationNotAllowedException;
}
