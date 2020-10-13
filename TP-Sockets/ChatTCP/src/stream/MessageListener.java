/***
 * MessageListener.java
 * Thread to handle incoming messages
 * Date: 13/10/2020
 * Authors: Jérôme Hue, Charly Poirier
 */

package stream;

import java.io.*;
import java.net.*;

public class MessageListener extends Thread {
	
	private BufferedReader input;

    MessageListener(BufferedReader input) {
		this.input = input;
	}

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
