/***
 * ChatClient.java
 * TCP client for a socket-based chat system
 * Date: 13/10/2020
 * Authors: Jérôme Hue, Charly Poirier
 */
package stream;

import java.io.*;
import java.net.*;

/**
 * Client class for the TCP chat
 */
public class ChatClient {
 
    /**
     * Main method
     * 
     * Accepts a connection, creates a client thread,
     * receives and sends messages
     * 
     * @param args arguments [server_address, port]
     *
     * @throws IOException Thrown when server had problems closing sockets
     */
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
      	    serverSocket = new Socket(args[0], new Integer(args[1]).intValue());
            socIn = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            socOut = new PrintStream(serverSocket.getOutputStream());
            stdIn = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("[Connected]");
        } catch (UnknownHostException e) {
            System.err.println("Unknown host:" + args[0]);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Error while connecting to " + args[0]);
            System.exit(1);
        }
        
        // Listen for incoming messages (new thread)
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
