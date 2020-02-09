package scrabble.client.gui;

/*
 *  Dean Pakravan: 757389
 *  Qijie Li: 927249 
 *  Luis Zapata: 938907 
 *  Dongming Li: 1002971
 *  Assignment 2: Distributed Systems - Sem2 2018
 */

import java.awt.*;

import javax.swing.*;

import scrabble.common.ClaimForWord;
import scrabble.common.GameState;
import scrabble.common.IScrabbleServer;
import scrabble.common.Movement;
import scrabble.common.OperationNotAllowedException;
import scrabble.common.Player;
import scrabble.common.Vote;
import scrabble.common.GameState.LastAction;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;


@SuppressWarnings("serial")
public class GUI extends JFrame {
	
	private Container contentPane;
	private JPanel eastPanel, westPanel, centerPanel;
	private JButton nextTurnBtn, passBtn, wordLTRBtn, wordTTBBtn, exitBtn, ruleBtn;
	private JTextField letter;
	private JTextArea votingListArea;
	// Global variables to communicate with the server
	private ArrayList<Player> playersNotVoted;
	private GameState currentState;
	private String clientId;
	private String playerName;
	private boolean enableNextMoveButtons;
	private IScrabbleServer server;
	private boolean handledExit = false;
	
	
	public GUI(){
		super("Scrabble");

//-------------------------------------------------------------------------------------------

//Window setup
		Toolkit tk = Toolkit.getDefaultToolkit();
		int xSize = ((int) tk.getScreenSize().getWidth());
		int ySize = ((int) tk.getScreenSize().getHeight());
		
		contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout(0, 0));
		
		
//--------------------------------------------------------------------------------------------
		
//Area Setup		
		
		centerPanel = new JPanel();
		centerPanel.setLayout(new GridLayout(20, 20, 0, 0));
		contentPane.add(centerPanel, BorderLayout.CENTER);
		
		eastPanel = new JPanel();
		eastPanel.setLayout(new GridLayout(0, 1, 0, 0));
		contentPane.add(eastPanel, BorderLayout.EAST);
	
		westPanel = new JPanel();
		westPanel.setLayout(new GridBagLayout());
		contentPane.add(westPanel, BorderLayout.WEST);
		
		eastPanel.setBackground(Color.getHSBColor(0.567F, 0.96F, 0.1632F));
		westPanel.setBackground(Color.getHSBColor(0.567F, 0.96F, 0.1632F));
		centerPanel.setBackground(Color.getHSBColor(0.567F, 0.96F, 0.1632F));
		
		
		//Content setup		
		setBounds(0, 0, xSize, (ySize - 50));
		setVisible(true);
		repaint();
		
		this.addWindowListener(new WindowAdapter() {
			  public void windowClosing(WindowEvent we) {
				if (!handledExit) {
					try {
						server.EndGame(clientId);
					} catch (RemoteException | OperationNotAllowedException e) {
						e.printStackTrace();
					}
				}
			  }
		});
	}
	
//--------------------------------------------------------------------------------------------

	public void configure(int clientsNumber, String clientId, String playerName, 
			IScrabbleServer server) {
		this.clientId = clientId;
		this.playerName = playerName;
		this.server = server;
		setTitle(getTitle() + " - " + playerName);
	}
	
//--------------------------------------------------------------------------------------------
	
