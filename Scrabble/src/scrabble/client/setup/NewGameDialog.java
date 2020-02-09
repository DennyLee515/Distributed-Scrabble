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
public class NewGameDialog extends JDialog {
    /**
     *
     */
    private static final long serialVersionUID = 9121632611028545346L;

    //action of new game dialog
    public enum Action {
        Ok,
        Cancel
    }

    private Action action = Action.Cancel;
    private JTextField portTF;
    private JTextField nameTF;

    public NewGameDialog() {
        this.setTitle("Scrabble-Create a Game");
        // this.setBounds(200, 200, 450, 300);
        this.setBounds(500, 200, 450, 350);
        //Notice panel
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));
        this.getContentPane().add(panel, BorderLayout.NORTH);
        panel.setLayout(new GridLayout(3, 1, 0, 10));
        JLabel noticeLabel1 = new JLabel("You are creating a new game.");
        noticeLabel1.setFont(new Font("Arial", Font.PLAIN, 14));
        noticeLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(noticeLabel1);
        JLabel noticeLabel2 = new JLabel("Please Input the Port number to create a game.");
        noticeLabel2.setFont(new Font("Arial", Font.PLAIN, 14));
        noticeLabel2.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(noticeLabel2);

        //Input Panel
        JPanel inputPanel = new JPanel();
        inputPanel.setBorder(new EmptyBorder(0, 70, 0, 70));
        this.getContentPane().add(inputPanel, BorderLayout.CENTER);
        //inputPanel.setLayout(new GridLayout(3, 1, 0, 5));
        inputPanel.setLayout(new GridLayout(4, 1, 0, 5));
        JLabel portInputLabel = new JLabel("Please input the PORT number:");
        portInputLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        inputPanel.add(portInputLabel);
        portTF = new JTextField();
        inputPanel.add(portTF);
        portTF.setColumns(10);

        //name input panel
        JLabel nameInputLabel = new JLabel("Please input your username:");
        nameInputLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        inputPanel.add(nameInputLabel);
        nameTF = new JTextField();
        inputPanel.add(nameTF);
        nameTF.setColumns(10);

        //button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        //back button
        JButton btnBack = new JButton("Back");
        buttonPanel.add(btnBack);
        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                action = Action.Cancel;
                setVisible(false);
            }
        });
        //Submit button:Submit button: Verify data format, transfer data to server
        JButton btnSubmit = new JButton("Submit");
        buttonPanel.add(btnSubmit);
        btnSubmit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (portTF.getText().equals("") || nameTF.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "Please input port number and username", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (portTF.getText().matches("[0-9]*")) {
                    String strPort = portTF.getText();
                    port = Integer.parseInt(strPort);
                    username = nameTF.getText();
                    if (port > 65535 || port < 0) {
                        JOptionPane.showMessageDialog(null, "Wrong port number", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        action = Action.Ok;
                        setVisible(false);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

    }

    private int port;
    private String username;

    public int getPort() {
        return port;
    }

    public String getUsername() { return username;}

    public Action getAction() {
        return action;
    }

    public void Clean() {
        action = Action.Cancel;
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
