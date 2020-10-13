/***
 * ChatServer
 * Example of a TCP server
 * Date: 10/01/04
 * Authors:
 */

package stream;

import java.io.*;
import java.net.*;

public class ChatServer  {
  
 	/**
  	* main method
	* @param ChatServer port
  	* 
  	**/

    static ClientThread[] listCT = new ClientThread[100];
    static int nbCT = 0;

    public static void main(String args[]){ 
        ServerSocket listenSocket;
        
        nbCT  = 0;

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

                // Update global variables.
                // !!! How to protect variables integrity ?
                listCT[nbCT] = ct;
                nbCT = nbCT +1;
		         
            }
        } catch (Exception e) {
            System.err.println("Error in ChatServer:" + e);
        }
      }
  }

  
