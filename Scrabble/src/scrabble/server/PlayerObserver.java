package scrabble.server;

/*
 *  Dean Pakravan: 757389
 *  Qijie Li: 927249 
 *  Luis Zapata: 938907 
 *  Dongming Li: 1002971
 *  Assignment 2: Distributed Systems - Sem2 2018
 */

import scrabble.common.IScrabbleObserver;
import scrabble.common.InvitationStatus;

/**
 * This class holds the current state of a player in the game with its client reference for RMI
 * 
 */
public class PlayerObserver {
	// Holds the player id
	private String id;
	// Holds the player name
	private String name;
	// Holds if the player has been invited
	private InvitationStatus invitationStatus = InvitationStatus.JoinedRoom;
	// Holds the player score
	private int score = 0;
	// Holds the player vote
	private Boolean vote = null;
	// Holds the reference of the client for RMI
	private IScrabbleObserver observer;
	// Holds if the player is active or has disconnected
	private boolean active = true;
	
	/*
	 * Builds a new player observer
	 */
	public PlayerObserver(String id, String name, IScrabbleObserver observer) {
		this.id = id;
		this.name = name;
		this.observer = observer;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public IScrabbleObserver getObserver() {
		return observer;
	}
	
	public void setObserver(IScrabbleObserver observer) {
		this.observer = observer;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	public void addToScore(int pointsToAdd) {
		score += pointsToAdd;
	}

	public Boolean getVote() {
		return vote;
	}

	public void setVote(Boolean vote) {
		this.vote = vote;
	}
	
	public InvitationStatus getInvitationStatus() {
		return invitationStatus;
	}

	public void setInvitationStatus(InvitationStatus invitationStatus) {
		this.invitationStatus = invitationStatus;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
}
