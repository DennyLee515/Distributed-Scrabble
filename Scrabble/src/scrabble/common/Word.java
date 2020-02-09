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
 * Holds a state and location of a word that is claimed for voting
 */
public class Word implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 641933976314468180L;

	// Holds the content of the word
	private String content;
	// Holds the start row
	private int startRow;
	// Holds the start column
	private int startColumn;
	// Holds the end row
	private int endRow;
	// Holds the end Column
	private int endColumn;
	// Holds what is the direction of the word
	private ClaimForWord direction;
	
	public Word(String content, int startRow, int startColumn, int endRow, int endColumn, ClaimForWord direction) {
		this.content = content;
		this.startRow = startRow;
		this.startColumn = startColumn;
		this.endRow = endRow;
		this.endColumn = endColumn;
		this.direction = direction;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getStartRow() {
		return startRow;
	}
	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}
	public int getStartColumn() {
		return startColumn;
	}
	public void setStartColumn(int startColumn) {
		this.startColumn = startColumn;
	}
	public int getEndRow() {
		return endRow;
	}
	public void setEndRow(int endRow) {
		this.endRow = endRow;
	}
	public int getEndColumn() {
		return endColumn;
	}
	public void setEndColumn(int endColumn) {
		this.endColumn = endColumn;
	}
	public ClaimForWord getDirection() {
		return direction;
	}
	public void setDirection(ClaimForWord direction) {
		this.direction = direction;
	}
}
