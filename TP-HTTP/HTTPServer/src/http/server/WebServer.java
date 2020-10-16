///A Simple Web Server (WebServer.java)

package http.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

/**
 * Example program from Chapter 1 Programming Spiders, Bots and Aggregators in
 * Java Copyright 2001 by Jeff Heaton
 * 
 * WebServer is a very simple web-server. Any request is responded with a very
 * simple web-page.
 * 
 * @author Jeff Heaton
 * @version 1.0
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
      // create the main server socket
      s = new ServerSocket(3000);
    } catch (Exception e) {
      System.out.println("Error: " + e);
      return;
    }

    System.out.println("Waiting for connection");
    for (;;) {
      try {
        // wait for a connection
        Socket remote = s.accept();
        // remote is now the connected socket
        BufferedReader in = new BufferedReader(new InputStreamReader(
            remote.getInputStream()));
        PrintWriter out = new PrintWriter(remote.getOutputStream());

        // Parse data from the header
        Map<String, String> parameters = new HashMap<String, String>();

        // First header line
        String str = in.readLine();
        if (str == null || str.equals("")) return;
        
        String[] args = str.split("\\s");
        if (args.length >= 2) {
            parameters.put("method", args[0]);
            parameters.put("resource", args[1]);
            parameters.put("version", args[2]);
        } else return;

        // Check request (only handle GET requests for now)
        if (!parameters.get("method").equals("GET")) return;
        
        // Parse header parameters
        while (str != null && !str.equals("")) {
            str = in.readLine();
            args = str.split(": ");
            if (args.length > 1) {
                parameters.put(args[0], args[1]);
            }
        }
        
        // Send the requested resource
        BufferedReader reader;
		try {
            reader = new BufferedReader(new FileReader("../doc" + parameters.get("resource")));
                
            // Send the headers
            out.println("HTTP/1.0 200 OK");
            out.println("Content-Type: text/html"); // TODO: Verify content-type (image/png, ...)
            out.println("Server: Bot");
            out.println("");

            // Send the file
			String line = reader.readLine();
			while (line != null) {
                out.println(line);
				line = reader.readLine();
			}
            reader.close();

		} catch (FileNotFoundException e) {
            out.println("HTTP/1.0 404 Not Found");
            out.println("Content-Type: text/html");
            out.println("Server: Bot");
            out.println("");
            out.println("<p>Not found (404)</p>");
        } catch (IOException e) {
            e.printStackTrace();
            out.println("HTTP/1.0 500 Internal Server Error");
            out.println("Content-Type: text/html");
            out.println("Server: Bot");
            out.println("");
            out.println("<p>Internal Server Error (500)</p>");
		}

        out.flush();

        remote.close();
      } catch (Exception e) {
        System.out.println("Error: " + e);
      }
    }
  }

  /**
   * Start the application.
   * 
   * @param args
   *            Command line parameters are not used.
   */
  public static void main(String args[]) {
    WebServer ws = new WebServer();
    ws.start();
  }
}
