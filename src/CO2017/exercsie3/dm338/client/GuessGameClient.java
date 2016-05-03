package CO2017.exercsie3.dm338.client;
// SquareClient class

import java.net.*;
import java.io.*;

public class GuessGameClient {

    // The client class is comprised of a single main method
    public static void main (String args[]) throws IOException {
        
        // Code to read in arguments from command line
        if (args.length != 2) {
            System.err.println("Usage: java GuessGameClient <host> <port>");
            System.exit(1);
        }
        
        // Assign arguments to variables
        String servername = args[0];
        int port = Integer.parseInt(args[1]);

        // Try and make a connection to the server
        try (Socket server = new Socket(servername, port)) {

            // Create server and user I/O streams
            BufferedReader serverIn =
                new BufferedReader (
                    new InputStreamReader(server.getInputStream(), "UTF-8"));
            BufferedReader userIn =
                new BufferedReader(new InputStreamReader(System.in));
            Writer serverOut =
                new OutputStreamWriter(server.getOutputStream());

            // Retrieve the start message and print out the information
            String startMsg = serverIn.readLine();
            String[] parts = startMsg.split(":");
            System.out.printf("%s: range is 1..%s, time allowed is %ss%n",
                    // "START"
                    parts[0],
                    // The upper limit of the guess
                    parts[1],
                    // The time limit of the game
                    parts[2]);
            
            // Whether the game is over or not
            boolean finished;
            
            // While the game hasn't finished
            do {
                
                // Get the guess from the user
                System.out.print("Enter guess: ");
                String userInput;
                
                // Wait for a user input for 10 seconds before timing out
                long startTime = System.currentTimeMillis();
                while ((System.currentTimeMillis() - startTime) < 10 * 1000 && !userIn.ready()) {}
                
                if (userIn.ready()) {
                    userInput = userIn.readLine();
                    
                    // Send the guess to the server
                    serverOut.write(String.format("%s%n", userInput));
                    serverOut.flush();
                    
                } else {
                    
                    // Send the guess to the server
                    serverOut.write(String.format("%s%n", null));
                    serverOut.flush();

                    // Get the result returned by the server
                    String returnedMsg = serverIn.readLine();
                    String[] returnedParts = returnedMsg.split(":");
                    
                    // Use this string format
                    System.out.printf("Turn %s: %s%n",
                            // The turn number
                            returnedParts[1],
                            // Whether the guess was incorrect (ERR) or if they won or lost
                            returnedParts[0]);
                    
                    throw new IOException();
                }

                // Get the result returned by the server
                String returnedMsg = serverIn.readLine();
                String[] returnedParts = returnedMsg.split(":");
                
                // Game is over if the returned message starts with WIN or LOSE
                finished = (returnedParts[0].equals("WIN") || returnedParts[0].equals("LOSE"));
                
                // Print to the user the result of their last guess
                // If the game isn't over (and it's not an error message)
                if (!finished && !returnedParts[0].equals("ERR")) {
                    
                    // Use this string format
                    System.out.printf("Turn %s: %s was %s, %.1fs remaining%n",
                            // The turn number
                            returnedParts[2],
                            // The user's input
                            userInput,
                            // Whether the guess was high or low
                            returnedParts[0],
                            // The time remaining, in seconds
                            Integer.parseInt(returnedParts[1])/1000.0);
                
                // If the game is over, or there was an error with the guess
                } else {
                    
                    // Use this string format
                    System.out.printf("Turn %s: %s%n",
                            // The turn number
                            returnedParts[1],
                            // Whether the guess was incorrect (ERR) or if they won or lost
                            returnedParts[0]);
                }
                
            // Keep asking user for inputs until the game finishes
            } while (!finished);
            
            // Close connection after game has handed
            server.close();
            
            // Throw IOException "when its all over"
            throw new IOException();
        }
        
        // Catch any errors, print out relevant info and exit the program
        catch (UnknownHostException e) {
            System.err.println("Unknown host: "+servername);
            System.err.println(e);
            System.exit(1);
        }
        
        catch (IOException e) { }
    }
}
