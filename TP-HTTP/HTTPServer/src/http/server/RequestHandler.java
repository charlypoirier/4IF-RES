
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
public class RequestHandler extends Thread {

    private Socket remote;

    private BufferedReader input;
    private OutputStream outputStream;
    private PrintWriter output;

    /**
     * RequestHandler Constructor
     * 
     * @param remote BufferedReader
     */
    RequestHandler(Socket remote) throws FileNotFoundException, IOException {
        this.remote = remote;
        this.input = new BufferedReader(new InputStreamReader(remote.getInputStream()));
        this.outputStream = remote.getOutputStream();
        this.output = new PrintWriter(outputStream);
    }

    /**
     * Build a header and returns it
     * 
     * @param code status code
     * @param type content-type
     */
    private String getHeader(int code, String type ) {

        String message = new String();
        // Finding response code
        switch(code) {
            case 200:
                message = "OK";             break;
            case 204:
                message = "No Content";     break;
            case 400:
                message = "Bad Request";    break;
            case 404:
                message = "Not Found";      break; 
            case 500:  
                message = "Internal Server Error";
                break;
        }

        String header = "HTTP/1.0 "+ code + " " + message + "\n";
        header = header +  "Content-Type: " + type + "\n";
        header = header + "Server: Bot\n";
        header = header + "\n";
    
        return header;
    
    } 
        
    /**
     * Parse header parameters and return
     * a Map of keys and values.
     * 
     * @throws FileNotFoundException Thrown when a resource is not found
     * @throws IOException Thrown on input/output errors
     * 
     * @return a map of header parameters
     */
    protected Map<String, String> parseParameters() throws FileNotFoundException, IOException {
        Map<String, String> parameters = new HashMap<String, String>();
        String str = input.readLine();
        if (str != null && !str.equals("")) {
            // First line
            String[] args = str.split("\\s");
            if (args.length == 3) {
                parameters.put("method", args[0]);
                parameters.put("resource", args[1]);
                parameters.put("version", args[2]);
            }
            // Header parameters
            str = input.readLine();
            while (str != null && !str.equals("")) {
                args = str.split(": ");
                if (args.length > 1) {
                    parameters.put(args[0], args[1]);
                }
                str = input.readLine();
            }
        }
        return parameters;
    }

    /**
     * The HTTP GET method requests a representation of the specified resource.
     * Requests using GET should only retrieve data.
     * 
     * @param resource The requested resource
     * @param os The output stream object to write a response to
     * 
     * @throws FileNotFoundException Thrown when the resource is not found
     * @throws IOException Thrown on input/output errors
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
        String h = getHeader(200, type);
        os.write(h.getBytes());

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
     * 
     * Example of a POST request from https://www.tutorialspoint.com/http/http_methods.htm
     * In this lab work, we are just displaying post data.
     * 
     * @param resource The resource to create
     * @param os The output stream object to write a response to
     * @param out The print writer object to write a response to
     * @param in The body of the request
     * @param length The length of the body
     * @param content_type Type of the content
     * 
     * @throws FileNotFoundException Thrown when the resource is not found
     * @throws IOException Thrown on input/output errors
     */ 
    public void POSTHandler(String resource, OutputStream os, PrintWriter out, BufferedReader in,  int length, String content_type) throws FileNotFoundException, IOException {
       
        // Display the requested resource
        System.out.println("POST " + resource);

        char c;
        String bodyLine = ""; 
        System.out.println("Content length : " +length);
        for (int i=0; i < length ;i++) {
            c = (char) in.read();
            bodyLine = bodyLine + c;        
        }
       
        if(content_type.equals("application/x-www-form-urlencoded")) { 
            
            Map<String, String> parameters = new HashMap<String, String>();
            String[] parameters_list = bodyLine.split("&");
        
            for(int i=0; i< parameters_list.length; i++) {
                String[] p = parameters_list[i].split("=");
                parameters.put(p[0],p[1]);        
            }    
            System.out.println("parameters : " + parameters);
        }
        else {
            System.out.println(bodyLine); 
        }
       
        // Open the resource
        if (resource.equals("/")) resource = "/index.html";
        Path path = Paths.get("../public" + resource);
        File file = new File(path.toString());
         
        String type = Files.probeContentType(path);
        if (type == null) type = "text/html";
        
        os.write(getHeader(200, type).getBytes());
        
        // Body
        // Fetch file type
        BufferedInputStream input = new BufferedInputStream(new FileInputStream(file));
        int size;
        byte[] buffer = new byte[256];
        while((size = input.read(buffer)) != -1) {
            os.write(buffer, 0, size);
        } 
        input.close();
        os.flush();
    }
    
