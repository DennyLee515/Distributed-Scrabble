package scrabble.client;

/*
 *  Dean Pakravan: 757389
 *  Qijie Li: 927249 
 *  Luis Zapata: 938907 
 *  Dongming Li: 1002971
 *  Assignment 2: Distributed Systems - Sem2 2018
 */

import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import javax.swing.JFrame;

import scrabble.client.gui.GUI;
import scrabble.client.setup.JoinGameDialog;
import scrabble.client.setup.NewGameDialog;
import scrabble.client.setup.StartPageDialog;
import scrabble.client.setup.WaitForPlayersDialog;
import scrabble.common.GameState;
import scrabble.common.GameState.LastAction;
import scrabble.common.IScrabbleServer;
import scrabble.common.OperationNotAllowedException;
import scrabble.common.Utilities;
import scrabble.server.Server;

public class ClientApp implements IClientNotifierObserver {

    public static void main(String args[]) {

        ClientApp app = new ClientApp();
        app.start();
    }

    private final StartPageDialog startPageWindow;
    private final NewGameDialog newGameWindow;
    private final JoinGameDialog joinGameWindow;
    private final WaitForPlayersDialog waitForPlayersWindow;
    private GUI gameWindow;

    //initiate the game
    public ClientApp() {
        startPageWindow = new StartPageDialog();
        startPageWindow.setModal(true);

        newGameWindow = new NewGameDialog();
        newGameWindow.setModal(true);

        joinGameWindow = new JoinGameDialog();
        joinGameWindow.setModal(true);

        waitForPlayersWindow = new WaitForPlayersDialog();
        waitForPlayersWindow.setModal(true);

    }

    //game status
    private enum Status {
        SettingUpGame,
        JoiningGame,
        Playing,
        Exit
    }

    private Status status;
    private boolean iAmTheHost = false;
    private ClientNotifier clientNotifier;
    private Server server;
    private String clientId;
    private String clientName;
    private IScrabbleServer service;


    public void start() {
        status = Status.SettingUpGame;
        boolean exit = false;
        String host = null;
        int port = 0;

        while (!exit) {

            switch (status) {
                //initial status is SettingUpGame, show start page window
                case SettingUpGame:
                    iAmTheHost = false;
                    host = "localhost";
                    port = 0;

                    startPageWindow.Clean();
                    startPageWindow.setVisible(true);
                    StartPageDialog.Action action = startPageWindow.getAction();

                    switch (action) {
                        //when the player click create a game, create a game and change the status to JoiningGame
                        case NewGame:
                            newGameWindow.Clean();
                            newGameWindow.setVisible(true);
                            //if the player clicks submit, try to create a game
                            if (newGameWindow.getAction() == NewGameDialog.Action.Ok) {
                                port = newGameWindow.getPort();
                                clientName = newGameWindow.getUsername();
                                //call startServer method to start a server. if failed, back to the SettingUpGame status
                                if (startServer(port)) {
                                    iAmTheHost = true;
                                    status = Status.JoiningGame;
                                } else {
                                    status = Status.SettingUpGame;
                                }
                            } else {
                                status = Status.SettingUpGame;
                            }
                            break;
                        //when a player click join a game, change the status to JoiningGame
                        case JoinGame:
                            joinGameWindow.Clean();
                            joinGameWindow.setVisible(true);

                            //get input information, if not successful, back to SettingUpGame status
                            if (joinGameWindow.getAction() == JoinGameDialog.Action.Ok) {
                                host = joinGameWindow.getHost();
                                port = joinGameWindow.getPort();
                                clientName = joinGameWindow.getUserName();
                            } else {
                                status = Status.SettingUpGame;
                            }
                            status = Status.JoiningGame;
                            break;

                        default:
                            status = Status.Exit;
                            break;
                    }

                    break;
                //When the status is JoiningGame
                case JoiningGame:
                    if (status == Status.JoiningGame) {
                        clientId = joinServer(host, port, clientName);
                        if (clientId != null) {
                            waitForPlayersWindow.Clean();
                            //set up the waiting page for different player
                            waitForPlayersWindow.configure(clientId, clientName, iAmTheHost, service);
                            waitForPlayersWindow.setVisible(true);
                            switch (waitForPlayersWindow.getAction()) {
                                case Ok:
                                    status = Status.Playing;
                                    // TODO: Handle this inside the window instead
                                    try {
                                        service.StartGame(clientId);
                                    } catch (RemoteException | OperationNotAllowedException e) {
                                        waitForPlayersWindow.showErrorMessage(e.getMessage());
                                    }
                                    break;
                                case GameStarted:
                                    status = Status.Playing;
                                    break;
                                case Cancel:
                                default:
                                    status = Status.Exit;
                                    break;
                            }
                        } else {
                            status = Status.SettingUpGame;
                            waitForPlayersWindow.showErrorMessage("Cannot join a game, please try again!");
                        }
                    }

                    break;
                case Playing:
                    //ShowGameForm();
                    break;
                case Exit:
                default:
                	if (clientNotifier != null) {
                		clientNotifier.removeObserver(this);
                	}
                    exit = true;
                    System.exit(0);
                    break;
            }
        }

    }

    //when a player is creating a game, call this method to start server
    private boolean startServer(int port) {
        server = new Server(port);
        try {
            server.start();
            return true;
        } catch (RemoteException | UnknownHostException e) {
            newGameWindow.showErrorMessage(e.getMessage());
            return false;
        }
    }

    //when a player is joining a game, call this method
    private String joinServer(String host, int port, String clientName) {
        try {
            service = (IScrabbleServer) Naming.lookup(
                    "rmi://" + host + ":" + port + "/" + Utilities.SERVER_NAME);
            clientNotifier = new ClientNotifier();
            clientNotifier.addObserver(this);
            String clientId = service.JoinGame(clientName, clientNotifier);
            return clientId;
            //service.StartGame(clientId);
        } catch (MalformedURLException | RemoteException | NotBoundException | OperationNotAllowedException e) {
            waitForPlayersWindow.showErrorMessage(e.getMessage());
            return null;
        }
    }

    //when the status changed, call this method to revalidate all player's interface
    @Override
    public void update(GameState state) {
        boolean handled = false;
        GameState.State currentState = state.getState();
        GameState.LastAction lastAction = state.getLastAction();

        switch (currentState) {
            case Joining:
                if (lastAction == LastAction.GameStarted || lastAction == LastAction.UserJoined
                        || lastAction == LastAction.UserInvited
                        || lastAction == LastAction.InvitationAccepted
                        || lastAction == LastAction.InvitationRejected
                        || lastAction == LastAction.UserLeft
                        || lastAction == LastAction.ServerLeft) {
                    waitForPlayersWindow.update(state.getPlayers(), lastAction);
                    handled = true;
                }
                break;

            case Playing:
                if (lastAction == LastAction.GameStarted) {
                    int clientsNumber = state.getPlayers().size();
                    waitForPlayersWindow.closeGameStarted();
                    ShowGameForm(clientsNumber);
                }
                UpdateGameForm(state);
                handled = true;
                break;
            case Voting:
                UpdateGameForm(state);
                handled = true;
                break;
            case Ended:
                UpdateGameForm(state);
                handled = true;
                break;
            default:
                break;
        }

        if (!handled) {
            //TODO: Supossed to be invalid??? what to do?
        }
    }

    private void UpdateGameForm(GameState state) {
        gameWindow.update(state);
    }

    private void ShowGameForm(int clientsNumber) {
        gameWindow = new GUI();
        gameWindow.configure(clientsNumber, clientId, clientName, service);
        gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
