import javax.swing.JFrame;
import javax.swing.*;

//Class that has the window the game is displayed on and also controls the menu
public class GameWindow extends JFrame {

    // Attributes

    /**
     * An array of radio buttons that represent the different game modes the user can
     * select (local/online multiplayer and singleplayer)
     */
    JRadioButton[] playModes;

    GameDisplay gameDisplay; // The game being displayed on the window
    JMenu menu; // The game's menu
    ButtonGroup playMode; // A group of buttons for displaying the modes
    JMenuItem start, quit; // The start and quit objects
    RollDiceButton rollDiceButton; // A button for rolling the dice
    Timer timerDice; // A timer for how long the dice are rolling
    Timer timerMove; // A timer for how long the move takes

    /**
     * Basic constructor that initalises everything and displays it all
     * 
     * @throws Exception
     */
    public GameWindow() throws Exception {

        // Set the size of the window and set the look and feel
        setSize(1200, 400);
        UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");

        // Display the game board itself
        GameDisplay gameDisplay = new GameDisplay(this);
        this.gameDisplay = gameDisplay;
        add(gameDisplay);

        // Set a menu bar or controlling the modes and rolling the dice
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // Add a menu to the menu bar
        menu = new JMenu("Menu");
        menuBar.add(menu);

        // Set the title and what to do when closing the window
        setTitle("Welcome to Royal Game Of Ur!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add the roll dice button to the menu bar
        rollDiceButton = new RollDiceButton("Roll Dice", this);
        menuBar.add(rollDiceButton);

        // Add the play modes
        playModes = new JRadioButton[3];
        playModes[0] = new JRadioButton("AI Opponent");
        playModes[1] = new JRadioButton("Local Multiplayer");
        playModes[2] = new JRadioButton("Network Game");

        // Group the playmode buttons into one group and add them to the menu bar
        playMode = new ButtonGroup();
        for (int i = 0; i < 3; i++) {

            playMode.add(playModes[i]);
            menu.add(playModes[i]);
        }
        menu.addSeparator();

        // Add a start start and quit buttons
        start = new JMenuItem("Start New Game");
        quit = new JMenuItem("Quit Game");

        menu.add(start);
        menu.addSeparator();

        menu.add(quit);
        menu.addSeparator();

        // Set the layout of the actual winfow
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setVisible(true);
        setResizable(false);
    }

    /**
     * This re-displays everything when the game has been quit/ restarted
     * 
     * @param game, the state of the game (currently restarted)
     */
    public void refreshGame(Game game) {

        // Clear the playmode
        if (game.isNoPlayModeSelected()) {
            playMode.clearSelection();
        }

        // Enable the roll dice button and set the title
        setAppropriateTitle(game);
        setRollDiceButtonEnabled(game);

        // Loop through the game board and display it
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 8; j++) {

                if (i == 0 && j == 4) {

                    gameDisplay.cells[i][j].setButtonVisible(1);
                    gameDisplay.cells[i][j].button.setText(String.valueOf(7));

                } else if (i == 2 && j == 4) {

                    gameDisplay.cells[i][j].setButtonVisible(2);
                    gameDisplay.cells[i][j].button.setText(String.valueOf(7));

                } else {

                    gameDisplay.cells[i][j].setButtonInvisible();
                }
            }
        }

        // Remove the dice cells and display the dice in the correct way
        for (int i = 0; i < 3; i++) {

            gameDisplay.cells[i][8].removeAll();
            gameDisplay.cells[i][8].add(new JLabel());
        }

