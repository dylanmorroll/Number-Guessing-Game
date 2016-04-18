package CO2017.exercsie3.dm338.server;

import java.net.*;
import java.util.Random;
import java.io.*;

public class GuessGameServerHandler implements Runnable {
    
    private Writer serverOut;
    private BufferedReader serverIn;
    
//    private Writer gameOut;
//    private BufferedReader gameIn;
    
    private int maxValue;
    private long timeLimit;
    private Socket client;
    
    private GameState game;

    public GuessGameServerHandler (int mv, long tl, java.net.Socket cl) {
        
        this.maxValue = mv;
        this.timeLimit = tl;
        this.client = cl;
        
        game = new GameState(maxValue, timeLimit, serverOut, this);
        
        try {
            
            serverOut = new OutputStreamWriter(client.getOutputStream());
            serverIn = new BufferedReader
                    (new InputStreamReader(client.getInputStream(), "UTF-8"));
            
//            gameIn = new BufferedReader(
//                new InputStreamReader(
//                    game.getInputStream(), "UTF-8"));
            
        } catch (IOException e) {
            
            System.err.printf("Failed to create Data streams to %s%n",
                    client.getInetAddress());
            System.err.println(e);
            System.exit(1);
        }
    }

    public void run() {
        
        try {
            int target = game.getTarget();
            System.out.println("Target: " + target);
            String startMsg = String.format("START:%d:%d%n", maxValue, 9999);
            serverOut.write(String.format("%s%n", startMsg));
            serverOut.flush();
            
            int guess;
            
            do {
                String guessInput = serverIn.readLine();
                guess = Integer.parseInt(guessInput);
                System.out.println("Guess recieved: " + guess);
                
                game.guess(guess);
                
                String tostring = game.toString();
                System.out.println("ToString: " + tostring);
                
                String response;
                
                if (tostring.equals("WIN")) {
                    response = String.format(format, args)
                }
                
                serverOut.write(String.format("%s%n", response));
                serverOut.flush();
                System.out.println("Response sent");
                
            } while (guess != target);
            
            System.out.printf("Service to %s completed%n",
                    client.getInetAddress());
            client.close();
            
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}