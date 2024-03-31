import java.util.*;

//This class is for the AI and controls how it makes moves
public class Ai {

    //Attributes 
    Game game;  //The state of the game the AI is participating in

    /**
     * Simple Constructor initalising the AI and the game state it has to judge
     * @param game
     */
    public Ai(Game game) {
        this.game = game;
    }

    /**
     * Method to calculate and return a move for the ai to play
     * @param diceRoll, the roll the dice makes
     * @return int[], the moves for the ai to play
     */
    public int[] getMove(int diceRoll) {
        int[] move = new int[2];

        // find all possible moves for the ai
        List<int[]> allMoves = findAllAiMoves(game.board, diceRoll);

        //if moving to a rosette is possible, return that move
        for (int[] move0 : allMoves) {
            if (game.isRosette(findNextAiPosition(move0[0], move0[1], diceRoll)[0],
                    findNextAiPosition(move0[0], move0[1], diceRoll)[1])) {
                return move0;
            }
        }
        /**
         * if the AI opponent cannot land on a rosette given the dice roll passed as a
         * parameter, then the program will invoke the expectiMinimax method which will
         * return the best move the AI opponent considers it can make
         */
        move = expectiMinimax(diceRoll);

        return move;
    }

    /**
     * Method to find the best move for the ai from list of possible moves
     * @param diceRoll, the roll of the dice
     * @return int[], the best possible move for the AI to make
     */
    private int[] expectiMinimax(int diceRoll) {

        // create new boards for all possible moves for current board
        List<int[][]> level1Boards = createBoardsAi(game.board, diceRoll);

        // create new list to store next level of boards
        List<List<List<int[][]>>> level2Boards = new ArrayList<List<List<int[][]>>>();

        /**
         * create new boards for all possible user moves including all the possible
         * rolls the user could make
         */
        for (int[][] board : level1Boards) {

            List<List<int[][]>> tempBoards = new ArrayList<List<int[][]>>();
            for (int i = 0; i < 4; i++) { // i is each dice roll
                tempBoards.add(createBoardsUser(board, i));
            }
            level2Boards.add(tempBoards);
        }

        // create new list of scores of all new boards
        List<List<List<Double>>> scores1 = new ArrayList<List<List<Double>>>();

        for (List<List<int[][]>> temp1 : level2Boards) {

            List<List<Double>> scores2 = new ArrayList<List<Double>>();
            
            for (List<int[][]> temp2 : temp1) {
            
                List<Double> scores3 = new ArrayList<Double>();
            
                for (int[][] board : temp2) {

                    scores3.add(calculateM(board));
                }
            
                scores2.add(scores3);
            }
            
            scores1.add(scores2);
        }

        // apply expectiminimax algorithm to calculate best move
        List<Double> averageScores = new ArrayList<Double>();
        
        for (List<List<Double>> scores2 : scores1) {
        
            List<Double> worstScores = new ArrayList<Double>();
        
            for (List<Double> scores3 : scores2) {
                /**
                 * find the worst score from list of scores, (user should pick worse score for
                 * ai)
                 */
                worstScores.add(Collections.min(scores3));
            }
            // find the weighted average of the worst scores
            averageScores.add(calculateWeightedAverage(worstScores));
        }

        // find best score from average scores
        double bestScore = Collections.max(averageScores);

        // find the index of best score
        int bestIndex = averageScores.indexOf(bestScore);

        // find best move
        List<int[]> allMoves = findAllAiMoves(game.board, diceRoll);
        int[] bestMove = allMoves.get(bestIndex);

        return bestMove;
    }

    /**
     * Method to calculate the weighted average of scores from dice rolls
     * @param worstScores, the worst score from each tree branch (each branch is worth only as musch as its worst part)
     * @return double, the average M score for that move
     */
    private double calculateWeightedAverage(List<Double> worstScores) {
        double sumScore = 0;

        /**
         * 3s and 0s dice rolls have a 0.125 probability, 1s and 2s have a 0.375
         * probability
         */
        int i = 0;
        for (double score : worstScores) {
            if (i == 0 || i == 3) {
                sumScore = sumScore + (score * 0.125);
            } else {
                sumScore = sumScore + (score * 0.375);
            }
            i++;
        }

        double averageScore = sumScore / 4;
        return averageScore;
    }

