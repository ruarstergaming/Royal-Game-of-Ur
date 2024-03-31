import java.io.*;
import java.net.Socket;
import java.util.List;

//Class that handles client requests
public class ClientHandler implements Runnable {

    // Attributes
    Socket client; // The socket object used for sending and receiving messages
    Server server; // The server its communicating with
    BufferedReader in; // A reader object for reading in messages from opponents and clients
    PrintWriter out; // A writer object for writing to the server and the client
    int stone; // The stone (aka the player)
    ClientHandler otherClient; // The opponent
    List<ClientHandler> clients; // A list of clients

    /**
     * Basic Constructor for setting up the client, server, stone and client list,
     * as well as the reader and writer
     * 
     * @param server
     * @param client
     * @param stone
     */
    public ClientHandler(Server server, Socket client, int stone) {
        try {
            // Set up attributes
            this.client = client;
            this.stone = stone;
            this.server = server;

            /**
             * Start up the reader and writer objects then print a confirmation to the
             * command line
             */
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);
            System.out.println("ClientHandler " + stone + " is connected");

        } catch (Exception e) {

        }
    }

    @Override
    /**
     * Overridden method for running the client
     */
    public void run() {

        try {
            while (true) {

                // Read in the command from the server
                String command = in.readLine();
                /**
                 * properly sending the message to the opponent in case it is about a dice roll,
                 * a new move made or that the user wants to start a new game
                 */
                if (command.startsWith("DICE") || command.startsWith("MOVE") || command.equals("NEW_GAME")) {

                    sendOpponentMessage(command);
                }
                // Otherwise if it indicates a client has quit and close everything down
                else if (command.equals("QUIT")) {

                    sendOpponentMessage(command);
                    /**
                     * sends message to the Client object that it is associated with so that it will
                     * close everything
                     */
                    out.println(command + "_NOW");
                    closeClientHandler();
                    System.out.println("Closing client handler " + stone);

                    /**
                     * if the user wants to quit the game before the other player has joined, it
                     * will also try to close the server so that the port number the server is
                     * associated with can be used after
                     */
                    if (otherClient == null) {

                        server.serverSocket.close();
                        new MessageWindow("The network game has been closed.");
                    }
                    break;
                }
                /**
                 * Otherwise it indicates somethign wrong has happened and it shuts everything
                 * down
                 */
                else if (command.equals("QUIT_NOW")) {

                    closeClientHandler();
                    System.out.println(stone);
                    System.out.println("Closing client handler " + stone);
                    break;
                }
            }

        } catch (IOException e) {

            System.out.println("IOException: " + e.getMessage());

        } catch (NullPointerException e) {

            /**
             * If an exception occurred with the client then send a quit command to the
             * opponent and close everything down
             */

            System.out.println("ClientHandler" +  stone + " NullPointerException: " + e.getMessage());
            System.out.println(stone);

            out.println("QUIT_NOW");
            sendOpponentMessage("QUIT");
            closeClientHandler();
        }
    }

    /**
     * method for properly closing the Clienthandler object
     */
    public void closeClientHandler() {
        try {
            /**
             * closing the reader/writer plus closing the Socket object that is associated
             * with
             */
            in.close();
            out.close();
            client.close();
        } catch (IOException e) {
            System.out.print("IOException: " + e.getMessage());
        }
    }

    /**
     * This method sends a message to the other player's Client object
     * 
     * @param command represents the command that wil be passed to the other player
     */
    public void sendOpponentMessage(String command) {
        if (otherClient != null) {
            otherClient.out.println("OPP_" + command);
            System.out.println("Message sent to opponent");
        }
    }
}
