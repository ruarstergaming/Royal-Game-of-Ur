import javax.swing.*;
import java.net.Socket;

//This is the panel that allows the user to enter the information to enter the game
public class InputPanel extends JPanel {

    // Attributes
    InputWindow parentDialog; // The window the panel belongs to
    JTextField textFieldPortNumber; // The text field where the user enters the host name
    JTextField textFieldHostname; // The text field where the user enters the port number
    JButton okButton; // The confirm button
    JButton backButton; // The back button that also closes the window
    Socket socket = null; // The socket its connecting to
    int stone = 0; // The players stone
    int portNumber; // The port number entered by the user
    String hostname; // The host name entered by the user

    /**
     * Basic constructor displaying the panel with the input fields
     * 
     * @param parentDialog, the window it belongs to
     */
    public InputPanel(InputWindow parentDialog) {

        // Set the parent dialog up
        this.parentDialog = parentDialog;

        // Display text for entering the hostname and port number
        JLabel labelHostname = new JLabel("Please input a Hostname/IP Address:");
        JLabel labelPort = new JLabel("Please input a Port Number:");

        // Nex text fields for user input
        textFieldPortNumber = new JTextField();
        textFieldHostname = new JTextField();

        // An ok button that will connect the user
        okButton = new JButton("OK");
        okButton.addActionListener(InputWindow.controller);

        backButton = new JButton("Back");
        backButton.addActionListener(InputWindow.controller);

        // Set the layout and the positioning of all the JLabel and JButton objects
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        labelHostname.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        labelPort.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        textFieldPortNumber.setAlignmentX(JTextField.CENTER_ALIGNMENT);
        textFieldHostname.setAlignmentX(JTextField.CENTER_ALIGNMENT);
        okButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
        backButton.setAlignmentX(JButton.CENTER_ALIGNMENT);

        // Add everything to the GUI
        add(labelHostname);
        add(textFieldHostname);
        add(labelPort);
        add(textFieldPortNumber);
        add(okButton);
        add(backButton);
    }

    /**
     * Process the unput of the user when they hit the okay button
     * 
     * @return boolean, for if the input was proccessed correctly
     */
    public boolean processInput() {
        try {

            // Store the input in variables
            portNumber = Integer.parseInt(textFieldPortNumber.getText());
            hostname = textFieldHostname.getText().trim();

            /**
             * If they are creating a game, set the player to red (1) otherwise set to green
             * (2)
             */
            if (parentDialog.getTitle().equals("Creating Room")) {

                stone = 1;
            } else {
                stone = 2;
            }
            return true;

            /**
             * catch exception in case the port number is not an actual integer number
             */
        } catch (NumberFormatException e) {
            new MessageWindow("The port number must be an actual number!");
            return false;
        }
    }
}
