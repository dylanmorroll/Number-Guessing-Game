package CO2017.exercsie3.dm338.client;
// SquareClient class

import java.net.*;
import java.io.*;

public class GuessGameClient {

    public static void main (String args[]) {
        
        // Code to read in arguments from command line
//        if (args.length != 2) {
//            System.err.println("Usage: java GuessGameClient <host> <port>");
//            System.exit(1);
//        }
//        
//        String servername = args[0];
//        int port = Integer.parseInt(args[1]);

        String servername = "localhost";
        int port = 8080;

        try (Socket server = new Socket(servername, port)) {
            
            System.out.println("Connected to " + server.getInetAddress());

            BufferedReader serverIn =
                new BufferedReader (
                    new InputStreamReader(server.getInputStream(), "UTF-8"));

            BufferedReader userIn =
                new BufferedReader(new InputStreamReader(System.in));

            Writer serverOut =
                new OutputStreamWriter(server.getOutputStream());

            String initialStartMessage = serverIn.readLine();
            System.out.println(initialStartMessage);
            
            String result;
            
            do {
                
                System.out.print("Enter guess: ");
                String userInput = userIn.readLine();
                System.out.println("Guess made");

                serverOut.write(String.format("%s%n", userInput));
                serverOut.flush();
                System.out.println("Guess sent");

//                if (num != 999) {
//                    result = Integer.parseInt(serverIn.readLine());
//                    System.out.printf("Server says %d x %d = %d%n",
//                            num,
//                            num,
//                            result);
//                }
                
                result = serverIn.readLine();
                System.out.println("Response receieved");
                System.out.println(result);
                
            } while (!result.startsWith("WIN"));
            
            System.out.println("Client shutdown");
            server.close();
        }
        
        catch (UnknownHostException e) {
            System.err.println("Unknown host: "+servername);
            System.err.println(e);
            System.exit(1);
        }
        
        catch (IOException e) {
            System.err.println(e);
            System.exit(1);
        }
    }
}
