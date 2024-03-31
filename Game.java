import java.util.*;

//Public class that handles most of the game logic
public class Game {

    // Attributes
    int[][] board = new int[3][8]; // 2D array storing the board information

    final int INVALID = -1; // Int representing an invalid square
    final int EMPTY = 0; // Int representing an empty square with no stone in it
    final int REDSTONE = 1; // Int representing a red stone
    final int GREENSTONE = 2; // Int representing a green stone

    final int AIOPPONENT = 0;
    final int LOCALMULTIPLAYER = 1;
    final int NETWORKGAME = 2;
    final int NOPLAYMODE = -1;

    int numRedStones = 7; // The number of red stones on the board (set to 7 initially)
    int numGreenStones = 7; // The number of green stones on the board (set to 7 initially)
    int redScore = 0; // The number of red stones at the goal (set to 0 initially)
    int greenScore = 0; // The number of green stones at the goal (set to 0 initially)

    int currentStone = REDSTONE; // The stone whos turn it currently is
    List<List<Integer>> currentMove; // The moves being made (used for animation)
    int movingStone; // The type of stone thats moving
    int steps = -1; // The number of steps a piece will take when doing a move
    String[] diceValues; // A string array of dice values
    int winner = 0; // The colour of he player who has won
    int playMode = -1; // The type of game mode i.e. local multiplayer, single player with AI and network play

    /**
     * all the following Random objects have been used for creating the animation of
     * rolling the dice
     */
    Random dice = new Random();

    GameWindow gameWindow;
    Client client;
    NetworkConstructor constructor;

    /**
     * Basic constructor that initalises the board and sets the window the game will
     * be displayed on
     * 
     * @param gameWindow
     */
    public Game(GameWindow gameWindow) {

        // Initialise the game board
        this.gameWindow = gameWindow;
        for (int i = 0; i < 3; i++) {

            for (int j = 0; j < 8; j++) {

                if ((i == 0 || i == 2) && (j == 4 || j == 5)) {

                    board[i][j] = INVALID;

                } else {

                    board[i][j] = EMPTY;
                }
            }
        }
    }

    /**
     * Resets the values of steps, dice, moves and the stone being moved
     */
    public void resetValues() {
        steps = -1;
        diceValues = null;
        currentMove = null;
        movingStone = 0;
    }

    /**
     * Rolls the dice
     */
    public void rollDice() {

        String allDiceValues = "";
        int diceValue;

        // Loop from 1 to 30 for creating the dice animation (each dice will display ten
        // consecutive integer values which are either 0 or 1)
        for (int i = 1; i <= 30; i++) {
            if (i <= 27) {

                //the first 27 values generated are for constructing the animation
                diceValue = dice.nextInt(2);

            } else {
                /**
                 * the remaining three values will be added together to construct the number of
                 * stepts the player will have to move a stone
                 */
                dice = new Random();
                diceValue = dice.nextInt(2);
                if (i == 28) {

                    steps = 0;
                }
                steps += diceValue;
            }
            allDiceValues += " " + String.valueOf(diceValue);
        }

        // Clean the dice values string and then reflect that on the UI
        this.diceValues = allDiceValues.trim().split(" ");
        gameWindow.updateDice(this);
    }

    /**
     * Checks if the stone of a given set of coordinates is green or not
     * 
     * @param i
     * @param j
     * @return boolean, if the stone is green
     */
    public boolean isGreen(int i, int j) {
        if (board[i][j] == GREENSTONE) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if the stone of a given set of coordinates is red or not
     * 
     * @param i
     * @param j
     * @return boolean, if the stone is red
     */
    public boolean isRed(int i, int j) {
        if (board[i][j] == REDSTONE) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Resets the game for a fresh start
     */
    public void refreshGame() {

        // Nested loop that resets the board
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 8; j++) {

                if ((i == 0 || i == 2) && (j == 4 || j == 5)) {
                    board[i][j] = INVALID;
                } else {
                    board[i][j] = EMPTY;
                }

            }
        }

        // Reset the othe values
        currentStone = REDSTONE;
        numGreenStones = numRedStones = 7;
        redScore = greenScore = 0;
        winner = 0;
        diceValues = null;
        currentMove = null;
        steps = -1;

        // Reflect it on the UI
        gameWindow.refreshGame(this);
    }

    /**
     * Checks if the cell of a given coordinate is a rosette space
     * 
     * @param row,    the row of the space
     * @param column, the column of the space
     * @return boolean, if there is a rosette there or not
     */
    public boolean isRosette(int row, int column) {

        // If the space is one of the corners or in the middle then it is a rosette,
        // otherwise it isn't
        if (((row == 0 || row == 2) && (column == 0 || column == 6)) || (row == 1 && column == 3)) {

            return true;
        }
        return false;
    }

