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
 * Holds the state of the voting
 *
 */
public class VotingState implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8361812560929649374L;
	
	public VotingState(String startedVotingPlayerName, Word wordForVoting) {
		this.startedVotingPlayerName = startedVotingPlayerName;
		this.wordForVoting = wordForVoting;
		this.votingResult = false;
		this.votes = null;
	}
	
	// Holds which player started voting
	private String startedVotingPlayerName;
	// Holds which is the word that is being voted for
	private Word wordForVoting;
	// When voting is finished, hold the voting result
	private boolean votingResult;
	// Hold a list of the players who have voted
	private Vote[] votes;
	
	public Word getWordForVoting() {
		return wordForVoting;
	}
	public void setWordForVoting(Word wordForVoting) {
		this.wordForVoting = wordForVoting;
	}
	public boolean getVotingResult() {
		return votingResult;
	}
	public void setVotingResult(boolean votingResult) {
		this.votingResult = votingResult;
	}
	public Vote[] getVotes() {
		return votes;
	}
	public void setVotes(Vote[] votes) {
		this.votes = votes;
	}
	public String getStartedVotingPlayerName() {
		return startedVotingPlayerName;
	}
	public void setStartedVotingPlayerName(String startedVotingPlayerName) {
		this.startedVotingPlayerName = startedVotingPlayerName;
	}
}
