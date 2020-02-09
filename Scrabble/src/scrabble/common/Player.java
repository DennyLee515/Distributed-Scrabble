package scrabble.common;

/*
 *  Dean Pakravan: 757389
 *  Qijie Li: 927249 
 *  Luis Zapata: 938907 
 *  Dongming Li: 1002971
 *  Assignment 2: Distributed Systems - Sem2 2018
 */

import java.io.Serializable;

/**
 * Holds the player current status in an object could be serializable for the game status
 */
public class Player implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5846937466954147304L;
	
	// Holds the player name
	String name;
	// Holds the player score
	int score = 0;
	// Holds the player invitation status
	private InvitationStatus invitationStatus = InvitationStatus.JoinedRoom;
	
	
	public Player(String name, int score, InvitationStatus invitationStatus) {
		this.name = name;
		this.score = score;
		this.invitationStatus = invitationStatus;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getScore() {
		return score;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	public InvitationStatus getInvitationStatus() {
		return invitationStatus;
	}

	public void setInvitationStatus(InvitationStatus invitationStatus) {
		this.invitationStatus = invitationStatus;
	}
}