        // Update the display
        gameDisplay.revalidate();
        gameDisplay.repaint();
    }

    /**
     * Handles all the action listeners for all the buttons
     * 
     * @param listener, the controller that listens for user input
     */
    public void setListener(Controller listener) {

        // Loop through the board and add an action listener for each cell
        for (int i = 0; i < 3; i++) {

            /**
             * This was put here as convientely there is the same amount of play modes as
             * there are rows so for efficiency the playmodes each get and action listener
             * here
             */
            playModes[i].addActionListener(listener);

            /**
             * add the ActionListener object to each RoundButton from the baord
             */
            for (int j = 0; j < 8; j++) {
                gameDisplay.cells[i][j].button.addActionListener(listener);
            }
        }

        /**
         * Add an action listener for the start new game button, quit game button, the
         * roll dice button and also to the timers used for the dice roll animation and
         * move animation
         */
        start.addActionListener(listener);
        quit.addActionListener(listener);
        rollDiceButton.addActionListener(listener);
        timerDice = new Timer(75, listener);
        timerMove = new Timer(100, listener);

    }

    /**
     * Displays the winner message
     * 
     * @param game, the current state of the game
     */
    public void displayWinner(Game game) {

        // If the mode is online
        if (playModes[2].isSelected()) {

            // Check if red won
            if (game.getWinner() == game.REDSTONE) {

                // Check which player it is such that the program will display an appropriate
                // message
                if (game.getClient().getStone() == game.REDSTONE) {

                    new MessageWindow("You won!");

                } else {

                    new MessageWindow("You lost!");

                }
            }

            // Otherwise green won
            else {

                // Check which player it is such that the program will display an appropriate
                // message
                if (game.getClient().getStone() == game.REDSTONE) {

                    new MessageWindow("You lost!");

                } else {

                    new MessageWindow("You won!");

                }
            }
        }

        // Otherwise if the mode is local multiplayer
        else if (playModes[1].isSelected()) {

            // Depenednign on who won display the appropriate message
            if (game.getWinner() == game.REDSTONE) {

                new MessageWindow("Red won!");

            }

            else {

                new MessageWindow("Green won!");

            }
        }

        /**
         * Otherwise if the mode is single player then display the appropriate message
         * depending on whether they lost or won
         */
        else if (playModes[0].isSelected()) {

            if (game.getWinner() == game.REDSTONE) {
                new MessageWindow("You won!");
            }

            else {
                new MessageWindow("You lost!");
            }
        }
    }

    /**
     * Decides whether the dice button should be available to press and makes it
     * available/unavailable
     * 
     * @param game, current state of the game
     */
    public void setRollDiceButtonEnabled(Game game) {

        // If the mode is online
        if (playModes[2].isSelected()) {

            // If its the clients turn then enable the button
            if (game.getClient() != null && game.getCurrentStone() == game.getClient().getStone()) {
                rollDiceButton.setEnabled(true);

            }

            // If its not the clients turn then disable the button
            else if (game.client != null && game.getCurrentStone() != game.getClient().getStone()) {
                rollDiceButton.setEnabled(false);
            }
        }

        // Otherwise if it's the singleplayer mode
        else if (playModes[0].isSelected()) {

            // If its the players turn then enable the button
            if (game.getCurrentStone() == game.REDSTONE) {
                rollDiceButton.setEnabled(true);
            }

            // If its not the players turn then disable the button
            else {
                rollDiceButton.setEnabled(false);
            }
        }

        // Otherwise its local multiplayer so the button should always be available
        else {
            rollDiceButton.setEnabled(true);
        }
    }

    /**
     * Sets the title of the window to the default 'Royal Game Of Ur'
     */
    public void setDefaultTitle() {
        setTitle("Royal Game Of Ur");
    }

    /**
     * Set the title to whoever's turn it is
     * 
     * @param game
     */
    public void setTitleCurrentPlayer(Game game) {

        // If the mode is local/singleplayer then display the appropriate method
        if (playModes[1].isSelected()) {

            // If it's red or green's turn display the appropriate title
            if (game.getCurrentStone() == game.REDSTONE) {
                setTitle("Red plays");
            }

            else {
                setTitle("Green plays");
            }
        } else if (playModes[0].isSelected()) {

            if (game.getCurrentStone() == game.REDSTONE) {
                setTitle("Your turn to move a red stone");

            } else {
                setTitle("Computer's turn to move a green stone");
            }
        }
        /**
         * Otherwise if the mode is online desplay the appropriate title for the
         * stiuattion
         */
        else if (playModes[2].isSelected()) {

            // If there is a connection
            if (game.getClient() != null) {

                // If its the players turn
                if (game.getCurrentStone() == game.getClient().getStone()) {

                    // If the client is red then display the appropriate message
                    if (game.getClient().getStone() == game.REDSTONE) {

                        // Check if the opponent is connected
                        if (game.getConstrutor().getServer().getSecondClient() != null) {

                            setTitle("Your turn to move a red stone");
                        }

                        else {
                            setTitle("Waiting for opponent to join");
                        }
                    }

                    else {
                        setTitle("Your turn to move a green stone");
                    }

                }

                // Otherwise its the opponents turn
                else {
                    setTitle("Waiting for opponent's turn");
                }
            }

            // If all else is untrue set the default title
            else {
                setDefaultTitle();
            }
        }
    }

    /**
     * Call the correct method for setting the default title
     * 
     * @param game
     */
    public void setAppropriateTitle(Game game) {

        /**
         * If the game hasn't started then set the default title, otherwise set the
         * title of the player turn
         */
        if (playMode.getSelection() != null) {
            setTitleCurrentPlayer(game);
        }

        else {
            setDefaultTitle();
        }
    }

    /**
     * Updates the move timer
     * 
     * @param game
     */
    public void updateMove(Game game) {

        if (!timerMove.isRunning()) {
            timerMove.start();

        } else {
            gameDisplay.moveStone(game);
        }
    }

    /**
     * Updates the dice timer
     * 
     * @param game
     */
    public void updateDice(Game game) {

        if (!timerDice.isRunning()) {
            timerDice.start();

        } else {
            rollDiceButton.rollDice(game);
        }

    }
}
