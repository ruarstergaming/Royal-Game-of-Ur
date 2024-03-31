import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;

//Class that handles how the board itself displays on the GUI
public class GameDisplay extends JPanel {

    // Attributes
    Cell[][] cells; // 2D array of cell objects representing the game board on the GUI side
    GameWindow parentWindow; // The window the game is being displayed on
    Image rosette = null; // The image used when displaying the rosette
    int indexMoveAnimation = 1; // The current index of the move animation

    /**
     * Basic constructor that initialises the game board and displays them all
     * 
     * @param parentWindow, GameWindow object which it the window the game is
     *                      displayed in
     */
    public GameDisplay(GameWindow parentWindow) {

        // Initialise the board with an extra column for the dice to display in
        cells = new Cell[3][9];
        this.parentWindow = parentWindow;

        // Try to read in the image for the rosette
        try {
            /**
             * REFERENCE
             * 
             * How do I add an image to a JButton. Stack Overflow. Retrieved 2 May 2021,
             * from
             * https://stackoverflow.com/questions/4801386/how-do-i-add-an-image-to-a-jbutton
             * 
             * We have made use of the content from the website provided above in order to
             * be able to read the rosette drawing and store it as an Image object
             */
            rosette = ImageIO.read(getClass().getResource("rosette.jpg"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set the layout for the board
        setLayout(new GridLayout(3, 9));

        // Loop through the size of the layout
        for (int j = 0; j < 3; j++) {

            for (int i = 0; i < 9; i++) {

                // If the column isn't the dice column then add a cell there
                if (i != 8) {

                    // Create the button for the cell
                    RoundButton button = new RoundButton(j, i, -1);
                    button.setAlignmentX(JButton.CENTER_ALIGNMENT);
                    button.setAlignmentY(JButton.CENTER_ALIGNMENT);

                    // Create the cell
                    Cell newCell = new Cell(j, i, button);
                    add(newCell);

                    // If the cell is invalid then set it to grey
                    if ((j == 0 || j == 2) && (i == 4 || i == 5)) {

                        newCell.setBackground(Color.gray);

                    }

                    // Otherwise the cell is white
                    else {

                        newCell.setBackground(Color.white);

                    }

                    // If the column is the dice column then set the background to grey
                    if (i == 8 && (j == 0 || j == 1 || j == 2)) {

                        newCell.setBackground(Color.gray);
                    }

                    // If the cell is a rosette then display the rosette image on it
                    if (((j == 0 || j == 2) && (i == 0 || i == 6)) || (j == 1 && i == 3)) {

                        /**
                         * assigning appropriate dimensions to the rosette drawings so that they would
                         * properly fit inside a cell
                         * 
                         * We have made use of content from the website provided by the reference below 
                         * 
                         * REFERENCE
                         * 
                         * resizing a ImageIcon in a JButton. Stack Overflow. Retrieved 30 April 2021,
                         * from
                         * https://stackoverflow.com/questions/2856480/resizing-a-imageicon-in-a-jbutton
                         */
                        Image newImage = rosette.getScaledInstance(parentWindow.getWidth() / 9,
                                parentWindow.getHeight() / 3 - 20, java.awt.Image.SCALE_SMOOTH);
                        newCell.newImage = newImage;
                    }

                    // Set the cell being made to the current position in the 2D array board
                    cells[j][i] = newCell;

                }

                // Othewise set the new cell to grey
                else {

                    Cell newCell = new Cell(j, i);
                    newCell.setBackground(Color.gray);
                    add(newCell);
                    cells[j][i] = newCell;
                }
            }
        }

        // For the home spaces create the buttons, and set the text of them
        cells[0][4].setButtonVisible(1);
        cells[0][4].button.setText("7");

        cells[2][4].setButtonVisible(2);
        cells[2][4].button.setText("7");
    }

    /**
     * Moves the stone on the display
     * 
     * @param game
     */
    public void moveStone(Game game) {

        // If there is an actual animation involved
        if (indexMoveAnimation < game.currentMove.size()) {

            /**
             * If the index of the move animation then call the special one for making the
             * stones move out of the board (from home or to the goal)
             */
            if (indexMoveAnimation == 1) {

                updateStonesOutsideTheBoard(game);
            }
            // Get the previous move
            int x = game.currentMove.get(indexMoveAnimation - 1).get(0);
            int y = game.currentMove.get(indexMoveAnimation - 1).get(1);

            int previousStone;

            /**
             * If index move animations isnt 1 then set the previous stone to the move
             * animation at the index previous
             */
            if (indexMoveAnimation != 1) {
                previousStone = game.currentMove.get(indexMoveAnimation - 1).get(2);

            }

            // Other wise the previous stone is 0
            else {
                previousStone = 0;
            }

            /**
             * If statements that decide which stone colour to display or just make them
             * invisible as they made it to the goal
             */
            if (previousStone == 2) {

                cells[x][y].setButtonVisible(2);
            } else if (previousStone == 1) {

                cells[x][y].setButtonVisible(1);
            } else {

                if (!((x == 0 && y == 4) || (x == 2 && y == 4))) {

                    cells[x][y].setButtonInvisible();
                } else if ((x == 0 && y == 4) && game.getRedStonesSupply() == 0) {

                    cells[x][y].setButtonInvisible();
                } else if ((x == 2 && y == 4) && game.getGreenStonesSupply() == 0) {

                    cells[x][y].setButtonInvisible();
                }
            }

            // Get the next move
            int i = game.currentMove.get(indexMoveAnimation).get(0);
            int j = game.currentMove.get(indexMoveAnimation).get(1);

            // Make the button visible
            cells[i][j].setButtonVisible(game.movingStone);

            // update the display
            revalidate();
            repaint();
            indexMoveAnimation++;

        }

        // Otherwise there is no animation to be done
        else {
            resetIndexMoveAnimation();

            // sets the roll dice button to be enabled/disabled
            parentWindow.setRollDiceButtonEnabled(game);
            // sets the appropriate message in the GameWindow's title bar
            parentWindow.setAppropriateTitle(game);

            // resets the dice's view and updates the score's view
            refreshDice();
            updateScore(game);
            // stop animation
            parentWindow.timerMove.stop();
        }
    }

    /**
     * This updates stones if they are being moved from home or to the goal
     * 
     * @param game
     */
    public void updateStonesOutsideTheBoard(Game game) {
        /**
         * properly displaying the red stones outside the board
         */
        if (game.getRedStonesSupply() != 0) {

            cells[0][4].setButtonVisible(1);
            cells[0][4].button.setText(String.valueOf(game.getRedStonesSupply()));
        } else {
            cells[0][4].setButtonInvisible();
        }

        /**
         * properly displaying the green stones outside the board
         */
        if (game.getGreenStonesSupply() != 0) {

            cells[2][4].setButtonVisible(2);
            cells[2][4].button.setText(String.valueOf(game.getGreenStonesSupply()));
        } else {

            cells[2][4].setButtonInvisible();
        }
        revalidate();
        repaint();
    }

    /**
     * Reset the dice cells for the dice animation to occur again
     */
    public void refreshDice() {
        for (int i = 0; i < 3; i++) {

            cells[i][8].removeAll();
            cells[i][8].add(new JLabel());
        }
        revalidate();
        repaint();
    }

    /**
     * Update the scores on the UI
     * 
     * @param game
     */
    public void updateScore(Game game) {
        /**
         * properly displaying the score of the player who is moving red stones
         */
        if (game.getRedScore() != 0) {

            cells[0][5].setButtonVisible(1);
            cells[0][5].button.setText(String.valueOf(game.getRedScore()));
            cells[0][5].button.setEnabled(false);
        }
        /**
         * properly displaying the score of the player who is moving green stones
         */
        if (game.getGreenScore() != 0) {
            cells[2][5].setButtonVisible(2);
            cells[2][5].button.setText(String.valueOf(game.getGreenScore()));
            cells[2][5].button.setEnabled(false);
        }
    }

    /**
     * resets the index used for crating the move animation
     */
    public void resetIndexMoveAnimation() {
        indexMoveAnimation = 1;
    }
}