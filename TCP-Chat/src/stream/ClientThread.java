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
	
    static int nbClients;

	ClientThread(Socket s) {
		this.clientSocket = s;
	}

 	/**
  	* receives a request from client then sends an echo to the client
  	* @param clientSocket the client socket
  	**/
	public void run() {
        try {
            BufferedReader socIn = null;
            socIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));    
            PrintStream socOut = new PrintStream(clientSocket.getOutputStream());
            while (true) {
                String line = socIn.readLine();
                System.out.println(clientSocket.getInetAddress()+": "+line);
                socOut.println(line);
                System.out.println("Number of clients : " + ChatServer.nbCT);
                for (int i=0; i< ChatServer.nbCT; i = i+1){
                    ChatServer.listCT[i].printHello();
                }
            }
    	} catch (Exception e) {
        	System.err.println("Error in EchoServer:" + e); 
        }
    }
    
    public void printHello() {
        try {
            //PrintStream socOut = new PrintStream(clientSocket.getOutputStream());
            //socOut.println("hello");
        } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e); 
        }
    }

}
