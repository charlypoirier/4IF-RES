/***
 * MessageListener.java
 * Thread to handle incoming messages
 * Date: 13/10/2020
 * Authors: Jérôme Hue, Charly Poirier
 */
package stream;

import java.io.*;
import java.net.*;

/**
 * Thread that listens for incoming messages
 */
public class MessageListener extends Thread {
	
	private BufferedReader input;

    /**
     * MessageListener Constructor
     * 
     * @param input BufferedReader
     */
    MessageListener(BufferedReader input) {
		this.input = input;
	}

    /**
     * Start the thread
     * 
     * This will listen for messages
     * and print them on the standard output
     */
	public void run() {
        try {
            while (true) {
                String message = this.input.readLine();
                if (message != null) {
                    System.out.println("> " + message);
                } else break;
            }
    	} catch (Exception e) {
        	System.err.println("Error in MessageListener:" + e); 
        }
        System.out.println("[Disconnected]");
        System.exit(1);
    }
}
