package CO2017.exercsie3.dm338.server;

import java.io.Writer;
import java.util.Random;
import java.util.concurrent.TimeUnit;

// Class to represent the state of an instance of the guessing game
public class GameState implements Runnable {
    
    // class constant: minimum allowed guess (set to 1)
    public static final int MINVAL = 1;
    
    // attribute to store a Random generator instance, can then be used to create a random number
    private static final Random RANDGEN = new Random();
    
    // The target for this game: a random number between MINVAL and maxValue
    private int target;
    
    // The maximum value the target can be
    private int maxValue;
    
    // The time limit of the game
    private long timeLimit;
    
    // A writer object to send messages direct to the client
    private Writer output;
    
    // The handler running that created this GameState object
    private GuessGameServerHandler handler;
    
    // The time at the beginning of the game to calculate relative difference
    long initialTime;
    
    // The number of guesses so far
    private int numOfGuesses;
    
    // Whether the game is over
    private boolean gameOver = false;
    
    // How much time is remaining (or, the actual time when the game will be over). (Phase 3 only.)
    // Note that the time does not start until the run() method is invoked.
    private long timeRemaining;
    
    // The last guess
    private int lastGuess;
    
    // Whether the game was won or not
    private boolean won;

    // CONSTRUCTORS
    // Setup an instance of the game.
    GameState(int mv, long tl, Writer o, GuessGameServerHandler ggsh) {
        
        // Assign passed variables to local variables
        // The maximum value to be used in the guessing game; guesses will be in the range MINVAL..mv
        maxValue = mv;
        // The time limit (in milliseconds) for the game
        timeLimit = tl;
        // Output buffer attached to the client playing the game; can be used to directly send messages to the client
        output = o;
        // The handler that is playing this instance of the game. (Phase 2 onwards)
        handler = ggsh;
        
        // Set the remaining time to the intial time
        timeRemaining = tl;
        
        // Generate a target value
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
        
        // Get the time at the beginning of the game
        initialTime = TimeUnit.MILLISECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS);
        
        // Start the timer
        while (timeRemaining > 0 && !finished()) {
            
            // Calculate the remaining time by the relative difference from the initial time (time elapsed)
            long currentTime = TimeUnit.MILLISECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS);
            long timeElapsed = currentTime - initialTime;
            // and subtracting it from the time limit
            timeRemaining = timeLimit - timeElapsed;
        }
        
        // If the time limit ends and the game hasn't been won yet
        System.out.println(gameOver);
        System.out.println(finished());
        if (!finished()) {

            // End the game
            gameOver = true;
            won = false;
    
            // Send a message to the user that the game is over
            String response = String.format("LOSE:%d%n", numOfGuesses);
            try {
                output.write(response);
                output.flush();
            
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // Print information about the guess
            System.out.printf("%c - (LOSE)--%.1fs/%d%n",
                    handler.getClientLetter(),
                    timeRemaining/1000.0,
                    numOfGuesses);
        }
    }
    
    // Change the state of the game according to the guess (validation is performed prior to method call)
    public void guess(int g) {
        
        // Set the last guess and increase the number of guess counter
        lastGuess = g;
        numOfGuesses++;
        
        // If the guess was correct and there is still time remaining, the user has won the game
        if (g == target && timeRemaining > 0) {
            gameOver = true;
            won = true;
        }
    }
    
    // Short string describing the state of the game based on the time remaining and the most recent guess
    public String toString() {
        
        // If a guess has been made
        if (numOfGuesses != 0) {
            
            // If the game is over
            if (finished()) { 
                
                // If the player won the game, return WIN
                if (won) {
                    return "WIN";
                    
                // If the player didn't win the game, return LOSE
                } else {
                    return "LOSE";
                }
             
            // If the game is not over, and the last guess was lower than the target
            // return LOW
            } else if (lastGuess < target) {
                return "LOW";
            
            // If the last guess was higher than the target return LOW
            } else if (lastGuess > target) {
                return "HIGH";
            }
            
        // If a guess hasn't been made return an empty string
        } else {
            return "";
        }
        
        // If something goes wrong return error
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
