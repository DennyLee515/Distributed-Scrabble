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
 * Represents a client movement
 *
 */
public class Movement implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1198111832512965835L;
	
	public Movement(int row, int col, char letter) {
		this.row = row;
		this.col = col;
		this.letter = letter;
	}
	
	// Holds in which row the client wants to place the letter
	private int row;
	// Holds in which column the client wants to place the letter
	private int col;
	// Holds in which letter the client wants to place
	private char letter;
	
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	
	public char getLetter() {
		return letter;
	}
	public void setLetter(char letter) {
		this.letter = letter;
	}
	
	public int getCol() {
		return col;
	}
	public void setColumn(int col) {
		this.col = col;
	}
}
