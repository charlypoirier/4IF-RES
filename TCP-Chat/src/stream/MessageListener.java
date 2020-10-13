/***
 * MessageListener
 * Example of a TCP server
 * Date: 14/12/08
 * Authors:
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
            System.out.println("[Disconnected]");
            System.exit(1);
    	} catch (Exception e) {
        	System.err.println("Error in MessageListener:" + e); 
        }
    }
}
