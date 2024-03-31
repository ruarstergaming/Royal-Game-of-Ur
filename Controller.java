import java.awt.event.*;
import java.net.Socket;

//This is the main controller that helps the UI elements and Game elements communicate
public class Controller implements ActionListener {

    // Attributes
    GameWindow gameWindow; // The window where the game will be
    InputWindow inputWindow; // The window where the user inputs the connection for the network play
    Game game; // The state of the game
    NetworkConstructor constructor; // The constructor for the network play
    Ai AIOpponent; // The Ai opponent for singleplayer

    /**
     * Basic contructor that links the game windoe and the game
     * 
     * @param gameWindow
     * @param game
     */
    public Controller(GameWindow gameWindow, Game game) {

        this.gameWindow = gameWindow;
        this.gameWindow.setListener(this);
        this.game = game;
        this.AIOpponent = new Ai(game);
        /**
         * setting this Controller object to be the Actionlistener to all the buttons
         * from any instance created of the MessageWindow, InputWindow and
         * IntroMessageWindow classes
         */
        MessageWindow.setListener(this);
        InputWindow.setListener(this);
        IntroMessageWindow.setListener(this);
        /**
         * display an intro message to the user offering a brief explanation of how the
         * game works
         */
        new IntroMessageWindow("Welcome to our Royal Game Of Ur!");
    }

    @Override
    /**
     * Overidden methos that listens for user input and depending on what the input
     * was directs the input to the appropriate part of the program
     */
    public void actionPerformed(ActionEvent e) {

        // If a round button was clicked get its source and store it
        if (e.getSource() instanceof RoundButton) {

            if (!gameWindow.timerMove.isRunning() && !gameWindow.timerDice.isRunning()) {
                roundButtonClicked((RoundButton) e.getSource());
            }
        }

        // If the start button was pressed
        else if (e.getSource().equals(gameWindow.start)) {

            game.refreshGame();
            if (game.isNetworkGame() && game.getClient() != null) {
                game.getClient().sendNewGameMessage();
            }
        }

        // Otherwise if the roll dice button button was pressed then trigger the roll
        // the dice method
        else if (e.getSource().equals(gameWindow.rollDiceButton)) {
            rollDiceButtonClicked();
        }

        // Otherwise if the game mode radio button was changed to singleplayer
        else if (e.getSource().equals(gameWindow.playModes[0])) {

            // If the game was online then reset the game and send a quit message to the
            // opponent
            if (game.getClient() != null && game.isNetworkGame()) {
                game.setPlayMode(game.AIOPPONENT);
                game.refreshGame();
                game.getClient().sendQuitMessage();
            }

            // Otherwise if the game was two player or not set then reset the game mode and
            // refresh the game
            else if (game.isLocalMultiplayer() || game.isNoPlayModeSelected()) {
                game.setPlayMode(game.AIOPPONENT);
                game.refreshGame();
            }
        }

        // Otherwise if the game mode radio button was changed to local multiplayer
        else if (e.getSource().equals(gameWindow.playModes[1])) {

            // If there was already an online game in progress then quit it and send a quit
            // message to the oponent
            if (game.getClient() != null && game.isNetworkGame()) {
                game.setPlayMode(game.LOCALMULTIPLAYER);
                game.refreshGame();
                game.getClient().sendQuitMessage();
            }

            // Otherwise just reset the game mode and refresh the game to reflect that
            else if (game.isAIOpponent() || game.isNoPlayModeSelected()) {
                game.setPlayMode(game.LOCALMULTIPLAYER);
                game.refreshGame();
            }
        }

        // Otherwise if the game mode radio button was changed to online multiplayer
        else if (e.getSource().equals(gameWindow.playModes[2])) {

            // if the game wasnt already online then set up the game online
            if (game.getClient() == null) {

                // game.playMode = 2;
                game.setPlayMode(game.NETWORKGAME);
                game.refreshGame();
                inputWindow = new InputWindow();
                /**
                 * if the Client attribute from the Game class is still null it means that no
                 * network connection has been created and the game's view will be updated such
                 * that there will be no play mode selected
                 */
                if (game.getClient() == null) {
                    System.out.println("Client is null");
                    game.setPlayMode(game.NOPLAYMODE);
                    game.refreshGame();
                }
            }
        }

        // Otherwise if the quit button was pressed
        else if (e.getSource().equals(gameWindow.quit)) {

            // If the game was online send a quit message to the opponent
            if (game.isNetworkGame() && game.getClient() != null) {
                game.getClient().sendQuitMessage();
            }

            // Otherwise reset the game
            else {
                game.setPlayMode(game.NOPLAYMODE);
                game.refreshGame();
            }
        }

        // Otherwise if the online player times out when making a move
        else if (e.getSource().equals(gameWindow.timerMove)) {
            gameWindow.updateMove(game);

            if (!gameWindow.timerMove.isRunning()) {
                /**
                 * reset the attirbutes used for creating the move animation and the dice
                 * animation
                 */
                game.resetValues();
                /**
                 * verify if there is a winner in which case display a MessageWindow with an
                 * appropriate message
                 */
                if (game.verifyWinner()) {
                    /**
                     * refresh the game if one of the players has won
                     */
                    game.refreshGame();
                }

                else {
                    /**
                     * if the user is playing against an AI Opponent and it is its turn to roll the
                     * dice the program will do this automatically
                     */
                    if (game.isAIOpponent() && game.getCurrentStone() == game.GREENSTONE) {
                        System.out.println("AI opponent rolls the dice");
                        rollDiceButtonClicked();
                    }
                }
            }
        }

        // Otherwise if the online player times out when rolling a dice
        else if (e.getSource().equals(gameWindow.timerDice)) {
            gameWindow.updateDice(game);
            /**
             * if the user is playing against the AI Opponnet and it is its turn to move,
             * then the program will get the coordinates of the stone that the AI Opponent
             * wants t move and will perform the move
             */
            if (!gameWindow.timerDice.isRunning() && game.isAIOpponent() && game.currentStone == game.GREENSTONE) {
                /**
                 * creating a delay of 300 milliseconds between the animation for the dice roll
                 * and the animation for the move such that the user will be able to acknoledge
                 * what dice roll the AI opponent had before actually making the move. This
                 * feature does not bring any new functionalities to the program, its only
                 * purpose is to imporve the user experience
                 */
                try {
                    Thread.sleep(300);
                } catch (InterruptedException ie) {
                    System.out.println("InterruptedException: " + ie.getMessage());
                }

                System.out.println("Getting AI opponent's next move");
                int[] coordinates = AIOpponent.getMove(game.getSteps());

                System.out.println("AI opponent makes move");
                game.makeMove(coordinates[0], coordinates[1], game.getSteps());
            }

        }

        // Otherwise if the create room button was pressed
        else if (inputWindow != null && e.getSource().equals(inputWindow.createRoomButton)) {
            inputWindow.createOrJoinRoomPanel("Creating Room");

        }

        // Otherwise if the join room button was pressed
        else if (inputWindow != null && e.getSource().equals(inputWindow.joinRoomButton)) {
            inputWindow.createOrJoinRoomPanel("Joining Room");

        }

        // Otherwise if the back button is pressed
        else if (inputWindow != null && e.getSource().equals(inputWindow.inputPanel.backButton)) {
            /**
             * returns back to the previous panel that let's the user to choose between
             * creating a network game, joining another one or closing the window
             */
            inputWindow.goBack();

        }

        // Otherwise if the ok button is pressed
        else if (inputWindow != null && e.getSource().equals(inputWindow.inputPanel.okButton)) {

            // Check the port number is valid
            if (inputWindow.inputPanel.processInput()) {

                // Construct a connection
                game.createNetworkConstrutor(inputWindow);
                Socket socket = null;
                System.out.println(
                        "Port number and hostname properly processed trying to create connection to a network game");
                /**
                 * if the stone attribute from the InputPanel class is equal to 1 then it means
                 * that the user wants to create a network game
                 */
                if (inputWindow.inputPanel.stone == game.REDSTONE) {
                    System.out.println("Trying to create first player");

                    /**
                     * creating the server which will be used to connect the two players between
                     * themselves
                     */
                    if (game.getConstrutor().buildServer()) {

                        System.out.println("Server properly created trying to create Socket object");
                        /**
                         * if the server has been properly created the program will also try to create
                         * the Socket object corresponding to the first player
                         */
                        socket = game.getConstrutor().buildSocket();

                    }
                }
                /**
                 * if the stone attribute from the InputPanel class is equal to 2 then it means
                 * that the user wants to join a network game
                 */
                else if (inputWindow.inputPanel.stone == game.GREENSTONE) {
                    /**
                     * creating the Socket object corresponding to the player that wants to join a
                     * network game
                     */
                    System.out.println("Trying to create the second player");
                    System.out.println("Trying to create Socket object");
                    socket = game.getConstrutor().buildSocket();
                }
                // If the socket was built correctly try to connect to the host
                if (socket != null) {
                    /**
                     * creating a Client object and running its run() method
                     */
                    System.out.println("Socket object properly created, trying to create Client object");
                    new Client(game, socket, inputWindow.inputPanel.stone);
                    new Thread(game.getClient()).start();
                    game.refreshGame();

                } else {

                    System.out.println("Socket object has not been properly created");
                }
            }
        }

        /**
         * Otherwise if the button being pressed is the close button just close the
         * window and reset the game
         */
        else if (e.getSource() instanceof CloseButton) {

            /**
             * CloseButton object whose only purpose is to dispose of the JDialog they are
             * associated with, they do not perform any manipulation on the game's data.
             */
            ((CloseButton) e.getSource()).closeWindow();
            /**
             * if the user is playing a online game the program will also refresh their
             * board
             */
            if (game.getClient() != null && game.isNetworkGame()) {
                game.refreshGame();
                game.getClient().sendNewGameMessage();
            }
        }
    }

