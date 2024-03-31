import java.awt.*;
import javax.swing.*;

/**
 * This class represents the actual stones on the board
 * 
 * REFERENCE
 * 
 * Retrieved 30 April 2021, from https://www.javacodex.com/More-Examples/2/14
 * 
 * We have used content from the website provided by the reference from above in
 * order to be able to properly shape the JButton object into an actual circle
 */
public class RoundButton extends JButton {

    // Attributes
    int row, column; // The cooridnates on the board of the stone
    int color; // The colour of the stone

    /**
     * Basic constructor that intialises the button and its attributes
     * 
     * @param row
     * @param column
     * @param color
     */
    public RoundButton(int row, int column, int color) {

        // setBackground(Color.red);

        // Set the button to essentially invisible
        setFocusable(false);
        setOpaque(false);
        setFocusPainted(false);
        setBorderPainted(false);

        // Initialise variables
        this.row = row;
        this.column = column;
        this.color = color;

        // If the stone is red or green make it visible
        if (color == 1 || color == 2) {
            setVisible(true);
        }

        // Otherwise make it invisible
        else {
            setVisible(false);
        }

        // Make sure its the correct size
        Dimension size = getPreferredSize();
        size.width = size.height = 85;
        setPreferredSize(size);

        setContentAreaFilled(false);
    }

    /**
     * Protected method that sets colour of the stone
     */
    protected void paintComponent(Graphics g) {

        // If the button ist going to be displayed set it to blue
        if (getModel().isArmed()) {

            g.setColor(Color.blue);

        }

        // Otherwise set it to the correct colour for the stone index
        else {
            if (color == 1) {

                g.setColor(Color.red);

            } else if (color == 2) {

                g.setColor(Color.green);

            } else {

                g.setColor(Color.white);
            }
        }
        g.fillOval(0, 0, getSize().width, getSize().height);
        super.paintComponent(g);
    }

    /**
     * Simple getter for the row of the button
     * 
     * @return int, the row
     */
    public int getRow() {
        return row;
    }

    /**
     * Simple getter for the column of the button
     * 
     * @return int, the column
     */
    public int getColumn() {
        return column;
    }
}
