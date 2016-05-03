package CO2017.exercsie3.dm338.server;

import java.net.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.io.*;

public class GuessGameServer {
    
    public static void main (String[] args) {

        // Code to read in arguments from command line
        if (args.length != 3) {
            System.err.println("Usage: java GuessGameServer <port> <max_guess> <time_limit>");
            System.exit(1);
        }

        // Assign the three command line arguments
        int port = Integer.parseInt(args[0]);
        int maxValue = Integer.parseInt(args[1]);
        long timeLimit = Integer.parseInt(args[2]); // in seconds
        // Turn the timeLimit into milliseconds
        timeLimit = timeLimit * 1000;

        // Try and create a server socket for the specified port
        try (ServerSocket server = new ServerSocket(port)) {
            
            // Print starting message
            System.out.printf("Starting GuessGame server (%d, %d) on port %d%n",
                    maxValue, timeLimit, port);
            
            // Create a thread pool for all handler threads
            ThreadPoolExecutor gameThreadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
            
            // Infinitely wait for new clients
            while (true) {
                
                // Wait for a new client and create a socket when one arrives
                Socket client = server.accept();
                
                // Create a handler object for the client and execute it from the thread pool
                GuessGameServerHandler handler = new GuessGameServerHandler(maxValue, timeLimit, client);
                Thread game = new Thread(handler);
                gameThreadPool.execute(game);
            }
            
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