    /**
     * The HTTP HEAD method requests the headers that would be returned if
     * the HEAD request's URL was instead requested with the HTTP GET method.
     * 
     * @param resource The requested resource
     * @param out The print writer object to write a response to
     * 
     * @throws FileNotFoundException Thrown when the resource is not found
     * @throws IOException Thrown on input/output errors
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

    /**
     * The PUT method requests that the enclosed entity be stored
     * under the supplied Request-URI.
     * 
     * If the Request-URI refers to an already existing resource,
     * the enclosed entity SHOULD be considered as a modified
     * version of the one residing on the origin server.
     * 
     * @param resource The resource to update
     * @param out The print writer object to write a response to
     * @param in The body of the request
     * @param length The length of the body
     * @param content_type The type of content
     * 
     * @throws FileNotFoundException Thrown when the resource is not found
     * @throws IOException Thrown on input/output errors
     */
    public void PUTHandler(String resource, PrintWriter out, BufferedReader in, int length, String content_type) throws FileNotFoundException, IOException {
        
        // Displaying requested resources
        System.out.println("PUT " + resource);
        if(resource.equals("/")) {  resource = "/out.txt";} 
         
        char c; 
        String bodyLine = ""; 
        for (int i=0; i < length ;i++) {
            c = (char) in.read();
            bodyLine = bodyLine + c;        
        } 
          
        if(content_type.equals("application/x-www-form-urlencoded")) { 
            
            Map<String, String> parameters = new HashMap<String, String>();
            String[] parameters_list = bodyLine.split("&");
        
            for(int i=0; i< parameters_list.length; i++) {
                String[] p = parameters_list[i].split("=");
                parameters.put(p[0],p[1]);        
            }    
            System.out.println("parameters : " + parameters);
        }
        else {
            System.out.println(bodyLine);
        
        }       

        // Write in file;
        byte[] buffer = bodyLine.getBytes();
    
        // Creating the file 
        File f = new File("../public" +resource); 
        BufferedOutputStream fOut = new BufferedOutputStream(new FileOutputStream(f)); 
        
        // Writing in it
        fOut.write(buffer, 0, buffer.length);
        fOut.flush();
     
        // Sending header 
        out.println(getHeader(200, "text/html"));
    }

    /**
     * The HTTP DELETE request method deletes the specified resource.
     * 
     * @param resource The requested resource
     * @param out The print writer object to write a response to
     * 
     * @throws FileNotFoundException Thrown when the resource is not found
     * @throws IOException Thrown on input/output errors
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
     * Run the thread.
     */
    public void run() {
        try {
            Map<String, String> parameters = parseParameters();
            String method = parameters.get("method");
            if (method != null) {
                switch (parameters.get("method")) {
                    case "GET":
                        GETHandler(parameters.get("resource"), outputStream);
                        break;
                    case "POST":
                        int l = Integer.parseInt(parameters.get("Content-Length"));
                        POSTHandler(parameters.get("resource"), outputStream, output, input,  l, parameters.get("Content-Type"));
                        break;
                    case "HEAD":
                        HEADHandler(parameters.get("resource"), output);
                        break;
                    case "PUT":
                        int l2 = Integer.parseInt(parameters.get("Content-Length"));
                        PUTHandler(parameters.get("resource"), output, input, l2, parameters.get("Content-Type"));
                        break;
                    case "DELETE":
                        DELETEHandler(parameters.get("resource"), output);
                        break;
                    default:
                        output.println(getHeader(400, "text/html"));
                        output.println("<p>Bad request (400)</p>");
                    }
            }
        } catch (FileNotFoundException e) {
            output.println(getHeader(404, "text/html"));
            output.println("<p>Not found (404)</p>");
        } catch (Exception e) {
            e.printStackTrace();
            output.println(getHeader(500, "text/html"));
            output.println("<p>Internal Server Error (500)</p>");
        }

        try {
            output.flush();
            remote.close();
        } catch (IOException e) {
            System.out.println("Error: Could not close remote.");
        }
    }
}
