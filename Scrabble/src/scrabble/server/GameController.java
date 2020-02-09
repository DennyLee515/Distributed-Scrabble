package scrabble.server;

/*
 *  Dean Pakravan: 757389
 *  Qijie Li: 927249 
 *  Luis Zapata: 938907 
 *  Dongming Li: 1002971
 *  Assignment 2: Distributed Systems - Sem2 2018
 */

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import scrabble.common.ClaimForWord;
import scrabble.common.GameState;
import scrabble.common.GameState.LastAction;
import scrabble.common.IScrabbleObserver;
import scrabble.common.IScrabbleServer;
import scrabble.common.InvitationStatus;
import scrabble.common.Movement;
import scrabble.common.OperationNotAllowedException;
import scrabble.common.Player;
import scrabble.common.Vote;
import scrabble.common.VotingState;
import scrabble.common.Word;

/**
 * Controls the game state in the server
 */
public class GameController extends UnicastRemoteObject implements IScrabbleServer {

	private static final long serialVersionUID = 4209728324065218640L;
	
	/**
	 * Initialize a game controller
	 */
	protected GameController() throws RemoteException {
		super();
		getState().setState(GameState.State.Joining);
		getState().setLastAction(GameState.LastAction.GameCreated);
	}

	
	/**
	 * Initiates a thread pool to update the clients
	 */
	private ExecutorService executorService = Executors.newFixedThreadPool(10);

	/**
	 * The following variables hold the state of the game
	 */
	
	// Holds the game state object
	private GameState _state = new GameState();
	// Holds the collection of players in a list
	private List<PlayerObserver> gameplayers = Collections.synchronizedList(new LinkedList<PlayerObserver>());
	// Holds an iterator that serves to identify the next player
	private Iterator<PlayerObserver> playersIterator = null;
	// Holds the current player
	private PlayerObserver currentPlayer = null;
	// Holds the previous player
	private PlayerObserver previousPlayer = null;
	// Holds the number of players that passed their turn
	private int passCount = 0;
	// Holds who is the server player
	private PlayerObserver serverPlayer = null;

	/**
	 * Allows a player to join the game to the game room giving its player name
	 */
	public String JoinGame(String playerName, IScrabbleObserver observer)
			throws RemoteException, OperationNotAllowedException {
		synchronized (this) {
			// Only if the game is in the joining status
			if (getState().getState() == GameState.State.Joining) {
				// If there is not an existing player with that name
				if (findPlayerByName(playerName) == null) {
					// Assign a random client ID
					String clientId = java.util.UUID.randomUUID().toString();
					// Add the player to the list of players
					PlayerObserver player = new PlayerObserver(clientId, playerName, observer);
					// If it is the first player add it to the list of the players that joined the game
					if (gameplayers.size() == 0) {
						player.setInvitationStatus(InvitationStatus.InvitationAccepted);
						serverPlayer = player;
					}
					gameplayers.add(player);

					getState().setLastAction(GameState.LastAction.UserJoined);

					// Update all clients
					UpdateClients();
					return clientId;
				} else {
					throw new OperationNotAllowedException(
							"The provided username is already in use, try to use another one");
				}
			} else {
				throw new OperationNotAllowedException(
						"The game has been already started in this server. Wait until another game is available for joining or start a new game");
			}
		}
	}
	
	/**
	 * Used after players join the game, allow other players to be invited
	 */
	public void InvitePlayers(String clientId, String[] playerNames) throws RemoteException, OperationNotAllowedException {
		synchronized (this) {
			// Invitations are accepted only when the game has not started
			if (getState().getState() == GameState.State.Joining) {
				PlayerObserver p = findPlayer(clientId);
				// If the client is a member of the game
				if (p != null) {
					// Invite all players in the list
					for (String playerName : playerNames) {
						getState().setLastAction(GameState.LastAction.UserInvited);
						
						PlayerObserver po = findPlayerByName(playerName);
						if (po != null) {
							po.setInvitationStatus(InvitationStatus.Invited);
						}
					}
					
					// Update all clients
					UpdateClients();
				} else {
					throw new OperationNotAllowedException("You have to be a game member to invite players");
				}
			} else {
				throw new OperationNotAllowedException("You cannot invite if the game already started");
			}
		}
	}
	