    /**
     * Calculates the cells the stone will move through
     * 
     * @param row,    the row of the stone being moved
     * @param column, the column of the stone being moved
     * @param steps,  the amoung of steps the stone has to make
     * @return List<List<Integer>>, the cells the stone will move through
     */
    public List<List<Integer>> getCellsMove(int row, int column, int steps) {

        // assign the row and columns to indexes of an array
        int i = row;
        int j = column;
        List<List<Integer>> allMoves = new ArrayList<>();
        allMoves.add(Arrays.asList(row, column, board[row][column]));

        // Loop for each step
        while (steps != 0) {

            // Increment the steps downwards so we don't get an infinite loop
            steps--;

            // Make a series of checks to determine where the stone moves next
            if (i == 0) {

                if (j == 0) {
                    i++;
                } else {
                    j--;
                }

            } 
            
            else if (i == 2) {

                if (j == 0) {
                    i--;
                } else {
                    j--;
                }
            } 
            
            else {

                if (j == 7 && board[row][column] == REDSTONE) {

                    i--;
                } else if (j == 7 && board[row][column] == GREENSTONE) {

                    i++;
                } else {

                    j++;
                }
            }

            /**
             * storing the cells coordinates and value of that cell i.e. 1 or 2 if there is
             * a stone, 0 if the cell is empty or -1 if the cell is invalid
             */
            allMoves.add(Arrays.asList(i, j, board[i][j]));
           
            /**
             * if the stone has reached the end of the game's path the program will
             * imediately returned all the moves made up until here. This allows the user to
             * bear off a stone by only having to receive, from rolling the dice, a number
             * of steps greater than a certain amount, not the exact value of that amount
             */
            if (board[i][j] == INVALID && steps != 0) {

                return allMoves;
            }
        }

        return allMoves;
    }

    /**
     * A method that prints the state of the game (for debugging purposes)
     */
    public void printGame() {

        // Loop through each cell in the board and depending on its state print out the
        // correct integer
        for (int i = 0; i < 3; i++) {

            for (int j = 0; j < 8; j++) {

                if (board[i][j] == INVALID) {

                    System.out.print(board[i][j] + " ");

                } else {

                    System.out.print(" " + board[i][j] + " ");
                }

            }
            // print a line between each row
            System.out.println();
        }
    }

    public boolean isMoveValid(int newRow, int newColumn, int stone) {

        // If the new column and row are within bounds of the board the move is invalid
        if (newRow < 0 || newRow > 2 || newColumn < 0 || newColumn > 7) {

            return false;

        } else if (board[newRow][newColumn] == stone) {

            return false;

        }

        // Otherwise the move is valid
        else {

            return true;
        }
    }

    /**
     * Simple get method that gets the values of the board
     * 
     * @return
     */
    public int[][] getBoard() {

        return board;

    }

    /**
     * Checks if a stone is in a given set of cooridnates
     * 
     * @param row,    the row of the cell ebing checked
     * @param column, the column of the cell ebing checked
     * @return boolean, if there is a stone there or not
     */
    public boolean isCurrentPlayer(int row, int column) {

        if ((currentStone == REDSTONE && (board[row][column] == REDSTONE || (row == 0 && column == 4)))
                || (currentStone == GREENSTONE && (board[row][column] == GREENSTONE || (row == 2 && column == 4)))) {
            return true;
        }
        return false;
    }

    /**
     * get the colour of the stone of a given set of coordinates
     * 
     * @param row,    the row of the stone being checked
     * @param column, the row of the stone being checked
     * @return int the index of the stone being checked
     */
    public int getStone(int row, int column) {
        if (row == 0 && column == 4) {

            return REDSTONE;

        } else if (row == 2 && column == 4) {

            return GREENSTONE;

        } else {
            return board[row][column];
        }
    }

