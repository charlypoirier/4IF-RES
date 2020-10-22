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
                
                System.out.println("Parameters : " + parameters) ;               
                // Handle request
                try {
                    switch (parameters.get("method")) {
                        case "GET":
                            GETHandler(parameters.get("resource"), os);
                            break;
                        case "POST":
                            int l = Integer.parseInt(parameters.get("Content-Length"));
                            POSTHandler(parameters.get("resource"), out, in,  l, parameters.get("Content-Type"));
                            break;
                        case "HEAD":
                            HEADHandler(parameters.get("resource"), out);
                            break;
                        case "PUT":
                            int l2 = Integer.parseInt(parameters.get("Content-Length"));
                            PUTHandler(parameters.get("resource"), out, in, l2);
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

    /**
     * The HTTP GET method requests a representation of the specified resource.
     * Requests using GET should only retrieve data.
     * 
     * @param resource The requested resource
     * @param os The output stream object to write a response to
     */
    public void GETHandler(String resource, OutputStream os) throws FileNotFoundException, IOException {

        // Display the requested resource
        System.out.println("GET " + resource);

        // Open the resource
        if (resource.equals("/")) resource = "/index.html";
        Path path = Paths.get("../public" + resource);
        File file = new File(path.toString());

        // Fetch file type
        String type = Files.probeContentType(path);
        if (type == null) type = "text/html";

        BufferedInputStream input = new BufferedInputStream(new FileInputStream(file));
        
        // Header
        os.write("HTTP/1.0 200 OK\n".getBytes());
        os.write(("Content-Type: " + type + "\n").getBytes());
        os.write("Server: Bot\n".getBytes());
        os.write("\n".getBytes());

        // Body
        int size;
        byte[] buffer = new byte[256];
        while((size = input.read(buffer)) != -1) {
            os.write(buffer, 0, size);
        }
        
        input.close();
        os.flush();
    }
   
    /**
    * The HTTP POST method sends data to the server. 
    * The type of the body of the request is indicated by the Content-Type header. 
    */ 
    public void POSTHandler(String resource, PrintWriter out, BufferedReader in,  int length, String content_type) throws FileNotFoundException, IOException {
        System.out.println("POST " + resource);

        
        //POST is used to send data to a server to create/update a resource.
        //The data sent to the server with POST is stored in the request body of the HTTP request:
    
        // Example of Post request from https://www.tutorialspoint.com/http/http_methods.htm

        // In this lab work, we are just displaying post data.
        char c;
        String bodyLine = ""; 
        System.out.println("Content length : " +length);
        for (int i=0; i < length ;i++) {
            c = (char) in.read();
            bodyLine = bodyLine + c;        
        }



        
        Map<String, String> parameters = new HashMap<String, String>();
        String[] parameters_list = bodyLine.split("&");
        
        for(int i=0; i< parameters_list.length; i++) {
            String[] p = parameters_list[i].split("=");
            parameters.put(p[0],p[1]);        
        }
                
        System.out.println("parameters : " + parameters);


        // Sending header 
        out.println("HTTP/1.0 200 OK");
        out.println("Content-Type: text/html");
        out.println("Server: Bot");
        
        //Blank line at the end of header
        out.println("");


        File rFile = new File("../public" + resource);
        boolean exist = rFile.exists();

    }
    
    /**
     * The HTTP HEAD method requests the headers that would be returned if
     * the HEAD request's URL was instead requested with the HTTP GET method.
     * 
     * @param resource The requested resource
     * @param out The print writer object to write a response to
     */
    public void HEADHandler(String resource, PrintWriter out) throws FileNotFoundException, IOException {
        
        // Displaying requested resources
        System.out.println("HEAD " + resource);

    // Check if the file exists
        Path path = Paths.get("../public" + resource);
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

    public void PUTHandler(String filename,  PrintWriter  out, BufferedReader in, int length) throws FileNotFoundException, IOException {
        System.out.println("Handling a PUT Method");
    
        char c; 
        String bodyLine = ""; 
        System.out.println("Content length : " +length);
        for (int i=0; i < length ;i++) {
            c = (char) in.read();
            bodyLine = bodyLine + c;        
        }

        String[]  params = bodyLine.split("&");
        
        System.out.println("Parameters : " +bodyLine);

        
        Map<String, String> parameters = new HashMap<String, String>();
        String[] parameters_list = bodyLine.split("&");
        
        for(int i=0; i< parameters_list.length; i++) {
            String[] p = parameters_list[i].split("=");
            parameters.put(p[0],p[1]);        
        }
                
        System.out.println("parameters : " + parameters);

        // Write in file // for put.
        BufferedWriter outf = null; 
        FileWriter fstream = new FileWriter("out.txt", true); //true tells to append data.
        outf = new BufferedWriter(fstream);
        outf.write(bodyLine);
     
        // Sending header 
        out.println("HTTP/1.0 200 OK");
        out.println("Content-Type: text/html");
        out.println("Server: Bot");
        
        //Blank line at the end of header
        out.println("");
    }

    /**
     * The HTTP DELETE request method deletes the specified resource.
     * 
     * @param resource The requested resource
     * @param out The print writer object to write a response to
     */
    public void DELETEHandler(String resource, PrintWriter out) throws FileNotFoundException, IOException {

        // Display requested resources
        System.out.println("DELETE " + resource);

        // Check if the file exists and delete it
        File file = new File("../public" + resource);
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
    * @param args Command line parameters are not used
    */
    public static void main(String args[]) {
        WebServer ws = new WebServer();
        ws.start();
    }
}
