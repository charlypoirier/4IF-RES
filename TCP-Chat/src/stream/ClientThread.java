/***
 * ClientThread
 * Date: 13/10/2020
 * Authors: Jérôme Hue, Charly Poirier
 */

package stream;

import java.io.*;
import java.net.*;

public class ClientThread
	extends Thread {
	
	private Socket clientSocket;
	
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
            }
    	} catch (Exception e) {
        	System.err.println("Error in EchoServer:" + e); 
        }
    }
}