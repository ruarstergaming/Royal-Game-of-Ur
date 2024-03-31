import java.io.IOException;
import java.net.*;

//Helps connect the clients to the server
public class NetworkConstructor {

    // Attributes
    Thread t; // The thread between the server and client
    Server server; // The server that the user is connecting to
    String hostname; // The host name
    int portNumber; // The port nuber
    InputWindow inputWindow; // The window where the input comes from

    /**
     * Basic constructor that takes information from the input window
     * 
     * @param inputWindow, the window (containing the host name and port nu.ber)
     */
    public NetworkConstructor(InputWindow inputWindow) {
        this.inputWindow = inputWindow;
        this.hostname = inputWindow.inputPanel.hostname;
        this.portNumber = inputWindow.inputPanel.portNumber;
    }

    /**
     * Build the server
     * 
     * @return boolean, if the server and thread was created correctly or not
     */
    public boolean buildServer() {

        // Create a new server object
        server = new Server(portNumber);

        // If the server has an open socket
        if (server.serverSocket != null) {

            // Create a new thread
            t = new Thread(server);
            t.start();

            // Return true as the thread and server was made successfully
            return true;
        }

        // Otherwise the server wasnt initialised correctly so return false
        return false;
    }

    /**
     * Build the socket for connecting a user to an existing game, returns the
     * socket of the room being connected to
     */
    public Socket buildSocket() {
        try {

            // Create the socket
            Socket socket = new Socket(hostname, portNumber);

            // Close the input window
            inputWindow.dispose();

            // Return the socket
            return socket;
        }
        // If the host wasn't connected to
        catch (UnknownHostException e) {

            closeServer();
            // Display the message that the hostname isnt valid, and return a null socket
            new MessageWindow("The hostname is not a valid one");
            return null;
        }

        catch (ConnectException e) {
            closeServer();
            /**
             * display an appropriate message
             */
            new MessageWindow("There is no server running on this port number");
            return null;
        }

        catch (IOException e) {
            closeServer();
            return null;
        }
    }

    /**
     * Close the server connection
     */
    public void closeServer() {
        /**
         * If the thread exists close it and print a conformation message to the command
         * line
         */
        if (t != null && t.isAlive()) {
            t.interrupt();
            System.out.println("The thread on which the server was running has been interrupted");
            try {
                /**
                 * trying to close the server
                 */
                System.out.println("Trying to close the server");
                server.serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Simple getter method
     * 
     * @return Server, the server object being gotten
     */
    public Server getServer() {
        return server;
    }
}