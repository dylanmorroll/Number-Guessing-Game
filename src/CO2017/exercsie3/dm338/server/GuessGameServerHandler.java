package CO2017.exercsie3.dm338.server;

import java.net.*;
import java.io.*;

public class GuessGameServerHandler implements Runnable {
    
    // I/O streams
    private Writer serverOut;
    private BufferedReader serverIn;
    
    // Variables to hold passed in data
    private int maxValue;
    private long timeLimit;
    private Socket client;
    
    // Variable to hold the GameState object
    private GameState game;
    
    // Variables to create unique identifiers for each client's handler
    private static int clientID = 0;
    private char clientLetter;

    // CONSTRUCTORS
    public GuessGameServerHandler (int mv, long tl, Socket cl) {
        
        // Assign passed variables to local variables
        this.maxValue = mv;
        this.timeLimit = tl;
        this.client = cl;
        
        // Give the client a unique letter identifier
        clientLetter = (char) ('A' + clientID);
        clientID = (clientID + 1) % 26;
        
        // Get and display client's IP address
        InetAddress clientAddress = client.getInetAddress();
        System.out.printf("%c connection : %s%n",
                clientLetter, clientAddress);

        // Try and create an input and output stream
        try {
            serverOut = new OutputStreamWriter(client.getOutputStream());
            serverIn = new BufferedReader
                    (new InputStreamReader(client.getInputStream(), "UTF-8"));

            // Create a GameState object to keep track of the current game
            game = new GameState(maxValue, timeLimit, serverOut, this);
            
        // Catch any errors, print out relevant info and exit the program
        } catch (IOException e) {
            System.err.printf("Failed to create Data streams to %s%n",
                    client.getInetAddress());
            System.err.println(e);
            System.exit(1);
        }
    }

    // METHODS
    // The main method of the handler - running the game for the client
    public void run() {
        
        // Signal the execution of the thread
        System.out.printf("%c start watching%n", clientLetter);
        
        try {
            // Print the target value
            int target = game.getTarget();
            System.out.printf("%c target is %d%n",
                    clientLetter, target);
            
            // Create and send the initial start message to the client
            String startMsg = String.format("START:%d:%d%n", maxValue, timeLimit/1000);
            serverOut.write(startMsg);
            serverOut.flush();
            new Thread(game).start();
            
            // Continue to process guesses from the user till the game is over
            while (!game.finished()) {
                
                // Get the guess from the client and turn into an integer
                String guessInput = serverIn.readLine();
                
                // Create a variable to hold the response to be sent to the client
                String response;
                
                // Get the number of valid turns made so far
                int turns = game.getGuesses();
                
                // Information about the guess to be printed
                String info;
                
                // Try turn guess into an int, if an exception is thrown the guess isn't valid
                try {
                    // Validate input and turn to integer
                    int guess = Integer.parseInt(guessInput);
                    
                    // If the guess is within the specified values, it's valid
                    if (GameState.MINVAL <= guess && guess <= maxValue) {
                        
                        // Get the time remaining in the game
                        long rem = game.getTimeRemaining();
                        
                        // Simulate a guess using the GameState object
                        game.guess(guess);
                        
                        // Get the new number of valid turns made (+1)
                        turns = game.getGuesses();
                        
                        // Send the string to the game client to print a message to the user
                        // If the game is over
                        if (game.finished()) {
                            
                            // Use this string format
                            response = String.format("%s:%d%n",
                                    // The GameState toString (WON, LOSE)
                                    game.toString(),
                                    // The number of turns that has been made
                                    turns);
                        
                        // If the game is not over    
                        } else {
                            
                            // Use this string format
                            response = String.format("%s:%d:%d%n",
                                    // The GameState toString (HIGH, LOW)
                                    game.toString(),
                                    // The time remaining
                                    rem,
                                    // The number of turns remaining
                                    turns);
                        }
                        
                        info = game.toString();
                    
                    // Otherwise, if the guess is not an integer, or between the specified values
                    // the guess isn't valid, so return ERR and the number of turns
                    } else {
                        response = String.format("ERR:%d%n", turns);
                        info = "ERR out of range";
                    }
                } catch (Exception e) {
                    response = String.format("ERR:%d%n", turns);
                    info = "ERR non-integer";
                }
                
                // If the game hasn't finished (otherwise GameState prints the info)
                if (!game.finished())
                
                    // Print information about the guess
                    System.out.printf("%c %d (%s)-%.1fs/%d%n",
                            clientLetter,
                            target,
                            info,
                            game.getTimeRemaining()/1000.0,
                            turns);
                
                // Send the response message
                serverOut.write(response);
                serverOut.flush();
            }
            
            // Signify the end of the game
            System.out.printf("%c Game over%n", clientLetter);
            client.close();
            
        } catch (IOException e) {
            System.err.println(e);
        }
    }
    
    // GETTERS & SETTERS
    public char getClientLetter() {
        return clientLetter;
    }
}