    /**
     * Rolls the dice
     */
    public void rollDiceButtonClicked() {

        // Checks if a valid game is being run
        if (!game.isNoPlayModeSelected()) {

            // Roll the dice
            game.rollDice();

            // If its online send a roll dice message to the opponent
            if (game.isNetworkGame()) {
                game.client.sendDiceMessage(game.diceValues);
            }
        } else {

            new MessageWindow("You must select a play mode first!");
        }
    }

    /**
     * Performs the move being made when a button is clicked
     * 
     * @param roundButton, the button being clicked
     */
    public void roundButtonClicked(RoundButton roundButton) {

        // If the dice have been rolled
        if (!gameWindow.rollDiceButton.isEnabled() && game.getSteps() != -1) {

            // Find the steps, row and column then make the move
            int steps = game.getSteps();
            int row = roundButton.getRow();
            int column = roundButton.getColumn();

            /**
             * if the move is legal then the progam will manipulate the game's internal data
             * in an appropriate way and will request the GameWindow object to update the
             * board's view
             */
            if (game.makeMove(row, column, steps)) {
                
                /**
                 * if the user is playing an online game it will also send an approrpiate
                 * message to their opponent so that the move will also be displayed on their
                 * board
                 */
                if (game.isNetworkGame() && (game.getMovingStone() == game.getClient().getStone())) {
                    game.getClient().sendMoveMessage(row, column, steps);
                }
            }
        }
    }
}