    /**
     * Method to create boards from all possible ai moves
     * @param board, the current state of the board
     * @param diceRoll, the current amount of steps an ai can make
     * @return List<int[][]>, a list of board objects that are the possible board states
     */
    private List<int[][]> createBoardsAi(int[][] board, int diceRoll) {
        // create new boards by playing every possible move
        List<int[][]> newBoards = new ArrayList<int[][]>();

        // create list of all possible moves from board
        List<int[]> allMoves = findAllAiMoves(board, diceRoll);

        for (int[] move : allMoves) {
            // find new position of current move
            int[] newPos = findNextAiPosition(move[0], move[1], diceRoll);

            // create new board
            newBoards.add(createBoard(board, move[0], move[1], newPos[0], newPos[1]));
        }

        return newBoards;
    }

    /**
     * Method to create boards from all possible user moves
     * @param board, the current state of the board
     * @param diceRoll, the current amount of steps a player can make
     * @return List<int[][]>, a list of board objects that are the possible board states
     */
    private List<int[][]> createBoardsUser(int[][] board, int diceRoll) {
        // create new boards by playing every possible move
        List<int[][]> newBoards = new ArrayList<int[][]>();

        // create list of all possible moves from board
        List<int[]> allMoves = findAllUserMoves(board, diceRoll);

        for (int[] move : allMoves) {
            // find new position of current move
            int[] newPos = findNextUserPosition(move[0], move[1], diceRoll);

            // create new board
            newBoards.add(createBoard(board, move[0], move[1], newPos[0], newPos[1]));
        }

        return newBoards;
    }

    /**
     * Method to find all possible legal moves for the ai, assuming ai is green
     * @param board, the current state of the board
     * @param diceRoll, the current amount of steps a player can make
     * @return List<int[]>, a list of all the moves the green player (AI) can make
     */
    private List<int[]> findAllAiMoves(int[][] board, int diceRoll) {
        List<int[]> allMoves = new ArrayList<int[]>();

        /**
         * find all green pieces on board, (starts at 1, because no green pieces can be
         * on top row)
         */
        for (int i = 1; i < 3; i++) {
            for (int j = 0; j < 8; j++) {

                if (board[i][j] == game.GREENSTONE || (i == 2 && j == 4 && game.getGreenStonesSupply() != 0)) {
                    // calculate new position of stone
                    int[] newPosition = findNextAiPosition(i, j, diceRoll);

                    // if new postion is valid, store the current position as a possible move
                    if (isAiMoveLegal(board, newPosition[0], newPosition[1], diceRoll)) {
                        int[] currentPosition = new int[2];
                        currentPosition[0] = i;
                        currentPosition[1] = j;

                        allMoves.add(currentPosition);
                    }
                }
            }
        }

        return allMoves;
    }

