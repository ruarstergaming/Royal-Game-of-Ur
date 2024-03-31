import java.io.*;
import java.net.Socket;

//This class runs the client for network play
public class Client implements Runnable {

    Socket socket; // Socket object used for sending and receiving messages
    BufferedReader in; // BufferedReader object for reading the messages received
    PrintWriter out; // PrintWriter object for sending messages to the other opponent
    int stone; // represents the type of stone the client is moving on the board
    Game game; // Game object for manipulating the game's internal data

    public Client(Game game, Socket socket, int stone) {

        try {
            // Assign values from the values passed in
            this.socket = socket;
            this.stone = stone;
            this.game = game;
            game.client = this;

            // Set up the reader and writer objects
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            /**
             * catch IOException that might appear and display an apprpriate message
             */
        } catch (IOException e) {

            System.out.println("IOException: " + e.getMessage());
        }
    }

    @Override
    /**
     * Overridden method for running the client that depending on the information
     * the server sends does different things with the game on this clients side.
     */
    public void run() {

        try {

            /**
             * Print the client is running and start an infinite loop (infinite until the
             * program is closed)
             */
            boolean oppLeft = false;
            System.out.println("Client " + stone + " is running");

            while (true) {

                // Read in the information from the server on what the opponent has done
                String command = in.readLine();

                // If the command has OPP_DICE then the opponent has rolled the dice
                if (command.startsWith("OPP_DICE")) {

                    // Print to the command line to confirm that the command has been received
                    System.out.println("Dice command received");

                    // Split up the opponents command and display their dice rolls
                    String[] splitCommand = command.split(" ");
                    String[] oppDiceValues = new String[splitCommand.length - 1];
                    for (int i = 0; i < oppDiceValues.length; i++) {

                        oppDiceValues[i] = splitCommand[i + 1];
                    }

                    game.setDiceValues(oppDiceValues);
                    game.gameWindow.updateDice(game);

                }

                // Otherwise if the command starts with OPP_MOVE then the opponent has moved a
                // piece
                else if (command.startsWith("OPP_MOVE")) {

                    System.out.println("Move command received");
                    String[] splitInput = command.split(" ");

                    /**
                     * Store the information as the position the opponent stone started as and how
                     * far it has moved
                     */
                    int row = Integer.parseInt(splitInput[1]);
                    int column = Integer.parseInt(splitInput[2]);
                    int steps = Integer.parseInt(splitInput[3]);

                    game.setSteps(steps);
                    game.makeMove(row, column, steps);

                }

                // If the opponent started a new game
                else if (command.equals("OPP_NEW_GAME")) {

                    game.refreshGame();
                    System.out.println("Game refreshed");

                }

                // If the command given is to quit
                else if (command.equals("QUIT_NOW")) {

                    closeClient();
                    /**
                     * setting the game's client to be null and the playMode attribute to be equal
                     * to -1 so that when the game's view is update there will be no play moe
                     * selected
                     */
                    game.setClient(null);
                    /**
                     * properly displaying in the Menu which game is selected (if there is any)
                     */
                    if (game.isNetworkGame()) {
                        game.setPlayMode(game.NOPLAYMODE);
                    }
                    game.refreshGame();
                    System.out.println("Client closed");
                    break;

                } else if (command.equals("OPP_QUIT")) {

                    // Again close the readers and sockets and quit the loop
                    out.println("QUIT_NOW");
                    closeClient();

                    /**
                     * setting the game's client to be null and the playMode attribute to be equal
                     * to -1 so that when the game's view is update there will be no play moe
                     * selected
                     */
                    game.setClient(null);
                    game.setPlayMode(game.NOPLAYMODE);
                    game.refreshGame();
                    /**
                     * setting oppLeft to true so that an appropriate message will be displayed to
                     * the user stating the fact that the opponent has left
                     */
                    oppLeft = true;

                    System.out.println("Client closed, opponent has left");
                    break;

                } else if (command.equals("OPP_JOINED")) {

                    /**
                     * display an appropriate message to the user stating the fact that the other
                     * opponent has joined the game
                     */
                    new MessageWindow("Opponent has joined the game.");

                    // refresh the game so that both players have a fresh board
                    game.refreshGame();
                }
            }

            /**
             * If the oppenent left the game show the user a message window telling them
             * that
             */
            if (oppLeft) {
                new MessageWindow("Opponent left the game");
            }

        } catch (IOException e) {

            System.out.println("IOException: " + e.getMessage());
            e.printStackTrace();

        } catch (NullPointerException e) {

            /**
             * If an exception occurs and the connection fails then quit the game and close
             * everything down
             */
            System.out.println("Client NullPointerException: " + e.getMessage());
            out.println("QUIT_NOW");
            closeClient();
            /**
             * setting the game's client to be null and the playMode attribute to be equal
             * to -1 so that when the game's view is update there will be no play moe
             * selected
             */
            game.setClient(null);
            game.setPlayMode(game.NOPLAYMODE);
            game.refreshGame();

            new MessageWindow("Opponent left the game");
        }
    }

    /**
     * Sends a move message to the ClientHandler object that is associated with
     * 
     * @param row,    the row of the stone being moved
     * @param column, the column of the stone being moved
     * @param steps,  how far the stone is moving
     */
    public void sendMoveMessage(int row, int column, int steps) {

        // generate a message and write it out the print writer
        out.println("MOVE " + row + " " + column + " " + steps);
    }

    /**
     * Sends a dice message out for it to get proccessed and send to the
     * ClientHandler object that is associated with
     * 
     * @param diceValues, the values of the dice rolls
     */
    public void sendDiceMessage(String[] diceValues) {

        // Concatenate the dice values and then send out the message
        String allDiceValues = "";
        for (int i = 0; i < diceValues.length; i++) {

            allDiceValues += " " + diceValues[i];
        }
        out.println("DICE" + allDiceValues);
    }

    /**
     * Sends a quit message to the ClientHandler object that is associated with
     */
    public void sendQuitMessage() {

        out.println("QUIT");
    }

    /**
     * Sends a new game message to the ClientHandler object that is associated with
     */
    public void sendNewGameMessage() {

        out.println("NEW_GAME");
    }

    /**
     * Closes down the reader, writer and socket to prevent data loss
     */
    public void closeClient() {
        try {
            in.close();
            out.close();
            socket.close();
            System.out.println("Closing client");
            /**
             * catch any IOException that might appear from the BufferedReader object or
             * from the Socket object
             */
        } catch (IOException e) {
            System.out.print("IOException: " + e.getMessage());
        }
    }

    /**
     * 
     * @return get the type of stone the client is moving on the board
     */
    public int getStone() {
        return stone;
    }
}