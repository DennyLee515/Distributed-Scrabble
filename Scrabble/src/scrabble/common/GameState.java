package scrabble.common;

/*
 *  Dean Pakravan: 757389
 *  Qijie Li: 927249 
 *  Luis Zapata: 938907 
 *  Dongming Li: 1002971
 *  Assignment 2: Distributed Systems - Sem2 2018
 */

import java.io.Serializable;
import java.util.List;

public class GameState implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1709514568776790255L;
	
	// The maximum rows of the board
	public static final int MAXROWS = 20;
	// The maximum columns of the board
	public static final int MAXCOLS = 20;
	// The empty square mark
	public static final char EMPTYSQUARE = '\u0000';
	
	// Holds the current state of the game
	private State state;
	// Holds the last player action
	private LastAction lastAction;
	// Holds the player list
	private List<Player> players;
	// Holds the current player name
	private String currentPlayerName;
	// Holds the previous player name
	private String previousPlayerName;
	// Holds the board with the letters
	private char[][] board = new char [MAXROWS][MAXCOLS];
	// Holds the count of the actions
	private int movementCount;
	// Holds the last movement of the last player who placed a letter
	private Movement lastMovement;
	// Holds the list of the winner
	private String[] winnerNames;
	// Holds the voting state
	private VotingState votingState;
	
	public enum State {
		NoGame, // No game is in progress
		Joining, // The players are joining
		Playing, // The players are playing (a user has to place a letter)
		Voting, // The players are voting
		Ended // The game ended
	}
	
	public enum LastAction {
		GameCreated, // The game was created
		UserJoined, // A user joined
		
		UserInvited, // Users were invited
		InvitationAccepted, // An invitation was accepted
		InvitationRejected, // An invitation was rejected
		UserLeft, // A user left
		ServerLeft, // A server left
		
		GameStarted, // The game has started
		PlayerPlayed, // The player has played
		PlayerPassed, // The player has passed
		PlayerVoted, // The player has voted
	}
		
	public State getState() {
		return state;
	}
	
	public LastAction getLastAction() {
		return lastAction;
	}
	
	public void setLastAction(LastAction lastAction) {
		this.lastAction = lastAction;
	}
	
	public void setState(State state) {
		this.state = state;
	}
	
	public List<Player> getPlayers() {
		return players;
	}
	
	public void setPlayers(List<Player> players) {
		this.players = players;
	}
	
	public char[][] getBoard() {
		return board;
	}
	
	public int getMovementCount() {
		return movementCount;
	}
	
	public void setMovementCount(int movementCount) {
		this.movementCount = movementCount;
	}

	public Movement getLastMovement() {
		return lastMovement;
	}

	public void setLastMovement(Movement lastMovement) {
		this.lastMovement = lastMovement;
	}

	public String getCurrentPlayerName() {
		return currentPlayerName;
	}

	public void setCurrentPlayerName(String currentPlayerName) {
		this.currentPlayerName = currentPlayerName;
	}

	public String getPreviousPlayerName() {
		return previousPlayerName;
	}

	public void setPreviousPlayerName(String previousPlayerName) {
		this.previousPlayerName = previousPlayerName;
	}

	public String[] getWinnerNames() {
		return winnerNames;
	}

	public void setWinnerNames(String[] winnerNames) {
		this.winnerNames = winnerNames;
	}

	public VotingState getVotingState() {
		return votingState;
	}

	public void setVotingState(VotingState voting) {
		this.votingState = voting;
	}
}
