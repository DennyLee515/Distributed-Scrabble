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
 * 
 * Holds my current votes
 */
public class Vote implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8653437561865111724L;
	
	public Vote(String playerName, boolean value) {
		this.playerName = playerName;
		this.value = value;
	}

	// Holds the player name
	private String playerName;
	// Holds the vote value
	private boolean value;
	
	public String getPlayerName() {
		return playerName;
	}
	
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	
	public boolean getValue() {
		return value;
	}
	
	public void setValue(boolean value) {
		this.value = value;
	}
}