    /**
     * Method to find all possible legal moves for the user, assuming user is red
     * @param board, the current state of the board
     * @param diceRoll, the current amount of steps a player can make
     * @return List<int[]>, a list of all the moves the red player (Human) can make
     */
    private List<int[]> findAllUserMoves(int[][] board, int diceRoll) {
        List<int[]> allMoves = new ArrayList<int[]>();

        /**
         * find all red pieces on board, (ends before 2, because no red pieces can be on
         * the bottom row)
         */
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 8; j++) {

                
                if (board[i][j] == game.REDSTONE || (i == 0 && j == 4 && game.getRedStonesSupply() != 0)
                        || (i == 0 && j == 4 && game.getRedStonesSupply() == 0 && findNoUserPieces(board) == 0)) {
                    // calculate new position of stone
                    int[] newPosition = findNextUserPosition(i, j, diceRoll);

                    // if new postion is valid, store the current position as a possible move
                    if (isUserMoveLegal(board, newPosition[0], newPosition[1], diceRoll)) {
                        int[] currentPosition = new int[2];
                        currentPosition[0] = i;
                        currentPosition[1] = j;

                        allMoves.add(currentPosition);
                    }
                }
            }
        }

        return allMoves;
    }

    /**
     * Method to calculate new position for ai pieces
     * @param currentRow, the current row of the stone being moved
     * @param currentColumn, the current column of the stone being moved
     * @param diceRoll, the current amount of steps a player can make
     * @return int[], the position of the user next
     */
    private int[] findNextAiPosition(int currentRow, int currentColumn, int diceRoll) {
        int[] nextPosition = new int[2];

        // set next position to the current position
        nextPosition[0] = currentRow;
        nextPosition[1] = currentColumn;

        // loop for length of dice roll
        for (int i = 0; i < diceRoll; i++) {
            // if piece is in bottom row
            if (nextPosition[0] == 2) {

                // and piece if first column, move up
                if (nextPosition[1] == 0) {
                    nextPosition[0]--;
                }
                // or if any other column move left
                else {
                    nextPosition[1]--;
                }
            }

            // piece is in middle row
            else {

                // and if piece in last column, move down
                if (nextPosition[1] == 7) {
                    nextPosition[0]++;
                }

                // or if any other column move right
                else {
                    nextPosition[1]++;
                }
            }

            if (game.board[nextPosition[0]][nextPosition[1]] == game.INVALID) {
                break;
            }
        }
        return nextPosition;
    }

    /**
     * Method to calculate new position for user pieces
     * @param currentRow, the current row of the stone being moved
     * @param currentColumn, the current column of the stone being moved
     * @param diceRoll, the current amount of steps a player can make
     * @return int[], the position of the user
     */
    private int[] findNextUserPosition(int currentRow, int currentColumn, int diceRoll) {
        int[] nextPosition = new int[2];

        // set next position to the current position
        nextPosition[0] = currentRow;
        nextPosition[1] = currentColumn;

        // loop for length of dice roll
        for (int i = 0; i < diceRoll; i++) {
           
            // if piece is in top row
            if (nextPosition[0] == 0) {

                // and piece if first column, move down
                if (nextPosition[1] == 0) {
                    nextPosition[0]++;
                }
           
                // or if any other column move left
                else {
                    nextPosition[1]--;
                }
            }
           
            // piece is in middle row
            else {
                // if piece in last column, move up
                if (nextPosition[1] == 7) {
                    nextPosition[0]--;
                }
           
                // or if any other column move right
                else {
                    nextPosition[1]++;
                }
            }
           
            if (game.board[nextPosition[0]][nextPosition[1]] == game.INVALID) {
                break;
            }
        }
        return nextPosition;
    }

    /**
     * Method to test whether a move is legal for the ai or not
     * @param board, the current state of the board
     * @param newRow, the row the stone is being moved to
     * @param newColumn, the column the stone is being moved to
     * @param diceRoll, the current amount of steps a player can ma
     * @return boolean, if the move is legal or not
     */
    private boolean isAiMoveLegal(int[][] board, int newRow, int newColumn, int diceRoll) {

        /**
         * if new position of piece is already occupied, move is invalid, unless
         * diceroll is 0
         */
        if (board[newRow][newColumn] == game.GREENSTONE && diceRoll != 0) {
            return false;
        }
        
        // otherwise, move is valid
        else {
            return true;
        }
    }

    /**
     * Method to test whether a move is legal for the user or not
     * @param board, the current state of the board
     * @param newRow, the row the stone is being moved to
     * @param newColumn, the column the stone is being moved to
     * @param diceRoll, the current amount of steps a player can make
     * @return boolean, if the move is legal or not
     */
    private boolean isUserMoveLegal(int[][] board, int newRow, int newColumn, int diceRoll) {

        /**
         * if new position of piece is already occupied, move is invalid, unless
         * diceroll is 0
         */
        if (board[newRow][newColumn] == game.REDSTONE && diceRoll != 0) {
            return false;
        }
        
        // otherwise, move is valid
        else {
            return true;
        }
    }

    /**
     * Method to create a new board from a given move and board
     * @param board, the current state of the board
     * @param currentRow, the current row of the stone being moved
     * @param currentColumn, the current column of the stone being moved
     * @param newRow, the row the stone is being moved to
     * @param newColumn, the column the stone is being moved to
     * @return int[][], the new state of the board once the move's been completed
     */
    private int[][] createBoard(int[][] board, int currentRow, int currentColumn, int newRow, int newColumn) {
        int[][] newBoard = new int[3][8];

        // set newBoard to given board
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 8; j++) {
                newBoard[i][j] = board[i][j];
            }
        }
        // printBoard(newBoard);

        // check what type the current piece is
        int currentPiece;

        // remove piece from current tile
        if ((currentRow != 0 && currentRow != 2) || currentColumn != 4) {
            currentPiece = newBoard[currentRow][currentColumn];
            newBoard[currentRow][currentColumn] = game.EMPTY;

        } 
        
        else if (currentRow == 0) {
            currentPiece = game.REDSTONE;
        } 
        
        else {
            currentPiece = game.GREENSTONE;
        }

        // add piece to new tile
        if (newRow != 2 || newColumn != 5) {
            newBoard[newRow][newColumn] = currentPiece;
        }

        // printBoard(newBoard);
        return newBoard;
    }

    /**
     * Method to calculate a value for how good a state of the board is for the ai
     * @param board, the board state being checked
     * @return double, the m score of the board state
     */
    private double calculateM(int[][] board) {
        double m = 0;

        // calculate values of the board state, each value is given weighting
        int noPiecesOnBoard = findNoPieces(board) * 20;
        int noUserPiecesOnBoard = findNoUserPieces(board) * 150;
        int noPiecesBeared = findNoPiecesBeared(board) * 100;
        int noUserPiecesBeared = findNoUserPiecesBeared(board) * 50;
        double avgPlaceOfPiecesOnBoard = calculateAvgPlace(board) * 2;
        double avgPlaceOfUserPiecesOnBoard = calculateAvgUserPlace(board) * 2;
        int noUserPiecesInRange = findNoUserPiecesInRange(board) * 20;
        int noPiecesInRange = findNoPiecesInRange(board) * 50;

        // add number of ai pieces on board
        m = m + noPiecesOnBoard;

        // minus number of user pieces on board
        m = m - noUserPiecesOnBoard;

        // add number of ai pieces beared
        m = m + noPiecesBeared;

        // minus number of ai pieces beared
        m = m - noUserPiecesBeared;

        // add number of user pieces in range of ai pieces
        m = m + noUserPiecesInRange;

        // minus number of ai pieces in range of user pieces
        m = m - noPiecesInRange;
        /**
         * add the customized value corresponding for how far the green stones have
         * travelled on the board
         */
        m = m + avgPlaceOfPiecesOnBoard;
        /**
         * minus the customized value corresponding for how far the red stones have
         * travelled on the board
         */
        m = m - avgPlaceOfUserPiecesOnBoard;

        return m;
    }

    /**
     * Method to find the number of ai pieces on the board
     * @param board, the current board state
     * @return int, the number of AI stones in the given board state
     */
    private int findNoPieces(int[][] board) {
        int noPieces = 0;

        /**
         * find all green pieces on board, (starts at 1, because no green pieces can be
         * on top row)
         */
        for (int i = 1; i < 3; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == game.GREENSTONE) {
                    noPieces++;
                }
            }
        }

        return noPieces;
    }

    /**
     * Method to find the number of user pieces on the board
     * @param board, the current board state
     * @return int, the number of player stones in the given board state
     */
    private int findNoUserPieces(int[][] board) {
        int noPieces = 0;
        /**
         * find all red pieces on board, (ends before 2, because no red pieces can be on
         * bottom row)
         */
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == game.REDSTONE) {
                    noPieces++;
                }
            }
        }
        return noPieces;
    }

    /**
     * Method to find the number of ai pieces that have been beared off the board
     * @param board, the current board state
     * @return int, the number of ai stones that have been beared off the board
     */
    private int findNoPiecesBeared(int[][] board) {
        if (findNoPieces(board) >= (7 - game.greenScore - game.numGreenStones)) {
            return game.greenScore;

        } else {

            return (game.greenScore + 1);
        }
    }


    /**
     * Method to find the number of user pieces that have been beared off the board
     * @param board, the current board state
     * @return int, the number of user stones that have been beared off the board
     */
    private int findNoUserPiecesBeared(int[][] board) {
        if (findNoPieces(board) >= (7 - game.redScore - game.numRedStones)) {
            return game.redScore;
        } else {

            return (game.redScore + 1);
        }
    }

    /**
     * 
     * @param board a future possible game board
     * @return returns a double value depending on how far the green stones have
     *         travelled on the board, the number of green stones on the board and
     *         the number of green stones that have been moved off the fboard
     */
    private double calculateAvgPlace(int[][] board) {
        int sumPlace = 0;

        // for each red stone moved off teh board add 15*15 to the sum
        sumPlace = sumPlace + (findNoPiecesBeared(board) * 15 * 15);
        /**
         * find all green pieces on board and add distance each piece has travelled and
         * add a particular number to the sum
         */
        for (int i = 1; i < 3; i++) {
            for (int j = 0; j < 8; j++) {

                if (board[i][j] == game.GREENSTONE) {

                    switch (j) {

                    case 0:
                        if (i == 1) {
                            sumPlace = sumPlace + 5 * 5;
                        } else {
                            sumPlace = sumPlace + 4 * 10;
                        }
                        break;

                    case 1:
                        if (i == 1) {
                            sumPlace = sumPlace + 6 * 6;
                        } else {
                            sumPlace = sumPlace + 3 * 20;
                        }
                        break;

                    case 2:
                        if (i == 1) {
                            sumPlace = sumPlace + 7 * 7;
                        } else {
                            sumPlace = sumPlace + 2 * 30;
                        }
                        break;

                    case 3:
                        if (i == 1) {
                            sumPlace = sumPlace + 8 * 8;
                        } else {
                            sumPlace = sumPlace + 1 * 40;
                        }
                        break;

                    case 4:
                        if (i == 1) {
                            sumPlace = sumPlace + 9 * 9;
                        }
                        break;

                    case 5:
                        if (i == 1) {
                            sumPlace = sumPlace + 10 * 10;
                        }
                        break;

                    case 6:
                        if (i == 1) {
                            sumPlace = sumPlace + 11 * 11;
                        } else {
                            sumPlace = sumPlace + 14 * 14;
                        }
                        break;

                    case 7:
                        if (i == 1) {
                            sumPlace = sumPlace + 12 * 12;
                        } else {
                            sumPlace = sumPlace + 13 * 13;
                        }
                        break;
                    }
                }
            }
        }

        if (sumPlace != 0) {
            double avgPlace = sumPlace / (double) (findNoPieces(board) + findNoPiecesBeared(board));
            return avgPlace;
        }
        return 0;
    }

    /**
     * 
     * @param board a future possible game board
     * @return returns a double value depending on how far the red stones have
     *         travelled on the board, the number of red stones on the board and the
     *         number of red stones that have been moved off the board
     */
    private double calculateAvgUserPlace(int[][] board) {
        int sumPlace = 0;

        // for each red stone moved off teh board add 15*15 to the sum
        sumPlace = sumPlace + (findNoUserPiecesBeared(board) * 15 * 15);

        /**
         * find all red pieces on board and add distance each piece has travelled and
         * add a particular number to the sum
         */
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 8; j++) {

                if (board[i][j] == game.REDSTONE) {
                    switch (j) {

                    case 0:
                        if (i == 1) {
                            sumPlace = sumPlace + 5 * 5;
                        } else {
                            sumPlace = sumPlace + 4 * 15;
                        }
                        break;

                    case 1:
                        if (i == 1) {
                            sumPlace = sumPlace + 6 * 6;
                        } else {
                            sumPlace = sumPlace + 3 * 20;
                        }
                        break;

                    case 2:
                        if (i == 1) {
                            sumPlace = sumPlace + 7 * 9;
                        } else {
                            sumPlace = sumPlace + 2 * 30;
                        }
                        break;

                    case 3:
                        if (i == 1) {
                            sumPlace = sumPlace + 8 * 10;
                        } else {
                            sumPlace = sumPlace + 1 * 30;
                        }
                        break;

                    case 4:
                        if (i == 1) {
                            sumPlace = sumPlace + 9 * 9;
                        }
                        break;

                    case 5:
                        if (i == 1) {
                            sumPlace = sumPlace + 10 * 10;
                        }
                        break;

                    case 6:
                        if (i == 1) {
                            sumPlace = sumPlace + 11 * 11;
                        } else {
                            sumPlace = sumPlace + 14 * 14;
                        }
                        break;

                    case 7:
                        if (i == 1) {
                            sumPlace = sumPlace + 12 * 12;
                        } else {
                            sumPlace = sumPlace + 13 * 13;
                        }
                        break;

                    }
                }
            }
        }
        
        if (sumPlace != 0) {
            double avgPlace = sumPlace / (double) (findNoUserPieces(board) + findNoUserPiecesBeared(board));
            return avgPlace;
        }

        return 0;
    }


    /**
     * Method to find the number of user pieces in range of ai pieces
     * @param board, the current board state
     * @return int, the number of player stones that can be taken
     */
    private int findNoUserPiecesInRange(int[][] board) {
        int noInRange = 0;

        /**
         * find all red pieces on board, (only middle row needs to be checked because
         * they are the only pieces in danger)
         */
        int i = 1;
        for (int j = 0; j < 8; j++) {
            if (board[i][j] == game.REDSTONE) {
                // check if there is a green piece within 3 squares behind
                boolean foundPiece = false;

                // set next position to the current position
                int[] nextPosition = new int[2];
                nextPosition[0] = i;
                nextPosition[1] = j;

                int k = 0;
                // move backwards to look for green piece
                while (foundPiece == false && k < 3) {

                    // if piece is in middle row
                    if (nextPosition[0] == i) {
                    
                        // and if piece in first column, move down
                        if (nextPosition[1] == 0) {
                            nextPosition[0]++;
                        }
                    
                        // or if any other column move left
                        else {
                            nextPosition[1]--;
                        }
                    }

                    // else piece is on bottom row
                    else {
                        // move right
                        nextPosition[1]++;
                    }
                    
                    // check for green piece
                    if (board[nextPosition[0]][nextPosition[1]] == game.GREENSTONE) {
                        foundPiece = true;
                    }
                    k++;
                }

                // if there is a piece found, add 1 to noInRange
                if (foundPiece == true) {
                    noInRange++;
                }
            }
        }
        return noInRange;
    }

    /**
     * Method to find the number of ai pieces in range of user pieces
     * @param board, the current board state
     * @return int, the number of ai stones that can be taken
     */
    private int findNoPiecesInRange(int[][] board) {
        int noInRange = 0;

        /**
         * find all green pieces on board, (only middle row needs to be checked because
         * they are the only pieces in danger)
         */
        int i = 1;
        for (int j = 0; j < 8; j++) {
            if (game.board[i][j] == game.GREENSTONE) {
                // check if there is a red piece within 3 squares behind
                boolean foundPiece = false;

                // set next position to the current position
                int[] nextPosition = new int[2];
                nextPosition[0] = i;
                nextPosition[1] = j;

                int k = 0;
                // move backwards to look for green piece
                while (foundPiece == false && k < 3) {
                    // if piece is in middle row
                    if (nextPosition[0] == i) {
                        // and if piece in first column, move up
                        if (nextPosition[1] == 0) {
                            nextPosition[0]--;
                        }
                        // or if any other column move left
                        else {
                            nextPosition[1]--;
                        }
                    }
                    // else piece is on top row
                    else {
                        // move right
                        nextPosition[1]++;
                    }
                    // check for red piece
                    if (board[nextPosition[0]][nextPosition[1]] == game.REDSTONE) {
                        foundPiece = true;
                    }
                    k++;
                }
                // if there is a piece found, add 1 to noInRange
                if (foundPiece == true) {
                    noInRange++;
                }
            }
        }

        return noInRange;
    }
}
