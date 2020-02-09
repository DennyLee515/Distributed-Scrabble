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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by DennyLee on 2018/9/25.
 */

public class JoinGameDialog extends JDialog {
    /**
     *
     */
    private static final long serialVersionUID = 6829919962195916558L;

    //Action of Join Dialog
    public enum Action {
        Ok,
        Cancel
    }

    private JPanel portInputPanel;
    private JPanel ipInputPanel;
    private JTextField portTF;
    private JTextField ipTF;
    private JTextField nameTF;
    private String message;
    private Action action = Action.Cancel;

    public JoinGameDialog() {
        this.setTitle("Scrabble-Join a Game");
        this.setBounds(500, 200, 450, 400);


        //notice panel
        JPanel noticePanel = new JPanel();
        noticePanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        this.getContentPane().add(noticePanel, BorderLayout.NORTH);
        noticePanel.setLayout(new GridLayout(3, 1, 0, 10));
        JLabel noticeLabel1 = new JLabel("You are joining a game.");
        noticeLabel1.setFont(new Font("Arial", Font.PLAIN, 14));
        noticeLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        noticePanel.add(noticeLabel1);
        JLabel noticeLabel2 = new JLabel("Please Input the following information.");
        noticeLabel2.setFont(new Font("Arial", Font.PLAIN, 14));
        noticeLabel2.setHorizontalAlignment(SwingConstants.CENTER);
        noticePanel.add(noticeLabel2);

        //input panel
        JPanel inputPanel = new JPanel();
        inputPanel.setBorder(new EmptyBorder(0, 70, 0, 70));
        this.getContentPane().add(inputPanel, BorderLayout.CENTER);
        inputPanel.setLayout(new GridLayout(4, 1, 0, 5));
        //name input panel
        JPanel nameInputPanel = new JPanel();
        nameInputPanel.setLayout(new GridLayout(2, 1, 0, 0));
        inputPanel.add(nameInputPanel);
        JLabel nameInputLabel = new JLabel("Please input your username:");
        nameInputLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        nameInputPanel.add(nameInputLabel);
        nameTF = new JTextField();
        nameInputPanel.add(nameTF);
        nameTF.setColumns(10);
        //IP input panel
        ipInputPanel = new JPanel();
        inputPanel.add(ipInputPanel);
        ipInputPanel.setLayout(new GridLayout(2, 1, 0, 0));
        JLabel ipInputLabel = new JLabel("Please input the IP address:");
        ipInputLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        ipInputPanel.add(ipInputLabel);
        ipTF = new JTextField();
        ipInputPanel.add(ipTF);
        ipTF.setColumns(10);
        //Port input panel
        portInputPanel = new JPanel();
        portInputPanel.setLayout(new GridLayout(2, 1, 0, 0));
        inputPanel.add(portInputPanel);
        JLabel portInputLabel = new JLabel("Please input the PORT number:");
        portInputLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        portInputPanel.add(portInputLabel);
        portTF = new JTextField();
        portInputPanel.add(portTF);
        portTF.setColumns(10);

        //panel for button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        inputPanel.add(buttonPanel);
        JButton btnBack = new JButton("Back");
        buttonPanel.add(btnBack);
        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                action = Action.Cancel;
                setVisible(false);
            }
        });

        //Submit button: Verify data format, transfer data to server
        JButton btnNewButton = new JButton("Submit");
        buttonPanel.add(btnNewButton);
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (nameTF.getText().equals("") || portTF.getText().equals("") || ipTF.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "Please input all information!", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    username = nameTF.getText();
                    if ((portTF.getText().matches("[0-9]*")) && ((ipTF.getText().equals("localhost")) || (ipTF.getText().matches
                            ("((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))")))) {
                        String strPort = portTF.getText();
                        port = Integer.parseInt(strPort);
                        if (port > 65535 || port < 0) {
                            JOptionPane.showMessageDialog(null, "Wrong port number!", "Error", JOptionPane.ERROR_MESSAGE);
                        } else {
                            host = ipTF.getText();
                            setVisible(false);
                            action = Action.Ok;
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid IP address!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    private int port;
    private String host;
    private String username;

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public Action getAction() {
        return action;
    }

    public void Clean() {
        action = Action.Cancel;
        //TODO Show all fields
    }

    public String getUserName() {
        return username;
    }

    public void showErrorMessage(String message) {
        this.message = message;
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

