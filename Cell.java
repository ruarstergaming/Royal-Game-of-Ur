import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import java.awt.*;

//Public Class that represents the game spaces
public class Cell extends JPanel {

    // Attributes
    int row; // Index for the row the cell is in for the game
    int column; // Index for the column the cell is in for the game
    RoundButton button = null; // A button object representing the gamepiece in the cell if there is one
    Image newImage = null; // If the image is a rosette square it will have an image to indicate this

    /**
     * Constructor that initialises all attributes except the image and displayes
     * the cell in the GUI
     * 
     * @param row
     * @param column
     * @param button
     */
    public Cell(int row, int column, RoundButton button) {

        // Initialises the attributes apart from the new image.
        this.row = row;
        this.column = column;
        this.button = button;

        // set the cell's layout
        setLayout(new GridBagLayout());
        // add a border line surrounding the cell so that it is properly delimited from
        // the rest of the board
        setBorder(new LineBorder(Color.black));
        setVisible(true);

        if (button != null) {
            add(button);
        }

    }

    /**
     * Initialises the cell using the other constructor but instead just making the
     * cell empty with no stone in it
     * 
     * @param row
     * @param column
     */
    public Cell(int row, int column) {

        this(row, column, null);
    }

    /**
     * Makes the player stone on the cell visible
     * 
     * @param color, The color the stone will be set to
     */
    public void setButtonVisible(int color) {

        // Makes the stone visible on the GUI, set its colour then display it with said
        // colour on the GUI
        button.setVisible(true);
        button.color = color;
        button.repaint();
    }

    /**
     * Makes the stone invisible
     */
    public void setButtonInvisible() {

        button.setVisible(false);
        button.color = -1;
        button.repaint();
    }

    @Override
    /**
     * This overriden method is the same as painting the cell but can also add the
     * rosette image to the paint
     */
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        if (newImage != null) {

            /**
             * properly drawing the rosette image
             */
            g.drawImage(newImage, 0, 0, null);
        }
    }
}
