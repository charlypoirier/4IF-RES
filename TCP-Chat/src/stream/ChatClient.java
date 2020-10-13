/***
 * ChatClient
 * TCP Client for a socket-based chat system
 * Date: 13/10/2020
 * Authors: Jérôme Hue, Charly Poirier
 */
package stream;

import java.io.*;
import java.net.*;

public class ChatClient {
 
    /**
    *  Main method
    *  Accepts a connection, receives and sends messages 
    **/
    public static void main(String[] args) throws IOException {

        Socket serverSocket  = null;
        PrintStream socOut   = null;
        BufferedReader stdIn = null;
        BufferedReader socIn = null;

        if (args.length != 2) {
            System.out.println("Usage: java ChatClient <ChatServer host> <ChatServer port>");
            System.exit(1);
        }

        try {
            // Connection socket
      	    serverSocket = new Socket(args[0], new Integer(args[1]).intValue());
            // Pipe for incoming messages
            socIn = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            // Pipe for outcoming messages
            socOut= new PrintStream(serverSocket.getOutputStream());
            // Standard output (terminal)
            stdIn = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("[Connected]");
        } catch (UnknownHostException e) {
            System.err.println("Unknown host:" + args[0]);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Error while connecting to " + args[0]);
            System.exit(1);
        }
        
        // Listen for incoming messages (thread)
        MessageListener ct = new MessageListener(socIn);
        ct.start();

        // Listen for user input and send messages
        String message;
        while (true) {
        	message = stdIn.readLine();
            if (message.equals(".")) break;
        	socOut.println(message);
        }
        socOut.close();
        socIn.close();
        stdIn.close();
        serverSocket.close();
    }
}
