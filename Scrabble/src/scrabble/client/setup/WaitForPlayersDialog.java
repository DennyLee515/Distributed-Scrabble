package scrabble.client.setup;

/*
 *  Dean Pakravan: 757389
 *  Qijie Li: 927249 
 *  Luis Zapata: 938907 
 *  Dongming Li: 1002971
 *  Assignment 2: Distributed Systems - Sem2 2018
 */

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import scrabble.common.GameState;
import scrabble.common.IScrabbleServer;
import scrabble.common.InvitationStatus;
import scrabble.common.OperationNotAllowedException;
import scrabble.common.Player;
import java.awt.*;
import java.awt.event.*;
import java.rmi.RemoteException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by DennyLee on 2018/9/25.
 */
public class WaitForPlayersDialog extends JDialog {
    /**
     *
     */
    private static final long serialVersionUID = 9121632611028545346L;

    public enum Action {
        Ok,
        Cancel,
        GameStarted
    }

    public Action action = Action.Cancel;
    private JButton btnInvite ;
    private JButton btnStart;
    private JTextArea roomListArea;
    private JTextArea lobbyListArea;
    private IScrabbleServer service;
    private String clientId;
    private String currentPlayerName;
    private JPanel invitationPanel;

    public WaitForPlayersDialog(){
        this.setTitle("Waiting for Players ");
        this.setBounds(500, 200, 660, 375);

        //Notice panel
        JPanel noticePanel = new JPanel();
        noticePanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        this.getContentPane().add(noticePanel, BorderLayout.NORTH);
        noticePanel.setLayout(new GridLayout(2, 1, 0, 10));

        JLabel waitingNoticeLabel = new JLabel("You are waiting for a game.");
        waitingNoticeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        waitingNoticeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        noticePanel.add(waitingNoticeLabel);

        JPanel roomNamePanel = new JPanel();
        noticePanel.add(roomNamePanel);
        roomNamePanel.setLayout(new GridLayout(1, 2, 0, 0));

        JLabel roomLabel = new JLabel("Game Room");
        roomLabel.setHorizontalAlignment(SwingConstants.CENTER);
        roomNamePanel.add(roomLabel);
        JLabel lobbyLabel = new JLabel("Game Lobby");
        lobbyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        roomNamePanel.add(lobbyLabel);

        //Text Area Panel
        JPanel textAreaPanel = new JPanel();
        textAreaPanel.setBorder(new EmptyBorder(5, 50, 0, 50));
        this.getContentPane().add(textAreaPanel, BorderLayout.CENTER);
        textAreaPanel.setLayout(new GridLayout(1, 1, 0, 5));
        //Game room list
        JPanel roomListPanel = new JPanel();
        roomListPanel.setBorder(new EmptyBorder(0, 0, 0, 30));
        textAreaPanel.add(roomListPanel);
        roomListPanel.setLayout(new GridLayout(0, 1, 0, 0));
        JScrollPane roomScrollPane = new JScrollPane();
        roomListPanel.add(roomScrollPane);
        roomListArea = new JTextArea();
        roomListArea.setEditable(false);
        roomScrollPane.setViewportView(roomListArea);
        //Game lobby list
        JPanel lobbyListPanel = new JPanel();
        lobbyListPanel.setBorder(new EmptyBorder(0, 30, 0, 0));
        textAreaPanel.add(lobbyListPanel);
        lobbyListPanel.setLayout(new GridLayout(0, 1, 0, 0));
        JScrollPane lobbyScrollPane = new JScrollPane();
        lobbyListPanel.add(lobbyScrollPane);
        lobbyListArea = new JTextArea();
        lobbyListArea.setEditable(false);
        lobbyScrollPane.setViewportView(lobbyListArea);

        //Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(new EmptyBorder(20, 80, 20, 80));
        buttonPanel.setLayout(new GridLayout(2, 1, 160, 0));
        this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        //invitation Panel
        invitationPanel = new JPanel();
        buttonPanel.add(invitationPanel);
        invitationPanel.setLayout(new GridLayout(0, 2, 0, 0));
        invitationPanel.setVisible(true);//todo:remove this line
        //mesage
        JLabel inviMessageLabel = new JLabel("You are invited to the game!");
        invitationPanel.add(inviMessageLabel);
        //invitation button panel
        JPanel invitationBtnPanel = new JPanel();
        invitationPanel.add(invitationBtnPanel);
        invitationBtnPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        //join button
        JButton Join = new JButton("Join");
        Join.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    service.AcceptInvitation(clientId,true);
                } catch (RemoteException | OperationNotAllowedException e1) {
                    showErrorMessage(e1.getMessage());
                    e1.printStackTrace();
                }
            }
        });
        invitationBtnPanel.add(Join);
        //leave button
        JButton btnLeave = new JButton("Leave");
        btnLeave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    service.AcceptInvitation(clientId,false);
                } catch (RemoteException | OperationNotAllowedException e1) {
                    showErrorMessage(e1.getMessage());
                    e1.printStackTrace();
                }
            }
        });
        invitationBtnPanel.add(btnLeave);

        //action panel
        JPanel actionBtnPanel = new JPanel();
        buttonPanel.add(actionBtnPanel);
        actionBtnPanel.setLayout(new GridLayout(0, 2, 120, 20));
        //invite button
        btnInvite = new JButton("Invite");
        actionBtnPanel.add(btnInvite);
        btnInvite.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String[] playerNames = GetPlayerNotInRoomNames();
                if (playerNames != null && playerNames.length > 0) {
                    try {
                        service.InvitePlayers(clientId, playerNames);
                    } catch (RemoteException | OperationNotAllowedException e1) {
                        showErrorMessage(e1.getMessage());
                        e1.printStackTrace();
                    }
                }
            }
        });
        //start button
        btnStart = new JButton("Start");
        actionBtnPanel.add(btnStart);
        btnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                action = Action.Ok;
                setVisible(false);

            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    service.LeaveGameRoom(clientId);
                    action = Action.Cancel;
                } catch (RemoteException  | OperationNotAllowedException e1) {
                    e1.printStackTrace();
                    showErrorMessage(e1.getMessage());
                }
            }
        });
    }

    public Action getAction() {
        return action;
    }

    public void Clean() {
        action = Action.Cancel;
        playersInRoom = null;
        playersNotInRoom = null;
    }

    private List<Player> playersInRoom;
    private List<Player> playersNotInRoom;

    private String[] GetPlayerNotInRoomNames() {
        if (playersNotInRoom != null) {
            List<String> pir = playersNotInRoom.stream().map(x -> x.getName()).collect(Collectors.toList());
            return pir.toArray(new String[pir.size()]);
        }
        return null;
    }

    public void update(List<Player> players, GameState.LastAction lastAction) {
        roomListArea.setText("");
        lobbyListArea.setText("");

        playersInRoom = players.stream()
                .filter(p -> p.getInvitationStatus() == InvitationStatus.InvitationAccepted)
                .collect(Collectors.toList());
        playersNotInRoom = players.stream()
                .filter(p -> p.getInvitationStatus() != InvitationStatus.InvitationAccepted)
                .collect(Collectors.toList());

        for (Player player: playersInRoom) {
            roomListArea.append(player.getName()+ " (Accepted)\n");
            invitationPanel.setVisible(false);
        }

        for (Player player : playersNotInRoom) {
            boolean isInvited = player.getInvitationStatus() == InvitationStatus.Invited;
            String str = isInvited ? " (Invited)" : "";
            lobbyListArea.append(player.getName() + str + "\n");
            if (isInvited && player.getName().equals(currentPlayerName)) {
                setInvitePanel();
            }
        }

        if(lastAction == GameState.LastAction.ServerLeft){
            showErrorMessage("The host has left, please join another game!");
            dispose();
        }
        if (playersInRoom.size() < 2) {
            btnStart.setEnabled(false);
        }
        else {
            btnStart.setEnabled(true);
        }
        revalidate();
    }

    private void setInvitePanel() {
        invitationPanel.setVisible(true);
    }

    public void showErrorMessage(String message){
        JOptionPane.showMessageDialog(null,message,"Error",JOptionPane.ERROR_MESSAGE);
    }

    public void closeGameStarted() {
        action = Action.GameStarted;
        setVisible(false);
    }

    //when player is joining a game, call this function to set up waiting page
    public void configure(String clientId, String currentPlayerName, boolean iAmTheHost, IScrabbleServer service) {
        this.clientId = clientId;
        this.service = service;
        this.currentPlayerName = currentPlayerName;
        if(!iAmTheHost){
            btnInvite.setVisible(false);
            btnStart.setVisible(false);
        }
    }
}

