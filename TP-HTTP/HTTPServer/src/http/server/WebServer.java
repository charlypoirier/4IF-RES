///A Simple Web Server (WebServer.java)

package http.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.io.File;

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

        // Parse header parameters
        while (str != null && !str.equals("")) {
            str = in.readLine();
            args = str.split(": ");
            if (args.length > 1) {
                parameters.put(args[0], args[1]);
            }
        }

        // Handle request
        try {
            if (parameters.get("method").equals("GET")) {
                GETHandler(parameters.get("resource"), out);
            }
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
    
    public void GETHandler(String ressource, PrintWriter out) throws FileNotFoundException, IOException {
        
        // Displaying requested ressources
        System.out.println("GET " +ressource);

        // Send the html page requested
        BufferedReader reader;

        if (!ressource.equals("/")) {
            // Open the file
            reader = new BufferedReader(new FileReader("../doc" + ressource));
            
            // Sending header 
            out.println("HTTP/1.0 200 OK");
            out.println("Content-Type: text/html");
            out.println("Server: Bot");
            
            // this blank line signals the end of the headers
            out.println("");
        } else {
            reader = new BufferedReader(new FileReader("../doc/index.html"));
        }
        
        String line = reader.readLine();
        while (line != null) {
            out.println(line);
            line = reader.readLine();
        }
        reader.close();
    }
    
    public void POSTHandler(String ressource, PrintWriter out) throws FileNotFoundException, IOException {
        System.out.println("POST " +ressource);

        //POST is used to send data to a server to create/update a resource.
        //The data sent to the server with POST is stored in the request body of the HTTP request:
    
        File rFile = new File("../doc" + ressource);
        boolean exist = rFile.exists();

        // FileOutputStream(File file, boolean append)
        FileOutputStream fos = new FileOutputStream(rFile, exist);
    }

    public void HEADHandler(String ressource, PrintWriter out) throws FileNotFoundException, IOException {
        System.out.println("Handling a HEAD Method");
        File rFile = new File("../doc" + ressource);
        if(rFile.exists() && rFile.isFile()) {
            // Sending header
            out.println("HTTP/1.0 200 OK");
            out.println("Content-Type: text/html");
            out.println("Server: Bot");
            out.println("");
        } else {}
        out.flush();
    }

    public void PUTHandler() throws FileNotFoundException, IOException {
        System.out.println("Handling a PUT Method");
    }

    public void DELETEHandler() throws FileNotFoundException, IOException {
        System.out.println("Handling a DELETE Method");
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