    /**
     * Make the actual move
     * 
     * @param row,    the row of the stone being moved
     * @param column, the row of the stone being moved
     * @param steps,  the amount of steps its going to move
     * @return boolean, if the move has been made
     */
    public boolean makeMove(int row, int column, int steps) {

        // If there is a player currently on the space being selected then make the move
        if (isCurrentPlayer(row, column)) {

            List<List<Integer>> currentMove = getCellsMove(row, column, steps);
            List<Integer> newCell = currentMove.get(currentMove.size() - 1);

            int newRow = newCell.get(0);
            int newColumn = newCell.get(1);
            int movingStone = getStone(row, column);

            // If there are steps to be made
            if (steps != 0) {

                // If the move is valid
                if (isMoveValid(newRow, newColumn, movingStone)) {

                    // If the new space its moving to is valid
                    if (board[newRow][newColumn] != EMPTY && board[newRow][newColumn] != INVALID) {

                        // If the stone there is red set it to green and return the red stone to home,
                        // otherwise do the inverse
                        if (board[newRow][newColumn] == REDSTONE) {

                            numRedStones++;
                            board[newRow][newColumn] = GREENSTONE;

                        } else {

                            numGreenStones++;
                            board[newRow][newColumn] = REDSTONE;
                        }

                    }

                    // Otherwise just make the stone being moved current place to empty
                    else {

                        // Otherwise if the new place is empty move the piece to the new place
                        if (board[newRow][newColumn] == EMPTY) {

                            board[newRow][newColumn] = movingStone;

                        }

                        /**
                         * Otherwise depending on whether its red or green add 1 to the green score or 1
                         * to the red score
                         */
                        else {

                            if (movingStone == REDSTONE) {
                                redScore++;

                            } else {

                                greenScore++;
                            }
                        }
                    }

                    /**
                     * If the player is trying to get a red stone out of home then change the
                     * appropriate values
                     */
                    if (row == 0 && column == 4 && numRedStones != 0) {

                        numRedStones--;
                        if (numRedStones == 0) {
                            board[row][column] = EMPTY;
                        } else {
                            board[row][column] = INVALID;
                        }

                    }

                    /**
                     * If the player is trying to get a green stone out of home then change the
                     * appropriate values
                     */
                    else if (row == 2 && column == 4 && numGreenStones != 0) {

                        numGreenStones--;
                        if (numGreenStones == 0) {
                            board[row][column] = EMPTY;
                        } else {
                            board[row][column] = INVALID;
                        }
                    }

                    // Otherwise just set the current position on the board to empty
                    else {

                        board[row][column] = EMPTY;
                    }

                    /**
                     * If there is no rosette on the place being moved to switch to the other
                     * players turn and reuturn the moves to be made i.e. the animations
                     */
                    if (!isRosette(newRow, newColumn)) {
                        changeCurrentStone();
                    }
                    System.out.println(row + " " + column + " " + steps);
                    this.currentMove = currentMove;
                    this.movingStone = movingStone;
                    gameWindow.updateMove(this);
                    return true;
                }
            }

            /**
             * Otherwise if the stone would've moved 0 steps change the turn order and
             * return the moves
             */
            else {
                System.out.println(row + " " + column + " " + steps);
                this.currentMove = currentMove;
                this.movingStone = movingStone;
                changeCurrentStone();
                gameWindow.updateMove(this);
                return true;
            }
        }
        return false;
    }

    /**
     * Check if someone has won and display the appropriate message if someone has
     * won
     * 
     * @return boolean, if someone has won
     */
    public boolean verifyWinner() {

        // If red has won, set the winner to red and display it then return true
        if (redScore == 7) {
            winner = REDSTONE;
            gameWindow.displayWinner(this);
            return true;
        }

        /**
         * Otherwise if green has won, set the winner to green and display it then
         * return true
         */
        else if (greenScore == 7) {
            winner = GREENSTONE;
            gameWindow.displayWinner(this);
            return true;
        }

        // Otherwise if no one has won return false
        return false;

    }

    /**
     * Simple get method for the winner of the game
     * 
     * @return int, the winner 0 if no one has won
     */
    public int getWinner() {

        return winner;
    }

    /**
     * Changes the current turn order
     */
    public void changeCurrentStone() {

        // If the current turn is red change it to green and vice versa
        if (currentStone == REDSTONE) {

            currentStone = GREENSTONE;
        } else {

            currentStone = REDSTONE;
        }
    }

    /**
     * This method constructs a new NetworkConstructor object while making use of
     * the InputWindow object passed as a parameter
     * 
     * @param inputWindow the InputWindow object that the NetworkConstrutor object
     *                    will be associated with
     */
    public void createNetworkConstrutor(InputWindow inputWindow) {
        this.constructor = new NetworkConstructor(inputWindow);
    }

    /**
     * The following three methods are used to verify which play mode is currently
     * selected
     */
    public boolean isNetworkGame() {
        if (playMode == NETWORKGAME) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isLocalMultiplayer() {
        if (playMode == LOCALMULTIPLAYER) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isAIOpponent() {
        if (playMode == AIOPPONENT) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isNoPlayModeSelected() {
        if (playMode == NOPLAYMODE) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * The following methods represent a set of setters and getters used throughout
     * the entire implementation of the program
     */
    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {

        this.client = client;
    }

    public int getPlayMode() {
        return playMode;
    }

    public void setPlayMode(int playMode) {

        this.playMode = playMode;
    }

    public NetworkConstructor getConstrutor() {
        return constructor;
    }

    public int getSteps() {
        return steps;
    }

    public int getMovingStone() {
        return movingStone;
    }

    public int getCurrentStone() {
        return currentStone;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public void setDiceValues(String[] diceValues) {
        this.diceValues = diceValues;
    }

    public int getRedStonesSupply() {
        return numRedStones;
    }

    public int getGreenStonesSupply() {
        return numGreenStones;
    }

    public int getRedScore() {
        return redScore;
    }

    public int getGreenScore() {
        return greenScore;
    }

    public String[] getDiceValues() {

        return diceValues;
    }
}