	/**
	 * A player who was invited can accept the invitation
	 */
	public void AcceptInvitation(String clientId, boolean accepted) throws RemoteException, OperationNotAllowedException {
		synchronized (this) {
			// Accept only if the game has not started
			if (getState().getState() == GameState.State.Joining) {
				PlayerObserver p = findPlayer(clientId);
				// If the client is a member of the game
				if (p != null) {
					//If the player accepted the invitation
					if (accepted) {
						getState().setLastAction(GameState.LastAction.InvitationAccepted);
						p.setInvitationStatus(InvitationStatus.InvitationAccepted);
					} else {
						getState().setLastAction(GameState.LastAction.InvitationRejected);
						p.setInvitationStatus(InvitationStatus.InvitationRejected);
					}
					//Update all clients
					UpdateClients();
				} else {
					throw new OperationNotAllowedException("You cannot accept or reject an invitation if you have not joined");
				}
			} else {
				throw new OperationNotAllowedException("You cannot accept or reject an invitation if the game already started");
			}
		}
	}
	
	/**
	 * For a user that is joined, it allows to leave the game room
	 */
	public void LeaveGameRoom(String clientId) throws RemoteException, OperationNotAllowedException {
		synchronized (this) {
			// Accept only if the game has not started
			if (getState().getState() == GameState.State.Joining) {
				PlayerObserver p = findPlayer(clientId);
				// If the client is a member of the game
				if (p != null) {
					//Remove the player from the game
					gameplayers.remove(p);

					if (p == serverPlayer) {
						getState().setLastAction(GameState.LastAction.ServerLeft);
					} else {
						getState().setLastAction(GameState.LastAction.UserLeft);
					}

					//Update all clients
					UpdateClients();
				} else {
					throw new OperationNotAllowedException(
							"You cannot leave a game you were not registered");
				}
			} else {
				throw new OperationNotAllowedException(
						"The game has already started!");
			}
		}
	}

	/**
	 * Allows a player to start the game
	 */
	public void StartGame(String clientId) throws RemoteException, OperationNotAllowedException {
		synchronized (this) {
			// If there are players registered
			if (getState().getPlayers().size() >= 0) {
				// Accept only if the game has not started
				if (getState().getState() == GameState.State.Joining) {
					// If the client is a member of the game
					if (findPlayer(clientId) != null) {
						passCount = 0;
						// Removes from the observer list all the players that has not accepted
						RemoveNotAcceptedPlayers();

						// Sets the players iterator to the player who started the game
						ResetIteratorTo(clientId);

						getState().setState(GameState.State.Playing);
						getState().setLastAction(GameState.LastAction.GameStarted);

						// Update all clients
						UpdateClients();
						
					} else {
						throw new OperationNotAllowedException("You cannot start a game if you are not a player");
					}
				} else {
					throw new OperationNotAllowedException("You cannot start an already started game");
				}
			} else {
				throw new OperationNotAllowedException("Cannot initiate a game without players");
			}
		}
	}

	/**
	 * Removes all the players who has not accepted the invitation
	 */
	private void RemoveNotAcceptedPlayers() {
		gameplayers = gameplayers.stream()
				.filter(p -> p.getInvitationStatus() == InvitationStatus.InvitationAccepted)
				.collect(Collectors.toList());
	}

