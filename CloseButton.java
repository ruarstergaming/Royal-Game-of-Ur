import javax.swing.JButton;
import javax.swing.JDialog;

/**
 * Public class for a button that closes the JDialog that the button is attached
 * to. 
 */
public class CloseButton extends JButton {

    JDialog parentDialog;
    /**
     * Constructor for setting the close button text and its parent JDialog
     * 
     * @param text
     * @param parentDialog
     */
    public CloseButton(String text, JDialog parentDialog) {

        setText(text);
        this.parentDialog = parentDialog;
    }

    /**
     * Closes the JDialog object attached to the button
     */
    public void closeWindow() {
        parentDialog.dispose();
    }
}
