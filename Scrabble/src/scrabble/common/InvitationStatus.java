package scrabble.common;

/*
 *  Dean Pakravan: 757389
 *  Qijie Li: 927249 
 *  Luis Zapata: 938907 
 *  Dongming Li: 1002971
 *  Assignment 2: Distributed Systems - Sem2 2018
 */

public enum InvitationStatus {
	JoinedRoom, // The player joined, he is not invited or in the game room
	Invited, // The player was invited
	InvitationRejected, // The invitation was rejected
	InvitationAccepted // The invitation was accepted
}
