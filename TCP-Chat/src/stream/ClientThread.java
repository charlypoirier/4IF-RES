/***
 * ClientThread
 * Example of a TCP server
 * Date: 14/12/08
 * Authors:
 */

package stream;

import java.io.*;
import java.net.*;

public class ClientThread extends Thread {
	
	private Socket clientSocket;
    private PrintStream socOut;
	
    static int nbClients;

	ClientThread(Socket s) {
		this.clientSocket = s;
        try {
            this.socOut = new PrintStream(s.getOutputStream());
    	} catch (Exception e) {
        	System.err.println("Error in ChatServer:" + e); 
        }
	}

 	/**
  	* Receives a message from client and sends it back to everyone
  	* @param clientSocket the client socket
  	**/
	public void run() {
        try {
            BufferedReader socIn = null;
            socIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));    
            while (true) {
                String message = socIn.readLine();
                if (message == null) {  break; }
                System.out.println(clientSocket.getInetAddress()+": "+message);
                //System.out.println("Number of clients : " + ChatServer.nbCT);
                for (int i=0; i<ChatServer.nbCT; i=i+1) {
                    ChatServer.listCT[i].send(message);
                }
            }
    	} catch (Exception e) {
        	System.err.println("Error in ChatServer:" + e); 
        }
    }
    
    public void send(String message) {
        try {
            this.socOut.println(message);
        } catch (Exception e) {
            System.err.println("Error in ChatServer:" + e); 
        }
    }

}