	/**
	 * Allows a player to place a letter in the board
	 */
	public void PlaceLetter(String clientId, Movement movement, ClaimForWord claimForWord)
			throws RemoteException, OperationNotAllowedException {
		synchronized (this) {
			// If the movement is valid
			if (movement.getRow() >= 0 && movement.getRow() < GameState.MAXROWS && movement.getCol() >= 0
					&& movement.getCol() < GameState.MAXCOLS && Character.isLetter(movement.getLetter())
					&& Character.isUpperCase(movement.getLetter())) {
				// Accept movements only if it is playing
				if (getState().getState() == GameState.State.Playing) {
					// If it is the players turn
					if (currentPlayer.getId().equals(clientId)) {
						// If it is an empty square
						if (getState().getBoard()[movement.getRow()][movement.getCol()] == GameState.EMPTYSQUARE) {
							getState().getBoard()[movement.getRow()][movement.getCol()] = movement.getLetter();

							passCount = 0;
							//Register the last movement in the game state
							getState().setLastMovement(movement);

							// Reset the voting state
							ResetVoting();
							
							// If the player has not asked for a word to be voted
							if (claimForWord == ClaimForWord.NoClaim) {
								// Continue game
								getState().setState(GameState.State.Playing);
								// Change turn to next player
								NextPlayer();
							} else {
								// Change the state of the game to voting
								getState().setState(GameState.State.Voting);
								// Get the word
								Word wordForVoting = GenerateWord(claimForWord);
								// Create the voting state in the game state 
								getState().setVotingState(new VotingState(currentPlayer.getName(), wordForVoting));
							}

							getState().setLastAction(GameState.LastAction.PlayerPlayed);

							// Update all clients
							UpdateClients();
						} else {
							throw new OperationNotAllowedException("There is an existing letter in that position");
						}
					} else {
						throw new OperationNotAllowedException("You cannot place a letter if it is not your turn");
					}
				} else {
					throw new OperationNotAllowedException("Cannot make a movement if the game has not started yet");
				}
			} else {
				throw new OperationNotAllowedException("Supplied arguments are not valid");
			}
		}
	}

	/**
	 *  Allows players to vote for a word
	 */
	public void VoteForWord(String clientId, boolean rightWord) throws RemoteException, OperationNotAllowedException {
		synchronized (this) {
			// If the current game state is voting
			if (getState().getState() == GameState.State.Voting) {
				PlayerObserver player = findPlayer(clientId);
				// If the player is in the game
				if (player != null) {

					// If the player have not voted yet
					if (player.getVote() == null) {
						getState().setLastAction(LastAction.PlayerVoted);
						//Set the player's vote in the current game state
						player.setVote(rightWord);

						boolean votingEnded = true;
						int negativeVotes = 0;
						// Check if all players voted
						// int positiveVotes = 0;
						for (PlayerObserver playerObserver : gameplayers) {
							if (playerObserver.getVote() == null) {
								votingEnded = false;
							} else if (playerObserver.getVote() == false) {
								negativeVotes += 1;
							} else {
								// positiveVotes += 1;
							}
						}

						// If all players voted
						if (votingEnded) {
							VotingState votingState = getState().getVotingState();
							//If all votes are positive, the result is positive, otherwise it is negative
							if (negativeVotes == 0) {
								votingState.setVotingResult(true);
								currentPlayer.addToScore(votingState.getWordForVoting().getContent().length());
							} else {
								votingState.setVotingResult(false);
							}
							
							// Return to play
							getState().setState(GameState.State.Playing);
							//Change to next player
							NextPlayer();
						}

						// Update all clients
						UpdateClients();
					} else {
						throw new OperationNotAllowedException("You are not allowed to vote again");
					}
				} else {
					throw new OperationNotAllowedException("You are not a player");
				}
			} else {
				throw new OperationNotAllowedException("You cannot vote if there is no current voting");
			}
		}
	}

	/**
	 * Allows a client to pass the turn
	 */
	public void PassTurn(String clientId) throws RemoteException, OperationNotAllowedException {
		synchronized (this) {
			// If the game is in the playing state
			if (getState().getState() == GameState.State.Playing) {
				// If it is the player's turn
				if (clientId.equals(currentPlayer.getId())) {
					//add one to the count pass
					passCount++;
					ResetVoting();

					// If all players have not passed
					if (passCount < gameplayers.size()) {
						getState().setState(GameState.State.Playing);
						// Set the nest player
						NextPlayer();
					} else {
						//End the game
						CalculateWinner();
						getState().setState(GameState.State.Ended);
					}
					getState().setLastAction(GameState.LastAction.PlayerPassed);

					// Update all clients
					UpdateClients();
				} else {
					throw new OperationNotAllowedException("You cannot pass if it is not your turn");
				}
			} else {
				throw new OperationNotAllowedException("You cannot pass a turn if the game has not started yet");
			}
		}
	}

