package scrabble.common;

/*
 *  Dean Pakravan: 757389
 *  Qijie Li: 927249 
 *  Luis Zapata: 938907 
 *  Dongming Li: 1002971
 *  Assignment 2: Distributed Systems - Sem2 2018
 */

/**
 * 
 * It is thrown when the client attempt an operation not allowed
 */
public class OperationNotAllowedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1914220203893616410L;

	public OperationNotAllowedException() {
		super();
	}
	
	public OperationNotAllowedException(String message) {
		super(message);
	}
}
