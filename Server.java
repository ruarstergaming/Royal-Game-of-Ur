import java.io.IOException;
import java.net.*;

//Class that runs the server allowing users to host games and communicate
public class Server implements Runnable {

    // Attributes
    ServerSocket serverSocket; // Server socket object that can communicate with client sockets
    int portNumber; // The port number
    Socket client1, client2; // The two player clients

    /**
     * Assigns variables as a basic constructor
     * 
     * @param portNumber
     */
    public Server(int portNumber) {

        try {
            this.portNumber = portNumber;
            serverSocket = new ServerSocket(portNumber);

        }

        catch (BindException e) {

            new MessageWindow("This port number is already in use!");
            System.out.println("BindException: " + e.getMessage());
        }

        catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }

        catch (IllegalArgumentException e) {
            new MessageWindow("The port number is not valid!");
            System.out.println("IllegalArgumentException: " + e.getMessage());
        }

    }

    @Override
    /**
     * Runs the server
     */
    public void run() {

        try {

            // Print a message to confirm the server is running
            System.out.println("Server is Running");

            // Accept a socket for one player and print a conformation line
            client1 = serverSocket.accept();
            System.out.println("The first player has entered");

            // Declare the client handler object and start a thread to them
            ClientHandler clientHandler1 = new ClientHandler(this, client1, 1);
            Thread thread1 = new Thread(clientHandler1);
            thread1.start();

            // Accept a socket for the other player and print a conformation line
            client2 = serverSocket.accept();
            System.out.println("The second player has entered");

            // Declare the client handler object and start a thread to them
            ClientHandler clientHandler2 = new ClientHandler(this, client2, 2);
            Thread thread2 = new Thread(clientHandler2);
            thread2.start();

            // Tell each client who the other is
            clientHandler1.otherClient = clientHandler2;
            clientHandler2.otherClient = clientHandler1;
            clientHandler2.otherClient.out.println("OPP_JOINED");

            /**
             * Join the threads so that the following code is only execute after both
             * threads have stopped from running
             */
            thread1.join();
            thread2.join();
            // properly closes the server
            serverSocket.close();
            System.out.println("Server closed");

        }

        // Catch any exceptions that occur
        catch (UnknownHostException e) {
            System.out.println("UnknownHostException: " + e.getMessage());

        } catch (SocketException e) {

            System.out.println("SocketException: " + e.getMessage());
        } catch (IOException e) {

            System.out.println("IOException: " + e.getMessage());
        }

        catch (InterruptedException e) {
            System.out.println("InterruptedException: " + e.getMessage());
        }

        catch (NullPointerException e) {
            System.out.println("NullPointerException: " + e.getMessage());
        }

        // Finally ensure the server is definitely is closed
        finally {

            try {

                if (!serverSocket.isClosed()) {
                    serverSocket.close();
                    System.out.println("Server closed");
                }

                else {
                    System.out.println("Server is already closed");
                }
            }

            catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Simple getter method for the other client
     * 
     * @return Socket, the socket
     */
    public Socket getSecondClient() {
        return client2;
    }
}