	/**
	 * Allows a player to end the game
	 */
	public void EndGame(String clientId) throws RemoteException, OperationNotAllowedException {
		synchronized (this) {
			// The game can be ended only when the players are playing, voting or joining the game
			if (getState().getState() == GameState.State.Playing || getState().getState() == GameState.State.Voting
					|| getState().getState() == GameState.State.Joining) {
				PlayerObserver player = findPlayer(clientId);
				// If the player is in the game
				if (player != null) {
					//End the game
					endGame();

					// Update all clients
					UpdateClients();
				} else {
					// TODO : Test disconnections, the game should end
					throw new OperationNotAllowedException("You has to be a player to disconnect");
				}
			} else {
				throw new OperationNotAllowedException("You cannot end a game if the game has not started yet");
			}
		}
	}

	/**
	 * Ends a game when a user left
	 */
	private void endGame() {
		//Calculates the winner
		CalculateWinner();
		getState().setState(GameState.State.Ended);
		getState().setLastAction(GameState.LastAction.UserLeft);
		ResetVoting();
	}

	/**
	 * Updates all the registered clients
	 */
	private void UpdateClients() {
		//Updates the current movement id
		getState().setMovementCount(getState().getMovementCount() + 1);

		// Set the current player in the game state
		if (currentPlayer != null) {
			getState().setCurrentPlayerName(currentPlayer.getName());
		} else {
			getState().setCurrentPlayerName(null);
		}
		// Set the previous player in the game state
		if (previousPlayer != null) {
			getState().setPreviousPlayerName(previousPlayer.getName());
		} else {
			getState().setPreviousPlayerName(null);
		}
		
		// If a voting state was built, pass the voting state to the game state
		VotingState votingState = getState().getVotingState();
		if (votingState != null) {
			List<Vote> votes = gameplayers.stream().filter(o -> o.getVote() != null)
					.map(o -> new Vote(o.getName(), o.getVote())).collect(Collectors.toList());

			votingState.setVotes(votes.toArray(new Vote[votes.size()]));
		}

		// Update all the player information in the game state
		getState().setPlayers(
				gameplayers.stream().map(x -> new Player(x.getName(), x.getScore(), x.getInvitationStatus())).collect(Collectors.toList()));

		// Check if all clients are alive in a multi-threaded way
		List<Future<?>> list = new ArrayList<Future<?>>();
		for (PlayerObserver po : gameplayers) {
			Future<?> future = executorService.submit(() -> {
				try {
					po.getObserver().IsActive();
				} catch (RemoteException e) {
					//e.printStackTrace();
					//If a client is not active, set its state to false
					po.setActive(false);
					throw new RuntimeException(e);
				}
			});
			list.add(future);
		}
		
		boolean allAlive = waitForFutures(list);
		list.clear();
		
		//If all players where not alive
		if (!allAlive) {
			if (getState().getState() == GameState.State.Playing
					|| getState().getState() == GameState.State.Voting) {
				// end the game
				endGame();
			}
			// Remove not alive players from update
			gameplayers.stream().filter(p -> p.isActive()).collect(Collectors.toList());
		}
		
		// Update all clients in a multi-threaded way
		for (PlayerObserver po : gameplayers) {
			Future<?> future = executorService.submit(() -> {
				try {
					po.getObserver().Update(getState());
				} catch (RemoteException e) {
					//e.printStackTrace();
					throw new RuntimeException(e);
				}
			});
			list.add(future);
		}
		
		waitForFutures(list);
	}

	// Wait until all the multi-threaded calls end
	private boolean waitForFutures(List<Future<?>> list) {
		boolean allClientsNotified = true;
		for (Future<?> future : list) {
			try {
				future.get();
			} catch (InterruptedException | ExecutionException e) {
				//e.printStackTrace();
				allClientsNotified = false;
			}
		}
		return allClientsNotified;
	}

