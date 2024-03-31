import javax.swing.*;

//The button that rolls the dice
public class RollDiceButton extends JButton {

    // Attributes
    GameWindow gameWindow; // The window the button will display on
    int indexDiceAnimation = -1; // The index of the dice animation

    /**
     * Basic constructor that initialises most attributes
     */
    public RollDiceButton(String text, GameWindow gameWindow) {

        this.gameWindow = gameWindow;
        // Set the text on the button
        setText(text);
    }

    /**
     * Reset the dice animation index
     */
    public void resetIndexDiceAnimation() {
        indexDiceAnimation = -1;
    }

    /**
     * Rolls the dice on the GUI
     * 
     * @param game, the state of the game
     */
    public void rollDice(Game game) {

        // If the animation isnt finished
        if (indexDiceAnimation != (game.getDiceValues().length - 1)) {

            // If the dice animation is invalid disable the animation
            if (indexDiceAnimation == -1) {
                setEnabled(false);
            }

            // Loop through each dice
            for (int i = 0; i < 3; i++) {

                // Increment the dice animation and find the value of the current dice
                indexDiceAnimation++;
                int value = Integer.parseInt(game.getDiceValues()[indexDiceAnimation]);

                // Remove the dice cells and replace them with the dice values
                gameWindow.gameDisplay.cells[i][8].removeAll();
                gameWindow.gameDisplay.cells[i][8].add(new JLabel(String.valueOf(value)));
            }

            // Update the GUI to reflect this
            gameWindow.gameDisplay.revalidate();
            gameWindow.gameDisplay.repaint();
        }

        // Otherwise stop the animation and reset
        else {

            resetIndexDiceAnimation();
            gameWindow.timerDice.stop();
        }
    }
}