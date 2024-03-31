import javax.swing.*;

//A class that displays messages to the user
public class MessageWindow extends JDialog {

    // Attributes
    public static Controller controller; // The Controller it belongs to

    /**
     * Basic constructor
     * 
     * @param message, the message to be displayed
     */
    public MessageWindow(String message) {

        // Use the super constructor and create the label that displays the message
        super(controller.gameWindow, "Message Window", true);
        JLabel newMessage = new JLabel(message);

        /**
         * properly setting the default close operaion for the JDialog
         * 
         * if a MessageWindow appears during a network game (i.e. the Client object from
         * the Game class is not null), the user has to click on the CloseButton in
         * order to close the JDialog, forcing the program to execute the block of code
         * reserved for this operation inside the Controller class
         */
        if (controller.game.isNetworkGame() && controller.game.getClient() != null) {

            setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        } else {
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        }

        // Create the close window button
        CloseButton closeWindow = new CloseButton("Close", this);

        // Add an action listener for the close button
        closeWindow.addActionListener(controller);
        // set the window's layout and the positioning of the message displayed and the
        // "Close" button
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        newMessage.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        closeWindow.setAlignmentX(JButton.CENTER_ALIGNMENT);

        // Add the message and close button
        add(newMessage);
        add(closeWindow);

        // Set the window's size
        setSize(600, 200);
        // set the window to be visible and to appear always in front of the GameWindow
        setLocationRelativeTo(controller.gameWindow);
        setAlwaysOnTop(true);
        setVisible(true);
    }

    /**
     * Closes the window
     */
    public void closeWindow() {
        dispose();
    }

    /**
     * Simple set method for the ActionListener that will be used for dealing with
     * receiving input via an instance of this class
     * 
     * @param newController
     */
    public static void setListener(Controller newController) {
        controller = newController;
    }
}
