import javax.swing.*;

//A class that displays messages to the user
public class IntroMessageWindow extends JDialog {

    // Attributes
    public static Controller controller; // The Controller it belongs to

    /**
     * Basic constructor
     * 
     * @param message, the message to be displayed
     */
    public IntroMessageWindow(String message) {

        // Use the super constructor and create the label that displays the message
        super(controller.gameWindow, "Welcome to Royal Game Of Ur!", true);
        JLabel newMessage = new JLabel(message);
        JLabel instruction0 = new JLabel("Click Menu to select a play mode, start a new game or quit the current one.");
        JLabel instruction1 = new JLabel("Press the Roll Dice button to receive a number of steps.");
        JLabel instruction2 = new JLabel("Click one of your stones to move it on the board.");
        JLabel instruction3 = new JLabel("The first one to bear all their stones off the board wins.");
        JLabel finalMessage = new JLabel("Have fun!");

        // Create the close window button
        CloseButton closeWindow = new CloseButton("Close", this);

        // Add an action listener for the close button
        closeWindow.addActionListener(controller);
        /**
         * set the window's layout and the positioning of the message displayed, all the
         * instructions and the "Close" button
         */
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        newMessage.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        instruction0.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        instruction1.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        instruction2.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        instruction3.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        finalMessage.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        closeWindow.setAlignmentX(JButton.CENTER_ALIGNMENT);

        // Add all the components to the JDialog
        add(newMessage);
        add(instruction0);
        add(instruction1);
        add(instruction2);
        add(instruction3);
        add(finalMessage);
        add(closeWindow);

        // Set the window's size
        setSize(800, 200);
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