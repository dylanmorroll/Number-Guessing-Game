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

        int port = Integer.parseInt(args[0]);
        int maxValue = Integer.parseInt(args[1]);
        long timeLimit = Integer.parseInt(args[2]); // in seconds

//        int port = 8080;

        try (ServerSocket server = new ServerSocket(port)) {
            
            ThreadPoolExecutor gameThreadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();

            while (true) {
                System.out.println("Waiting for client...");
                Socket client = server.accept();

                // get and display client's IP address
                InetAddress clientAddress = client.getInetAddress();
                System.out.printf("Client from %s connected%n",
                        clientAddress);

                GuessGameServerHandler handler = new GuessGameServerHandler(maxValue, timeLimit, client);
                Thread game = new Thread(handler);
                gameThreadPool.execute(game);
            }
            
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
