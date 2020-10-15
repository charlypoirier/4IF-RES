/***
 * ClientThread.java
 * Thread to handle clients
 * Date: 13/10/2020
 * Authors: Jérôme Hue, Charly Poirier
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
        
        // Display message history
        if (ChatServer.history.size() > 0) {
            for (int i = 0; i< ChatServer.history.size() ; i = i+1) {
                ChatServer.listCT[ChatServer.nbCT - 1].send(ChatServer.history.get(i));
            }
        }
        
        
        try {
            BufferedReader socIn = null;
            socIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));    
            while (true) {
                String message = socIn.readLine();
                if (message == null) {  break; }
                
                // Display message on server
                System.out.println(clientSocket.getInetAddress()+": "+message);
                
                // Store message in history
                ChatServer.history.add(message);
             
                // Store message in file
                FileWriter fwriter = new FileWriter("history.txt", true);
                fwriter.write(message + "\n");
                fwriter.close();


                System.out.println("Messages in chat history : "+ ChatServer.history.size());

                // Send message to everyone 
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
