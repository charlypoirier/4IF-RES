///A Simple Web Server (WebServer.java)

package http.server;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
import java.nio.file.Files;
import java.nio.file.*;
import java.io.*;
import java.util.*;
import java.util.stream.*;

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

        // Wait for a connection
        Socket remote = s.accept();

        // Remote is now the connected socket
        BufferedReader in = new BufferedReader(new InputStreamReader(remote.getInputStream()));
        OutputStream os = remote.getOutputStream();
        PrintWriter out = new PrintWriter(os);

        // Parse data from the header
        Map<String, String> parameters = new HashMap<String, String>();

        String str = in.readLine();
        if (str != null && !str.equals("")) {
            // First line
            String[] args = str.split("\\s");
            if (args.length >= 2) {
                parameters.put("method", args[0]);
                parameters.put("resource", args[1]);
                parameters.put("version", args[2]);
            }

            // Header parameters
            str = in.readLine();
            while (str != null && !str.equals("")) {
                args = str.split(": ");
                if (args.length > 1) {
                    parameters.put(args[0], args[1]);
                }
                str = in.readLine();
            }
        }
        

        // Handle request
        try {
            switch (parameters.get("method")) {
                case "GET":
                    GETHandler(parameters.get("resource"), os);
                    break;
                case "POST":
                    String bodyLine = ""; 
                    char c; 
                    for (int i=0; i< Integer.parseInt(parameters.get("Content-Length")) ;i++) {
                        c = (char) in.read();
                        bodyLine = bodyLine + c;        
                    }
                    System.out.println("> " + bodyLine);
                   /* 
                    while(bodyLine != null && bodyLine.length() > 0){
                        System.out.println(bodyLine);
                        bodyLine = in.readLine();
                    }*/

                    POSTHandler(parameters.get("resource"), out, in, bodyLine);
                    break;
                case "HEAD":
                    HEADHandler(parameters.get("resource"), out);
                    break;
                case "PUT":
                    String body = "This is just a test";
                    PUTHandler(parameters.get("ressource"), out, body);
                    break;
                case "DELETE":
                    DELETEHandler(parameters.get("resource"), out);
                    break;
                default:
                    out.println("HTTP/1.0 400 Bad Request");
                    out.println("Content-Type: text/html");
                    out.println("Server: Bot");
                    out.println("");
                    out.println("<p>Bad request (400)</p>");
            }
        } catch (FileNotFoundException e) {
            out.println("HTTP/1.0 404 Not Found");
            out.println("Content-Type: text/html");
            out.println("Server: Bot");
            out.println("");
            out.println("<p>Not found (404)</p>");
        } catch (Exception e) {
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
        e.printStackTrace();
      }
    }
  }
    
    public void GETHandler(String ressource, OutputStream os) throws FileNotFoundException, IOException {
        
        PrintWriter out = new PrintWriter(os);

        /*
            The HTTP GET method requests a representation of the specified resource.
            Requests using GET should only retrieve data.
        */

        System.out.println("GET " + ressource);

        if (ressource.equals("/")) {
            ressource = "/index.html";
        }

        Path path = Paths.get("../public" + ressource);
        String type = Files.probeContentType(path);
        
        if (type == null) {
            type = "text/html";
        }
        
        File file = new File(path.toString());

        if (type.startsWith("text")) {
            
            BufferedReader reader = new BufferedReader(new FileReader(file));
            
            out.println("HTTP/1.0 200 OK");
            out.println("Content-Type: " + type);
            out.println("Server: Bot");
            out.println("");

            String line = reader.readLine();
            while (line != null) {
                out.println(line);
                line = reader.readLine();
            }

            reader.close();
            out.flush();

        } else {
            /*
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            */

            // https://commons.apache.org/proper/commons-io/javadocs/api-2.5/org/apache/commons/io/FileUtils.html#readFileToByteArray(java.io.File)
        }


    }
    
    public void POSTHandler(String ressource, PrintWriter out, BufferedReader in, String body) throws FileNotFoundException, IOException {
        System.out.println("POST " +ressource);

        
        //POST is used to send data to a server to create/update a resource.
        //The data sent to the server with POST is stored in the request body of the HTTP request:
    
        // Example of Post request from https://www.tutorialspoint.com/http/http_methods.htm

        // In this lab work, we are just displaying post data.

        System.out.println(body);


        
        // Sending header 
        out.println("HTTP/1.0 200 OK");
        out.println("Content-Type: text/html");
        out.println("Server: Bot");
        
        //Blank line at the end of header
        out.println("");


        File rFile = new File("../public" + ressource);
        boolean exist = rFile.exists();

    }

    public void HEADHandler(String ressource, PrintWriter out) throws FileNotFoundException, IOException {

        /*
            The HTTP HEAD method requests the headers that would be returned if
            the HEAD request's URL was instead requested with the HTTP GET method.
        */

        // Displaying requested ressources
        System.out.println("HEAD " + ressource);

        // Check if the file exists
        Path path = Paths.get("../public" + ressource);
        String type = Files.probeContentType(path);
        File file = new File(path.toString());

        if (file.exists() && file.isFile()) {
            out.println("HTTP/1.0 200 OK");
            out.println("Content-Type: " + type);
            out.println("Content-Length: " + file.length());
            out.println("Server: Bot");
            out.println("");
        } else {
            out.println("HTTP/1.0 404 Not Found");
            out.println("Server: Bot");
            out.println("");
        }
        
    }

    public void PUTHandler(String filename, PrintWriter out, String body) throws FileNotFoundException, IOException {
        System.out.println("Handling a PUT Method");
    
    
        System.out.println(body);


        // Write in file // for put.
        BufferedWriter outf = null; 
        FileWriter fstream = new FileWriter("out.txt", true); //true tells to append data.
        outf = new BufferedWriter(fstream);
        outf.write(body);
     
        // Sending header 
        out.println("HTTP/1.0 200 OK");
        out.println("Content-Type: text/html");
        out.println("Server: Bot");
        
        //Blank line at the end of header
        out.println("");
    }

    public void DELETEHandler(String ressource, PrintWriter out) throws FileNotFoundException, IOException {

        /*
            The HTTP DELETE request method deletes the specified resource.
        */

        // Displaying requested ressources
        System.out.println("DELETE " + ressource);

        // Check if the file exists
        File file = new File("../public" + ressource);
        if (file.exists() && file.isFile() && file.delete()) {
            out.println("HTTP/1.0 204 No Content");    
            out.println("Server: Bot");
            out.println("");
        } else {
            out.println("HTTP/1.0 404 Not Found");    
            out.println("Server: Bot");
            out.println("");
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
