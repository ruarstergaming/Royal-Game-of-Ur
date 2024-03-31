import javax.swing.*;

//The window that holds the panel where the user inputs to connect to an online game
public class InputWindow extends JDialog {

    // Attributes
    InputPanel inputPanel; // The panel where the input is done
    JPanel panel; // A start panel used for selecting the option of creating a network game or
                  // joining another one
    JButton createRoomButton; // The button that will create a room
    JButton joinRoomButton; // The button that will join a game
    CloseButton closeButton; // The button that closes the window
    public static Controller controller; // The Controller it's associated with

    /**
     * Basic constructor
     */
    public InputWindow() {

        // Use the super constructor
        super(controller.gameWindow, "Input Window", true);
        controller.inputWindow = this;

        // Set up the panel for the user input
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        inputPanel = new InputPanel(this);
        panel = new JPanel();

        // A label for the select option
        JLabel label = new JLabel("Select option:");

        // Create the close, back and create/join room buttons
        createRoomButton = new JButton("Create Room");
        joinRoomButton = new JButton("Join Room");
        closeButton = new CloseButton("Close", this);

        // Set the layout
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        // allign all the start panel's components in the center
        label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        createRoomButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
        joinRoomButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
        closeButton.setAlignmentX(JButton.CENTER_ALIGNMENT);

        // Display everything to the GUI
        panel.add(label);
        panel.add(createRoomButton);
        panel.add(joinRoomButton);
        panel.add(closeButton);
        add(panel);

        // Create action listeners for the GUI's
        createRoomButton.addActionListener(controller);
        joinRoomButton.addActionListener(controller);
        closeButton.addActionListener(controller);

        // Set up the window's size
        setSize(600, 200);
        // set the window to be visible and to appear always in front of the GameWindow
        setLocationRelativeTo(controller.gameWindow);
        setAlwaysOnTop(true);
        setVisible(true);
    }

    /**
     * Create the panel for the input with the appropriate title
     * 
     * @param title
     */
    public void createOrJoinRoomPanel(String title) {

        setTitle(title);
        remove(panel);
        add(inputPanel);
        revalidate();
        repaint();
    }

    /**
     * Makes the GUI go back to the previous panel
     */
    public void goBack() {
        remove(inputPanel);
        add(panel);
        revalidate();
        repaint();
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
