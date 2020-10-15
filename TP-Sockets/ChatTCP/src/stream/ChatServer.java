/***
 * ChatServer.java
 * TCP server for a socket-based chat system
 * Date: 13/10/2020
 * Authors: Jérôme Hue, Charly Poirier
 */

package stream;

import java.io.*;
import java.net.*;
import java.util.*;


public class ChatServer  {

    static ClientThread[] listCT = new ClientThread[100];
    static int nbCT = 0;
    static ArrayList<String> history = new ArrayList<String>();

 	/**
  	* main method
	* @param ChatServer port
  	* 
  	**/
    public static void main(String args[]){ 
        ServerSocket listenSocket;
  	    if (args.length != 1) {
            System.out.println("Usage: java ChatServer <ChatServer port>");
            System.exit(1);
  	    }
	    try {
		    listenSocket = new ServerSocket(Integer.parseInt(args[0])); //port
		    System.out.println("Server ready..."); 
		    while (true) {
			    Socket clientSocket = listenSocket.accept();
			    System.out.println("Connexion from:" + clientSocket.getInetAddress());
			    ClientThread ct = new ClientThread(clientSocket);
                ct.start();

		        incrementCT(ct); 
            }
        } catch (Exception e) {
            System.err.println("Error in ChatServer:" + e);
        }
    }

    public static synchronized void incrementCT(ClientThread ct ){
        // Update global variables.
        listCT[nbCT] = ct;
        nbCT = nbCT +1;
    }
}

  
