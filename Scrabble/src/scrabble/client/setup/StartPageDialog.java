package scrabble.client.setup;

/*
 *  Dean Pakravan: 757389
 *  Qijie Li: 927249 
 *  Luis Zapata: 938907 
 *  Dongming Li: 1002971
 *  Assignment 2: Distributed Systems - Sem2 2018
 */

import scrabble.common.OperationNotAllowedException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;

/**
 * Created by DennyLee on 2018/9/25.
 */
public class StartPageDialog extends JDialog {

	private static final long serialVersionUID = -6212363548658576134L;

	public enum Action {
		NewGame,
		JoinGame,
		NoAction
	}
	
	private Action action = Action.NoAction;

    public  StartPageDialog(){
        this.setTitle("Scrabble");
        this.setBounds(500, 200, 450, 300);

        JPanel textPanel = new JPanel();
        textPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        this.getContentPane().add(textPanel, BorderLayout.NORTH);
        textPanel.setLayout(new GridLayout(3, 1, 0, 10));

        JLabel welcomeLabel = new JLabel("Welcome to the Scrabble Game! ");
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        textPanel.add(welcomeLabel);
        JLabel noticeLabel = new JLabel("Please create a new game or join a game.");
        noticeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        noticeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        textPanel.add(noticeLabel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(new EmptyBorder(0, 100, 0, 100));
        this.getContentPane().add(buttonPanel, BorderLayout.CENTER);
        buttonPanel.setLayout(new GridLayout(3, 2, 0, 10));
        //button for create a game
        JButton btnCreate = new JButton("Create a Game");
        btnCreate.setFont(new Font("Arial", Font.PLAIN, 14));
        buttonPanel.add(btnCreate);
        btnCreate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
            	action = Action.NewGame;
                setVisible(false);
            }
        });
        //button for join a game
        JButton btnJoin = new JButton("Join a Game");
        btnJoin.setFont(new Font("Arial", Font.PLAIN, 14));
        buttonPanel.add(btnJoin);
        btnJoin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	action = Action.JoinGame;
                setVisible(false);
            }
        });

        //listener for close button
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                    action = Action.NoAction;
                    dispose();
            }
        });

    }

	public Action getAction() {
		return action;
	}

	public void Clean() {
		action = Action.NoAction;
	}
}
