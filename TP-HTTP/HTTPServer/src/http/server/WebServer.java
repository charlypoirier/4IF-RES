///A Simple Web Server (WebServer.java)

package http.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.FileReader;
import java.io.IOException;
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
        System.out.println("Connection, sending data.");
        BufferedReader in = new BufferedReader(new InputStreamReader(
            remote.getInputStream()));
        PrintWriter out = new PrintWriter(remote.getOutputStream());

        // read the data sent. We basically ignore it,
        // stop reading once a blank line is hit. This
        // blank line signals the end of the client HTTP
        // headers.
        String str = "."; 
        while (str != null && !str.equals("")) {
            //System.out.println(str);
            String[] Tokens = str.split(" ");
            if (Tokens[0].equals("GET") ) {
                // System.out.println("stringValidTokens : " + stringValidTokens[0]);
                GETHandler(Tokens[1], out);
            }
            //System.out.println("stringValidTokens : " + stringValidTokens[0]);
            str = in.readLine();
        }
         
       /* 
        // Send the response
        
        // Send the headers
        out.println("HTTP/1.0 200 OK");
        out.println("Content-Type: text/html");
        out.println("Server: Bot");
        
        // this blank line signals the end of the headers
        out.println("");
        
        // Send the HTML page
        BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader("../doc/index.html"));
			String line = reader.readLine();
			while (line != null) {
                out.println(line);
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

        //out.println("<H1>Welcome to the Ultra Mini-WebServer</H2>");
        out.flush();
        */
        remote.close();
      } catch (Exception e) {
        System.out.println("Error: " + e);
      }
    }
  }

    
    public void GETHandler(String ressource, PrintWriter out) {
        
        // Displaying requested ressources
        System.out.println("GET " +ressource);
       
        // Sending header 
        out.println("HTTP/1.0 200 OK");
        out.println("Content-Type: text/html");
        out.println("Server: Bot");
        
        // this blank line signals the end of the headers
        out.println("");

        // Send the html page requested
        BufferedReader reader;
		try {
            if (!ressource.equals("/")) {
			reader = new BufferedReader(new FileReader("../doc" + ressource));
            } else {
            
			reader = new BufferedReader(new FileReader("../doc/index.html"));
            }
            
            String line = reader.readLine();
			while (line != null) {
                out.println(line);
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

        out.flush();
        
    }
    
    public void POSTHandler(String ressource, PrintWriter out) {
        System.out.println("POST " +ressource);

        //POST is used to send data to a server to create/update a resource.
        //The data sent to the server with POST is stored in the request body of the HTTP request:
    
        File rFile = new File("../doc" + ressource);
        boolean exist = rFile.exists();

        // FileOutputStream(File file, boolean append)
        FileOutputStream fos = new FileOutputStream(rFile, exist);

        
    

    }

    public void HEADHandler(String ressource, PrintWriter out) {
        System.out.println("Handling a HEAD Method");
		try {
			// Vérification de l'existence de la ressource demandée
			File rFile = new File("../doc" + ressource);
			if(rFile.exists() && rFile.isFile()) {
				
                  
            // Sending header 
            out.println("HTTP/1.0 200 OK");
            out.println("Content-Type: text/html");
            out.println("Server: Bot");
            out.println("");
            
			
            } else {}
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    public void PUTHandler() {
        System.out.println("Handling a PUT Method");
    }

    public void DELETEHandler() {
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
