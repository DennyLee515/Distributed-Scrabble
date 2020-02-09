package scrabble.common;

/*
 *  Dean Pakravan: 757389
 *  Qijie Li: 927249 
 *  Luis Zapata: 938907 
 *  Dongming Li: 1002971
 *  Assignment 2: Distributed Systems - Sem2 2018
 */

/**
 * Tells if the client wants to ask for a word to be voted
 */
public enum ClaimForWord {
	NoClaim, // No voting needed
	Up, // The word goes from button to top
	Down, // The words goes from top to button
	Left, // The word goes from right to left
	Right // The word goes from left to right
}
