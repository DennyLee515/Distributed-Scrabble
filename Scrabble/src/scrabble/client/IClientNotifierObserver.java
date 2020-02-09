package scrabble.client;

/*
 *  Dean Pakravan: 757389
 *  Qijie Li: 927249 
 *  Luis Zapata: 938907 
 *  Dongming Li: 1002971
 *  Assignment 2: Distributed Systems - Sem2 2018
 */

import scrabble.common.GameState;

public interface IClientNotifierObserver {
	public void update(GameState state);
}
