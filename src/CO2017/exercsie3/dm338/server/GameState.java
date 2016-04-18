package CO2017.exercsie3.dm338.server;

import java.io.Writer;
import java.util.Random;

// Class to represent the state of an instance of the guessing game
public class GameState implements Runnable {
    
    // class constant: minimum allowed guess (set to 1)
    public static final int MINVAL = 1;
    
    // attribute to store a Random generator instance, can then be used to create a random number
    private static final Random RANDGEN = new Random();
    
    // The target for this game: a random number between MINVAL and maxValue
    private int target;
    
    private int maxValue;
    private long timeLimit;
    private Writer output;
    private GuessGameServerHandler handler;
    
    // The number of guesses so far
    private int numOfGuesses;
    
    // Whether the game is over
    private boolean gameOver = false;
    
    // How much time is remaining (or, the actual time when the game will be over). (Phase 3 only.)
    // Note that the time does not start until the run() method is invoked.
    private long timeRemaining;
    
    // The last guess
    private int lastGuess;

    /*
     * Setup an instance of the game.
     * 
     * mv - the maximum value to be used in the quessing game; guesses will be in the range MINVAL..mv
     * tl - the time limit (in milliseconds) for the game (ignore until phase 3)
     * o - output buffer attached to the client playing the game; can be used to directly send messages to the client (ignore until phase 3)
     * ggsh - the handler that is playing this instance of the game. (Phase 2 onwards)
     */
    GameState(int mv, long tl, java.io.Writer o, GuessGameServerHandler ggsh) {
        
        maxValue = mv;
        timeLimit = tl;
        output = o;
        handler = ggsh;
        
        target = RANDGEN.nextInt(maxValue) + 1;
        
    }

    // METHODS
    /* 
     * Start and monitor the game
     * Start the countdown of the available time
     * In phase 3, you should also monitor the remaining time and stop the game
       (by sending a message to the client) when time is up
     */
    @Override
    public void run() {
        // TODO Auto-generated method stub
        
    }
    
    // Change the state of the game according to the guess
    public void guess(int g) {
        lastGuess = g;
        numOfGuesses++;
        
        if (g == target) {
            gameOver = true;
        }
    }
    
    /* Short string describing the state of the game based on the time remaining and the most recent guess
     * 
     * Returns:
        LOSE
            if the game is over because time is up (phase 3 only)
        WIN
            if the player has guessed correctly within the time limit 
        LOW
            if the game is not over and the last guess was low
        HIGH
            if the game is not over and the last guess was high
        (empty string)
            if the game has not yet started 
     */
    public String toString() {
        
        if (numOfGuesses != 0) {
            if (lastGuess == target) {
                return "WIN";
                
            } else if (lastGuess < target) {
                return "LOW";
                
            } else if (lastGuess > target) {
                return "HIGH";
            }
            
        } else {
            return "";
        }
        
        return "ERROR";
    }
    
    // GETTERS & SETTERS
    // Get the target value for this game
    public int getTarget() {
        return target;
    }
    
    // Get the number of guesses made so far
    public int getGuesses() {
        return numOfGuesses;
    }
    
    // Is the game over?
    public boolean finished() {
        return gameOver;
    }
    
    // How many milliseconds before the game is over
    public long getTimeRemaining() {
        return timeRemaining;
    }
    
    
}
