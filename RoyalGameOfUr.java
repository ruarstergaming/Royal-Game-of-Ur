
/**
 * This class has been created with the only purpose of putting together all the
 * game's necessary components in order to run it
 */
public class RoyalGameOfUr {

    /**
     * Runs the actual game
     */
    public static void main(String[] args) throws Exception {

        GameWindow gameWindow = new GameWindow();
        Game game = new Game(gameWindow);

        new Controller(gameWindow, game);
    }
}
