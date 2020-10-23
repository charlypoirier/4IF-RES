
package http.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.*;
import java.io.*;
import java.util.*;

/**
 * A web server that handles GET, HEAD, POST, PUT and DELETE
 * requests, multiples error codes and media files.
 * 
 * @author Jérôme Hue, Charly Poirier
 */
public class WebServer {

    /**
     * WebServer constructor.
     */
    protected void start() {
        ServerSocket s;

        System.out.println("Webserver starting up on port 80");
        System.out.println("(press ctrl-c to exit)");
        try {
            // Create the main server socket
            s = new ServerSocket(3000);
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return;
        }

        System.out.println("Waiting for connection");

        for (;;) {
            try {
                Socket remote = s.accept(); // Wait for a connection
                RequestHandler thread = new RequestHandler(remote); // Create a new thread to handle request
                thread.run();
            } catch (Exception e) {
                System.out.println("Error: " + e);
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Start the application.
     * 
     * @param args Command line parameters are not used
     */
    public static void main(String args[]) {
        WebServer ws = new WebServer();
        ws.start();
    }
}