	/**
	 * Calculates the winners
	 */
	private void CalculateWinner() {
		int maxScore = gameplayers.stream().mapToInt(o -> o.getScore()).max().orElse(-1);

		List<String> result = gameplayers.stream().filter(o -> o.getScore() == maxScore).map(o -> o.getName())
				.collect(Collectors.toList());

		getState().setWinnerNames(result.toArray(new String[result.size()]));
	}

	/**
	 * Find a player using its client id
	 */
	private PlayerObserver findPlayer(String clientId) {
		return gameplayers.stream().filter(p -> p.getId().equals(clientId)).findFirst().orElse(null);
	}

	/**
	 * Find a player by its username
	 */
	private PlayerObserver findPlayerByName(String name) {
		return gameplayers.stream().filter(p -> p.getName().equals(name)).findFirst().orElse(null);
	}

	/**
	 * Put the players iterator in the playar with the given clientId
	 */
	private void ResetIteratorTo(String clientId) {
		previousPlayer = null;
		currentPlayer = null;
		playersIterator = gameplayers.iterator();
		while (playersIterator.hasNext() && (currentPlayer == null || !clientId.equals(currentPlayer.getId()))) {
			currentPlayer = playersIterator.next();
		}
	}

	// Selects the next player
	private void NextPlayer() {
		//Set the previous player
		previousPlayer = currentPlayer;
		
		do {
			// if there is no iterator, create a new
			if (playersIterator == null) {
				playersIterator = gameplayers.iterator();
			}
			// if the iterator has next player, select the next player in the iterator
			if (playersIterator.hasNext()) {
				currentPlayer = playersIterator.next();
			} else {
				//set the iterator to null
				playersIterator = null;
			}
		// Repeat while there is no iterator
		} while (playersIterator == null);
	}

	/**
	 * Returns the word the user is claiming for
	 * @param claimForWord
	 * @return
	 */
	private Word GenerateWord(ClaimForWord claimForWord) {
		int yMovement = 0;
		int xMovement = 0;
		switch (claimForWord) {
		case Up:
			xMovement = 1;
			break;
		case Down:
			xMovement = -1;
			break;
		case Left:
			yMovement = 1;
			break;
		case Right:
			yMovement = -1;
			break;
		default:
			// Should not happen never
			break;
		}

		int xCurrent = getState().getLastMovement().getRow();
		int yCurrent = getState().getLastMovement().getCol();
		char[][] board = getState().getBoard();

		do {
			xCurrent += xMovement;
			yCurrent += yMovement;
		} while (xCurrent >= 0 && xCurrent < GameState.MAXROWS && yCurrent >= 0 && yCurrent < GameState.MAXCOLS
				&& board[xCurrent][yCurrent] != GameState.EMPTYSQUARE);

		// Undo last movement
		xCurrent -= xMovement;
		yCurrent -= yMovement;

		int startRow = xCurrent;
		int startColumn = yCurrent;

		StringBuilder content = new StringBuilder();

		// Reverse movements
		xMovement *= -1;
		yMovement *= -1;

		do {
			content.append(board[xCurrent][yCurrent]);
			xCurrent += xMovement;
			yCurrent += yMovement;
		} while (xCurrent >= 0 && xCurrent < GameState.MAXROWS && yCurrent >= 0 && yCurrent < GameState.MAXCOLS
				&& board[xCurrent][yCurrent] != GameState.EMPTYSQUARE);

		// Undo last movement
		xCurrent -= xMovement;
		yCurrent -= yMovement;

		Word word = new Word(content.toString(), startRow, startColumn, xCurrent, yCurrent, claimForWord);

		return word;
	}

	/**
	 * Set the voting state to null when it is not voting
	 */
	private void ResetVoting() {
		getState().setVotingState(null);
		for (PlayerObserver playerObserver : gameplayers) {
			playerObserver.setVote(null);
		}
	}

	/**
	 * return the current game state
	 * @return
	 */
	public GameState getState() {
		return _state;
	}

}