//Listeners	

	// Listener for whenever we press the grid
	// All we want the tile press to do is highlight the green one
	private class tilePress implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent ea) {
			// input is the location of the scrabble board
			JButton input =  (JButton) ea.getSource();
			for (int i = 0; i < centerPanel.getComponentCount(); i++) {
				if (centerPanel.getComponent(i).getBackground() != Color.CYAN) {
					centerPanel.getComponent(i).setBackground(Color.getHSBColor(0.147F, 0.17F, 0.14F));
				}
			}
			if (input.getBackground() != Color.CYAN) {
				input.setBackground(Color.green);
			}
			
		}
	}
	
	// Listener for whenever we add a letter to a tile on the grid
	private class LetterPress implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			JButton input =  (JButton) e.getSource();
			try {
				String inputLetter = letter.getText();
			
				// Checking it is a letter from the alphabet
				char[] arr = inputLetter.toCharArray();
				int val = (int)arr[0];
				
				// Make sure we only add one letter
				if (inputLetter.length()!= 1) {
					JOptionPane optionPane = new JOptionPane("Enter only one letter!", JOptionPane.ERROR_MESSAGE);    
					JDialog dialog = optionPane.createDialog("Failure");
					dialog.setAlwaysOnTop(true);
					dialog.setVisible(true);
				}
				// Checking it is a letter from the alphabet
				else if(!((val >=65 && val <= 90) || (val >= 97 && val <= 122))) {
					JOptionPane optionPane = new JOptionPane("Enter one letter from the alphabet!", JOptionPane.ERROR_MESSAGE);    
					JDialog dialog = optionPane.createDialog("Failure");
					dialog.setAlwaysOnTop(true);
					dialog.setVisible(true);
				}
				// If the letter is valid, we add it to the board
				else {
					char character = inputLetter.charAt(0);
					// Index to interate through the GUI board
					int j;
					for(j=0;j<centerPanel.getComponentCount();j++){
						JButton letterBlock = (JButton) centerPanel.getComponent(j);
						// Check where we are going to place the letter, making sure it is empty
						if(letterBlock.getBackground() == Color.GREEN){
							letter.setEditable(false);
							// Find the row & column of the letter placement
							int row = j / 20;
							int column = j % 20;
							// Change the letter to uppercase
							char letter = Character.toUpperCase(character);
							// We track the letters adjacent of the newly placed letter
							Movement movement = new Movement(row, column, letter);
							try {
								// If we claim a LTR word
								if (input == wordLTRBtn) {
									server.PlaceLetter(clientId, movement, ClaimForWord.Right);
								}
								// If we claim a TTB word
								else if (input == wordTTBBtn) {
									server.PlaceLetter(clientId, movement, ClaimForWord.Down);
								}
								// If we are just placing a letter
								else {
									server.PlaceLetter(clientId, movement, ClaimForWord.NoClaim);
								}
							} catch (RemoteException | OperationNotAllowedException e1) {
								// TODO Think about this error
								e1.printStackTrace();
							}
							break;
						}
					}
					// If the player attempts to place a letter without clicking on the grid first
					if (j==centerPanel.getComponentCount()) {
						JOptionPane optionPane = new JOptionPane("Click a box on the grid first", JOptionPane.ERROR_MESSAGE);    
						JDialog dialog = optionPane.createDialog("Failure");
						dialog.setAlwaysOnTop(true);
						dialog.setVisible(true);
					}
					
				
				}
			// Catch if the player does not enter a letter
			} catch(ArrayIndexOutOfBoundsException e1) {
				JOptionPane optionPane = new JOptionPane("Please enter a letter", JOptionPane.ERROR_MESSAGE);    
				JDialog dialog = optionPane.createDialog("Failure");
				dialog.setAlwaysOnTop(true);
				dialog.setVisible(true);
			}
		}
	}
	
	// Method if we pass turn
	// Also handles the case when ALL players pass - End game
	private class PassTurn implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				server.PassTurn(clientId);
			} catch (RemoteException | OperationNotAllowedException e1) {
				// TODO Think what should happen
				e1.printStackTrace();
			}
		}
	}
	
	// Method to display the rules in a new JFrame
	private class displayRules implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
	      JFrame frame = new JFrame("Rules");

	      frame.getContentPane().setBackground(Color.getHSBColor(0.847F, 0.6F, 0.23F));
	      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	      frame.setPreferredSize(new Dimension(700, 400));
	      
	      JLabel title = new JLabel("The Rulebook:");
	      title.setFont(new Font("Jokerman", Font.BOLD, 25));
	      title.setForeground(Color.RED);
	      frame.getContentPane().add(title, BorderLayout.NORTH); 
	      
	      // <html> & <p> are Html coding operators that JLabel uses
	      // We use them to create a new line rather than several JLabels
	      JLabel coreGameInfo = new JLabel("<html><p>How the core game is played:</p><p>"
	      		+ "1). Click a tile</p><p>"
	      		+ "2). Then type a letter into the orange box</p><p>"
	      		+ "3). Press \"Next Player\" to add the letter to the board and change turns</p><p>"
	      		+ "4). Pass your turn if you cannot make a move</p><p>"
	      		+ "5). Continue until ALL players pass their turn consecutively.</p><p>"
	      		+ "6). The player with the highest score is the winner.</p></html>");
	      coreGameInfo.setFont(new Font("TimesNewRoman", Font.PLAIN, 15));
	      coreGameInfo.setForeground(Color.WHITE);
	      frame.getContentPane().add(coreGameInfo, BorderLayout.CENTER);
	      
	      JLabel scoringInfo = new JLabel("<html><p>Scoring:</p><p>"
		      		+ "->  Players can score in two ways.</p><p>"
		      		+ "1). By making a word that reads from left to right (LTR) across the board OR</p><p>"
		      		+ "2). By making a word that reads from top to bottom (TTB) along the board</p><p>"
		      		+ "->  Submit the correct directional button if your next letter"
		      		+ " will make a word.</p><p>"
		      		+ "->  All other players will have a chance to vote if "
		      		+ "they think the word is correct.</p><p>"
		      		+ "->  If majority of players agree, then the player recieves points "
		      		+ "based on the length of the word</p></html>");
	      scoringInfo.setFont(new Font("TimesNewRoman", Font.PLAIN, 15));
	      scoringInfo.setForeground(Color.WHITE);
	      frame.getContentPane().add(scoringInfo, BorderLayout.AFTER_LAST_LINE);
	      
	      frame.setLocation(50,50);
	      frame.pack();
	      frame.setVisible(true);
		   
		}
	}
	
	// If a player presses exit
	private class stopGame implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (!handledExit) {
				try {
					server.EndGame(clientId);
				} catch (RemoteException | OperationNotAllowedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}
//--------------------------------------------------------------------------------------------
	
//Methods
		
	// When we update the GUI board
	public void boardUpdate(){
		centerPanel.removeAll();
		char [][] tempBoard = currentState.getBoard();
		for(int i = 0; i < tempBoard.length; i++)
		{
			for(int j = 0; j < tempBoard[i].length; j++)
			{
				JButton temp = new JButton();
				if(tempBoard[i][j] != 0)
				{
					temp.setText(tempBoard[i][j]+"");
					temp.setBackground(Color.CYAN);
				}
				else{
					temp.setBackground(Color.getHSBColor(0.147F, 0.17F, 0.14F));
				}
				temp.addActionListener(new tilePress());
				temp.setEnabled(enableNextMoveButtons);
				centerPanel.add(temp);
			}	
		}
		
		centerPanel.revalidate();
		centerPanel.repaint();
		repaint();
	}
		
	// Redraw the westPanel for each update
	public void westPanelRedraw(){
		westPanel.removeAll();
		westPanel.setLayout(new GridLayout(0,1, 0, 0));
		contentPane.add(westPanel, BorderLayout.WEST);
		
		// Displaying the current player
		JLabel displayPlayer = new JLabel(currentState.getCurrentPlayerName() + "'s turn.");
		displayPlayer.setFont(new Font("SansSerif", Font.BOLD, 15));
		displayPlayer.setForeground(Color.WHITE);
		westPanel.add(displayPlayer);

		// Displaying player scores on the LHS - refresh after each turn
		for(Player player : currentState.getPlayers()){
			JLabel displayScores = new JLabel(player.getName()+ ": " + player.getScore(),
					JLabel.LEFT);
			displayScores.setFont(new Font("SansSerif", Font.PLAIN, 12));
			displayScores.setForeground(Color.WHITE);
			westPanel.add(displayScores);
		}
		
		nextTurnBtn = new JButton("Next Player");
		nextTurnBtn.setBackground(Color.getHSBColor(0.394F, 0.86F, 0.3F));
		nextTurnBtn.setFont(new Font("Serif", Font.BOLD, 15));
		nextTurnBtn.setForeground(Color.white);
		nextTurnBtn.addActionListener(new LetterPress());
		nextTurnBtn.setEnabled(enableNextMoveButtons);
		westPanel.add(nextTurnBtn);
		
		westPanel.revalidate();
		westPanel.repaint();

	}
	
	// Redraw the east panel after each turn
	public void eastPanelRedraw(){
		eastPanel.removeAll();
		eastPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		Font bFont = new Font("Serif", Font.BOLD, 15);
		
		// Pass button
		passBtn = new JButton("Pass");
		c.weightx = 0.2;
		c.weighty = 1;
		c.gridy = 0;
		c.gridx = 1;
		eastPanel.add(passBtn, c);
		passBtn.addActionListener(new PassTurn());
		passBtn.setEnabled(enableNextMoveButtons);
		
		// RTL = Right to Left (word)
		wordLTRBtn = new JButton("Claim to have a word (LTR)");
		c.gridy = 1;
		c.gridx = 1;
		eastPanel.add(wordLTRBtn, c);
		wordLTRBtn.addActionListener(new LetterPress());
		wordLTRBtn.setEnabled(enableNextMoveButtons);
		
		// TTB = Top to Bottom (word)
		wordTTBBtn = new JButton("Claim to have a word (TTB)");
		c.gridy = 2;
		c.gridx = 1;
		eastPanel.add(wordTTBBtn, c);
		wordTTBBtn.addActionListener(new LetterPress());
		wordTTBBtn.setEnabled(enableNextMoveButtons);
		
		// Button for rules - displays the rules in a new jframe
		ruleBtn = new JButton("How to play (Rules)");
		c.gridy = 3;
		c.gridx = 1;
		eastPanel.add(ruleBtn, c);
		ruleBtn .addActionListener(new displayRules());
		
		// If it is our turn, we can enter a new letter
		if (enableNextMoveButtons) {
			JLabel enter = new JLabel("Enter a letter in the orange box");
			c.gridx = 1;
			c.gridy = 4;
			c.weighty = 0.1;
			enter.setFont(bFont);
			enter.setForeground(Color.white);
			eastPanel.add(enter, c);
			
			letter = new JTextField();
			c.weightx = 3;
			c.gridy = 5;
			c.gridx = 1;
			c.fill=GridBagConstraints.HORIZONTAL;
			// orange colour for fun
			letter.setBackground(Color.ORANGE);
			eastPanel.add(letter, c);
			letter.addActionListener(new LetterPress());
		}
		
		// Exit button so a player can leave anytime they wish
		exitBtn = new JButton("Exit");
		c.weightx = 0.2;
		c.weighty = 4;
		c.gridy = 6;
		c.gridx = 1;
		eastPanel.add(exitBtn, c);
		exitBtn .addActionListener(new stopGame());
	
		// Button customisation
		passBtn.setBackground(Color.getHSBColor(0.394F, 0.86F, 0.3F));
		wordLTRBtn.setBackground(Color.getHSBColor(0.394F, 0.86F, 0.3F));
		wordTTBBtn.setBackground(Color.getHSBColor(0.394F, 0.86F, 0.3F));
		ruleBtn.setBackground(Color.getHSBColor(0.394F, 0.86F, 0.3F));
		exitBtn.setBackground(Color.getHSBColor(0F, 0.96F, 0.59F));
		
		passBtn.setFont(bFont);
		wordLTRBtn.setFont(bFont);
		wordTTBBtn.setFont(bFont);
		ruleBtn.setFont(bFont);
		exitBtn.setFont(bFont);
		
		passBtn.setForeground(Color.white);
		wordLTRBtn.setForeground(Color.white);
		wordTTBBtn.setForeground(Color.white);
		ruleBtn.setForeground(Color.white);
		exitBtn.setForeground(Color.white);
				
		eastPanel.revalidate();
		eastPanel.repaint();
	}
	
	// Whenever a player claims a word left/right - this is the dialog box that comes up
	private void ShowVotingDialog(String word) {
		
		// The main JFrame
		JFrame jframe = new JFrame();
		jframe.setBounds(200,200,500,400);
		jframe.getContentPane().setLayout(null);
		jframe.setVisible(true);
		jframe.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		// Display the word the player claims
		JLabel message = new JLabel("Is " + word + " a word?");
		message.setBounds(60, 50, 400, 70);
		jframe.getContentPane().add(message);
		
		votingListArea = new JTextArea();
        votingListArea.setEditable(false);
        votingListArea.setBounds(60, 100, 400, 300);
        votingListArea.setVisible(true);
        
		JScrollPane voteScrollPane = new JScrollPane();
		voteScrollPane.setBounds(60, 100, 300, 200);
        jframe.getContentPane().add(voteScrollPane);
        voteScrollPane.setViewportView(votingListArea);
        
        // Button and listener if we vote Yes
        JButton yesButton = new JButton("YES");
		yesButton.setBounds(60,309,117,29);
		jframe.getContentPane().add(yesButton);
		yesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {	
					server.VoteForWord(clientId, true);
					jframe.dispose();
				} catch (RemoteException e1) {
					e1.printStackTrace();
				} catch (OperationNotAllowedException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		// Button for listener if we vote No
		JButton noButton = new JButton("NO");
		noButton.setBounds(320,309,117,29);
		jframe.getContentPane().add(noButton);
		noButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					server.VoteForWord(clientId, false);
					jframe.dispose();
				} catch (RemoteException e1) {
					
					e1.printStackTrace();
				} catch (OperationNotAllowedException e1) {
					
					e1.printStackTrace();
				}
			}
		});
	}

	// We update the voting dialog to show when a player has voted to all other players
	private void UpdateVotingDialog(GameState state)
	{
		votingListArea.setText("");
		List<Player> players = state.getPlayers();
		playersNotVoted = new ArrayList<Player>();
		
		// Each player gets the current votes
		Vote[] votes = state.getVotingState().getVotes();
		// Array of players who have already voted
		String[] votedPlayerName = new String[votes.length];
	
		// If at least one person has voted then we display 
		// that they have voted to all other players
		if(votes.length>0)
		{
			for (int i =0; i < votes.length; i++)
			{
				votingListArea.append(votes[i].getPlayerName()+ " (Voted)\n");
				votedPlayerName[i] = votes[i].getPlayerName();
			}
			for (Player player: players)
			{
				if(!(Arrays.asList(votedPlayerName).contains(player.getName())))
				{
					playersNotVoted.add(player);
				}
			}
			for (Player playerN: playersNotVoted)
			{
				votingListArea.append(playerN.getName()+" (Not Voted)\n");
			}
		}else
		{
			for (Player player: players)
			{
				votingListArea.append(player.getName()+" (Not Voted)\n");
			}
		}	
	}
	
	// Method to deal with when we end the game via exit button or by passing
	private void startEndGameSequence() {
		handledExit = true;
		JFrame frame = new JFrame("End game");
		frame.setTitle("End game");
		Box box = Box.createVerticalBox();
		frame.getContentPane().add(box);
		
		frame.getContentPane().setBackground(Color.getHSBColor(0.383F, 0.7879F, 0.2475F));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(400, 400));
		
		JLabel title = new JLabel("<html><p>The game has finished.</p><p>"
				+ " Thank you for playing:</p></html>");
		  
		// We check to see if the game ended by a player leaving - informing other players if so
		if (currentState.getLastAction() == GameState.LastAction.UserLeft) {
			title = new JLabel("<html><p>The game has finished because a player has left.</p><p>"
					+ " Thank you for playing:</p></html>");
		}
		
		title.setFont(new Font("SansSeriff", Font.BOLD, 20));
		title.setForeground(Color.RED);
		frame.getContentPane().add(title, BorderLayout.NORTH);

		JLabel top5 = new JLabel("The Top 5 scores!");
		top5.setFont(new Font("SansSeriff", Font.ITALIC, 15));
		top5.setForeground(Color.MAGENTA);
		box.add(top5);
		// Print the top 5 scores.
		int numScores = 5;
		// If there are fewer than 5 players
		if (numScores > currentState.getPlayers().size()) {
		    numScores = currentState.getPlayers().size();
		}
		
		// How we order which player has the highest score - the ranking
		currentState.getPlayers().sort(new Comparator<Player>() {
            public int compare(Player p1, Player p2) 
            {
            	return Integer.compare(p2.getScore(), p1.getScore());
            }
        });
		
		Iterator<Player> playerIterator = currentState.getPlayers().iterator();
		
		// Display the winning players
		for (int i = 0; i < numScores; i ++) {
			Player p = playerIterator.next();
	        JLabel scoreInfo = new JLabel(i+1 + "). Player "
	  		  + p.getName() + " - Scored: " + p.getScore());
		      
		      scoreInfo.setFont(new Font("TimesNewRoman", Font.PLAIN, 15));
		      scoreInfo.setForeground(Color.WHITE);
		      box.add(scoreInfo);
		}
		
	  
	    frame.setLocation(50,50);
	    frame.pack();
	    frame.setVisible(true);
	    
	    passBtn.setEnabled(false);
	    wordLTRBtn.setEnabled(false);
	    wordTTBBtn.setEnabled(false);
	    nextTurnBtn.setEnabled(false);
	    exitBtn.setEnabled(false);
	}

	// Method to update the east, west and board panels.
	// We also check to see if we are in a state of voting or playing
	public void update(GameState state) {
		currentState = state;
		enableNextMoveButtons = currentState.getCurrentPlayerName().equals(playerName) 
				&& state.getState() == GameState.State.Playing;
		westPanelRedraw();
		eastPanelRedraw();
		boardUpdate();
		if (state.getState() == GameState.State.Voting) 
		{
			if (currentState.getLastAction() == LastAction.PlayerPlayed) 
			{
				String word = currentState.getVotingState().getWordForVoting().getContent();
				ShowVotingDialog(word);
				UpdateVotingDialog(state);
				
			} 
			else 
			{
				UpdateVotingDialog(state);
			}
		}
		if (state.getState() == GameState.State.Ended) {
			startEndGameSequence();
		}
	}

	
}
