package scrabble.server;

/*
 *  Dean Pakravan: 757389
 *  Qijie Li: 927249 
 *  Luis Zapata: 938907 
 *  Dongming Li: 1002971
 *  Assignment 2: Distributed Systems - Sem2 2018
 */

import static org.junit.jupiter.api.Assertions.*;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import scrabble.common.ClaimForWord;
import scrabble.common.GameState;
import scrabble.common.IScrabbleObserver;
import scrabble.common.Movement;
import scrabble.common.OperationNotAllowedException;
import scrabble.common.Player;
import scrabble.common.Vote;

/**
 * Class for testing the controller alone
 * @author Miguel
 *
 */
class GameControllerTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}
	
	private static String step = null;
	private static TestObserver previousPlayerObserver = null;
	private static TestObserver currentPlayerObserver = null;
	private static TestObserver nextPlayerObserver = null;
	private static TestObserver currentPlayerVoting = null;
	private static TestObserver[] playerObservers = new TestObserver[3];
	private static int passes = 0;
	private static int votes = 0;
	private static Step lastStep;

	private class TestObserver implements IScrabbleObserver {

		private String name;
		private String id;
		private Movement movement;
		
		
		private TestObserver(String name, String id) {
			super();
			this.name = name;
			this.id = id;
		}

		@Override
		public void Update(GameState state) throws RemoteException {
			ControlGameState(state);
		}

		@Override
		public boolean IsActive() throws RemoteException {
			// TODO Auto-generated method stub
			return false;
		}
	}
	
	private static void ControlGameState(GameState state) {
		GameState.State innerState = state.getState();
		GameState.LastAction lastAction = state.getLastAction();
		List<Player> players = state.getPlayers();
		Movement lastMovement = state.getLastMovement();
		char[][] board = state.getBoard();
		switch (step) {
		
		case "GameCreated":
			assertEquals(innerState, GameState.State.Joining);
			assertEquals(lastAction, GameState.LastAction.GameCreated);
			break;
			
		case "JoinGameP0":
			assertEquals(innerState, GameState.State.Joining);
			assertEquals(lastAction, GameState.LastAction.UserJoined);
			assertEquals(players.size(), 1);
			assertEquals(players.get(0).getName(), currentPlayerObserver.name);
			break;

		case "JoinGameP1":
			assertEquals(innerState, GameState.State.Joining);
			assertEquals(lastAction, GameState.LastAction.UserJoined);
			assertEquals(players.size(), 2);
			assertEquals(players.get(1).getName(), currentPlayerObserver.name);
			break;
			
		case "JoinGameP2":
			assertEquals(innerState, GameState.State.Joining);
			assertEquals(lastAction, GameState.LastAction.UserJoined);
			assertEquals(players.size(), 3);
			assertEquals(players.get(2).getName(), currentPlayerObserver.name);
			break;
			
		case "StartGame":
			assertEquals(innerState, GameState.State.Playing);
			assertEquals(lastAction, GameState.LastAction.GameStarted);
			assertEquals(state.getCurrentPlayerName(), currentPlayerObserver.name);
			assertEquals(state.getPreviousPlayerName(), null);
			break;
			
		case "Movement":
			if (lastStep.claim == ClaimForWord.NoClaim) {
				assertEquals(innerState, GameState.State.Playing);
				assertEquals(lastAction, GameState.LastAction.PlayerPlayed);
				assertEquals(state.getCurrentPlayerName(), nextPlayerObserver.name);
				assertEquals(state.getPreviousPlayerName(), currentPlayerObserver.name);
				Movement cpMovement = currentPlayerObserver.movement;
				assertEquals(lastMovement, cpMovement);
				assertEquals(board[lastMovement.getRow()][lastMovement.getCol()], cpMovement.getLetter());
			} else {
				assertEquals(innerState, GameState.State.Voting);
				assertEquals(lastAction, GameState.LastAction.PlayerPlayed);
				assertEquals(state.getCurrentPlayerName(), currentPlayerObserver.name);
				assertEquals(state.getPreviousPlayerName(), previousPlayerObserver.name);
				Movement cpMovement = currentPlayerObserver.movement;
				assertEquals(lastMovement, cpMovement);
				assertEquals(board[lastMovement.getRow()][lastMovement.getCol()], cpMovement.getLetter());
				assertNotEquals(null, state.getVotingState());
				assertEquals(lastStep.word, state.getVotingState().getWordForVoting().getContent());
			}
			
			break;
			
		case "Pass":
			if (passes < playerObservers.length) {
				assertEquals(innerState, GameState.State.Playing);
				assertEquals(lastAction, GameState.LastAction.PlayerPassed);
				assertEquals(state.getCurrentPlayerName(), nextPlayerObserver.name);
				assertEquals(state.getPreviousPlayerName(), currentPlayerObserver.name);
			} else {
				assertEquals(innerState, GameState.State.Ended);
				assertEquals(lastAction, GameState.LastAction.PlayerPassed);
				//TODO Test the end and the score
			}
			break;
		
		case "Vote":
			Vote vote = Stream.of(state.getVotingState().getVotes()).filter(v -> v.getPlayerName().equals(lastStep.clientNameVoting)).findFirst().orElse(null);
			assertNotEquals(vote, null);
			assertEquals(vote.getValue(), lastStep.vote);
			if (votes < playerObservers.length) {
				assertEquals(innerState, GameState.State.Voting);
				assertEquals(lastAction, GameState.LastAction.PlayerVoted);
				assertEquals(state.getCurrentPlayerName(), currentPlayerObserver.name);
				assertEquals(state.getPreviousPlayerName(), previousPlayerObserver.name);
				assertEquals(state.getVotingState().getStartedVotingPlayerName(), currentPlayerObserver.name);
			} else {
				assertEquals(innerState, GameState.State.Playing);
				assertEquals(lastAction, GameState.LastAction.PlayerVoted);
				assertEquals(state.getCurrentPlayerName(), nextPlayerObserver.name);
				assertEquals(state.getPreviousPlayerName(), currentPlayerObserver.name);
				assertEquals(state.getVotingState().getStartedVotingPlayerName(), currentPlayerObserver.name);
				assertEquals(state.getVotingState().getVotes().length, playerObservers.length);
				Player player = state.getPlayers().stream().filter(p -> p.getName().equals(currentPlayerObserver.name)).findFirst().get();
				assertEquals(player.getScore(), lastStep.targetScore);
				assertEquals(state.getVotingState().getVotingResult(), lastStep.votingResult);
			}
			break;
			
		case "End":
			assertEquals(innerState, GameState.State.Ended);
			assertArrayEquals(state.getWinnerNames(), lastStep.winners);
			break;
			
		default:
			break;
		}
	}
	
	private enum StepType {
		Movement,
		Pass,
		Vote,
		End
	}
	
	private class Step {
		private StepType type;
		private Movement movement;
		private ClaimForWord claim;
		private String word;
		private int targetScore;
		private boolean vote;
		private Boolean votingResult;
		private String clientNameVoting;
		private String winners[];
		
		public Step(StepType type, Movement movement, ClaimForWord claim, String word) {
			this.type = type;
			this.movement = movement;
			this.claim = claim;
			this.word = word;
		}
		
		public Step(StepType type, String clientNameVoting, boolean vote, Boolean votingResult, int targetScore) {
			this.type = type;
			this.targetScore = targetScore;
			this.vote = vote;
			this.votingResult = votingResult;
			this.clientNameVoting = clientNameVoting;
		}
		
		public Step(StepType type, int targetScore, String[] winners) {
			this.type = type;
			this.targetScore = targetScore;
			this.winners = winners;
		}
	}
	
	@Test
	void testGame() throws RemoteException, OperationNotAllowedException {
		
		
		GameController controller = new GameController();
		GameState state = controller.getState();
		
		step = "GameCreated";
		ControlGameState(state);
		
		playerObservers[0] = new TestObserver("player0", "");
		playerObservers[1] = new TestObserver("player1", "");
		playerObservers[2] = new TestObserver("player2", "");
		
		//Join player 0
		step = "JoinGameP0";
		currentPlayerObserver = playerObservers[0];
		currentPlayerObserver.id = controller.JoinGame(currentPlayerObserver.name, currentPlayerObserver);
		
		//Join player 1
		step = "JoinGameP1";
		currentPlayerObserver = playerObservers[1];
		currentPlayerObserver.id = controller.JoinGame(currentPlayerObserver.name, currentPlayerObserver);
		
		//Join player 2
		step = "JoinGameP2";
		currentPlayerObserver = playerObservers[2];
		currentPlayerObserver.id = controller.JoinGame(currentPlayerObserver.name, currentPlayerObserver);
		
		step = "StartGame";
		currentPlayerObserver = playerObservers[1];
		controller.StartGame(currentPlayerObserver.id);
		
		ConfigureStartPlayerIndexes(1);
		
		LinkedList<Step> steps = new LinkedList<Step>();
		steps.add(new Step(StepType.Movement, new Movement(2,3,'A'), ClaimForWord.NoClaim, null)); //1
		steps.add(new Step(StepType.Movement, new Movement(2,4,'B'), ClaimForWord.NoClaim, null)); //2
		steps.add(new Step(StepType.Movement, new Movement(2,2,'C'), ClaimForWord.NoClaim, null)); //0
		steps.add(new Step(StepType.Pass, null, ClaimForWord.NoClaim, null)); //1
		steps.add(new Step(StepType.Pass, null, ClaimForWord.NoClaim, null)); //2
		//steps.add(new Step(StepType.Pass, null, ClaimForWord.NoClaim, null));
		
		steps.add(new Step(StepType.Movement, new Movement(2,1,'D'), ClaimForWord.Down, "DCAB")); //0
		steps.add(new Step(StepType.Vote, playerObservers[2].name, true, null, 0));
		steps.add(new Step(StepType.Vote, playerObservers[0].name, true, null, 0));
		steps.add(new Step(StepType.Vote, playerObservers[1].name, true, true, 4));
		
		steps.add(new Step(StepType.Movement, new Movement(1,2,'E'), ClaimForWord.NoClaim, null)); //1
		steps.add(new Step(StepType.Movement, new Movement(3,2,'F'), ClaimForWord.Right, "ECF")); //2
		steps.add(new Step(StepType.Vote, playerObservers[0].name, false, null, 0));
		steps.add(new Step(StepType.Vote, playerObservers[2].name, true, null, 0));
		steps.add(new Step(StepType.Vote, playerObservers[1].name, true, false, 0));
		
		steps.add(new Step(StepType.End, 4, new String[] {playerObservers[0].name})); //0
		
		for (Step s : steps) {
			step = s.type.toString();
			lastStep = s;
			switch (s.type) {
			case Movement:
				ChangePlayers();
				passes = 0;
				votes = 0;
				currentPlayerObserver.movement = s.movement;
				controller.PlaceLetter(currentPlayerObserver.id, currentPlayerObserver.movement, s.claim);
				break;

			case Pass:
				ChangePlayers();
				passes++;
				votes = 0;
				currentPlayerObserver.movement = null;
				controller.PassTurn(currentPlayerObserver.id);
				break;
				
			case Vote:
				passes = 0;
				votes++;
				currentPlayerObserver.movement = null;
				currentPlayerVoting = Stream.of(playerObservers).filter(v -> v.name.equals(s.clientNameVoting)).findFirst().get();
				controller.VoteForWord(currentPlayerVoting.id, s.vote);
				break;
				
			case End:
				controller.EndGame(currentPlayerObserver.id);
				break;
				
			default:
				break;
			}
			
		}
		
		
		
		//fail("Not yet implemented");
	}
	
	private int previowsPlayerIndex = 0;
	private int currentPlayerIndex = 0;
	private int nextPlayerIndex = 0;
	
	private void ConfigureStartPlayerIndexes(int startPlayerIndex) {
		currentPlayerIndex = startPlayerIndex - 1 >= 0 ? startPlayerIndex - 1 : playerObservers.length - 1;
		previowsPlayerIndex = currentPlayerIndex - 1 >= 0 ? currentPlayerIndex - 1 : playerObservers.length - 1;
		nextPlayerIndex = currentPlayerIndex + 1 < playerObservers.length ? currentPlayerIndex + 1 : 0;
	}
	
	private void ChangePlayers() {
		previowsPlayerIndex = currentPlayerIndex;
		currentPlayerIndex = nextPlayerIndex;
		nextPlayerIndex = currentPlayerIndex + 1 < playerObservers.length ? currentPlayerIndex + 1 : 0;
		SetPlayersByIndex();
	}
	
	private void SetPlayersByIndex() {
		previousPlayerObserver = playerObservers[previowsPlayerIndex];
		currentPlayerObserver = playerObservers[currentPlayerIndex];
		nextPlayerObserver = playerObservers[nextPlayerIndex];
	}

}
