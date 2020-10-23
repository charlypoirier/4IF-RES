/***
 * ChatServer.java
 * TCP server for a socket-based chat system
 * Date: 13/10/2020
 * Authors: Jérôme Hue, Charly Poirier
 */

import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;

/**
 * Server main file for the
 * GUI Chat application
 */
public class ChatServer  {

    static ClientThread[] listCT = new ClientThread[100];
    static int nbCT = 0;
    
    // a unique ID for each connection
    private static int uniqueId;
    // an ArrayList to keep the list of the Client
    private ArrayList<ClientThread> al;
    // an ArrayList to keep the list of messages
    private ArrayList<String> ml;
    // if I am in a GUI
    private ServerGUI sg;
    // to display time
    private SimpleDateFormat sdf;
    // the port number to listen for connection
    private int port;
    // the boolean that will be turned of to stop the server
    private boolean keepGoing;

    /**
     * ChatServer Constructor with no GUI
     * 
     * @param port an integer representing the port
     */
    public ChatServer(int port) {
        this(port, null);
    }

    /**
     * ChatServer Constructor with GUI
     * 
     * @param port an integer representing the port
     * @param sg a ServerGUI object for the chat interface
     */
    public ChatServer(int port, ServerGUI sg) {
        // GUI or not
        this.sg = sg;
        // the port
        this.port = port;
        // to display hh:mm:ss
        sdf = new SimpleDateFormat("HH:mm:ss");
        // ArrayList for the Client list
        al  = new ArrayList<ClientThread>();
        // ArrayList for the messages list
        ml = new ArrayList<String>();
        // Keep going
        this.keepGoing = true;
    }

    /**
     * Starts the server
     */
    public void start() {
        try {
            // Create a server socket associated with the server port
            ServerSocket serverSocket = new ServerSocket(port);
            
            // Display message saying that we are waiting
            display("Server ready on port : "+ port + "." +keepGoing);
            
            while(keepGoing) {
                // Server waits for a connection
                Socket socket  = serverSocket.accept();

                if (!keepGoing) {
                    break; 
                }

                // Spawn a thread for new client
                ClientThread t = new ClientThread(socket);
                al.add(t); 
                t.start();
            }
        } catch (Exception e) {
            display("Error while running server");
        }
    }

    /**
     * For the GUI to stop the server
     */
    protected void stop() {
        keepGoing = false;
        // connect to myself as Client to exit statement
        // Socket socket = serverSocket.accept();
        try {
            new Socket("localhost", port);
        }
        catch(Exception e) {
            // nothing I can really do
        }
    }

    /**
     * Displays a message on the GUI
     * or terminal
     */
    private void display(String msg) {
        String time = sdf.format(new Date()) + " " + msg;
        if(sg == null)
            System.out.println(time);
        else 
            sg.appendEvent(time + "\n");
    }

    /**
     * Broadcasts a message to all Clients
     */
    private synchronized void broadcast(String message) {
        // add HH:mm:ss and \n to the message
        String time = sdf.format(new Date());
        String messageLf = time + " " + message + "\n";
        // display message on console or GUI
        if(sg == null)
            System.out.print(messageLf);
        else
            sg.appendRoom(messageLf); // append in the room window

        // we loop in reverse order in case we would have to remove a Client
        // because it has disconnected
        for(int i = al.size(); --i >= 0;) {
            ClientThread ct = al.get(i);
            // try to write to the Client if it fails remove it from the list
            if(!ct.writeMsg(messageLf)) {
                al.remove(i);
                display("Disconnected Client " + ct.username + " removed from list.");
            }
        }
    }

    /**
     * Removes a logged off Client from the list
     * 
     * @param id Client ID to be removed
     */
    synchronized void remove(int id) {
        // scan the array list until we found the Id
        for(int i = 0; i < al.size(); ++i) {
            ClientThread ct = al.get(i);
            // found it
            if(ct.id == id) {
                al.remove(i);
                return;
            }
        }
    }    

 	/**
     * Main method
     * 
	 * @param args command line arguments (port)
  	**/
    public static void main(String args[]){ 
        ServerSocket listenSocket;
        // Start server on port 1500 unless a PortNumber is specified
        int portNumber = 1500;
            switch(args.length) {
             case 1:
                try {
                    portNumber = Integer.parseInt(args[0]);
                }
                catch(Exception e) {
                    System.out.println("Invalid port number.");
                    System.out.println("Usage is: > java Server [portNumber]");
                    return;
                }
             case 0:
                break;
             default:
                System.out.println("Usage is: > java Server [portNumber]");
                return;
            }

        // Create a server object and start it
        ChatServer server = new ChatServer(portNumber);
        server.start();
    }

    /**
     * One instance of this thread will run for each client
     */
    class ClientThread extends Thread {
        // the socket where to listen/talk
        Socket socket;
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;
        
        // my unique id (easier for deconnection)
        int id;

        // the Username of the Client
        String username;
        // the only type of message a will receive
        ChatMessage cm;
        // the date I connect
        String date;

        /**
         * ClientThread Constructor
         * 
         * @param socket the communication socket
         */
        ClientThread(Socket socket) {
            
            // a unique id
            id = ++uniqueId;
            this.socket = socket;
            
            /**
             * Creating both Data Stream
             */
            System.out.println("Thread trying to create Object Input/Output Streams");
            try
            {
                // create output first
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput  = new ObjectInputStream(socket.getInputStream());
                // read the username
                username = (String) sInput.readObject();
                display(username + " just connected.");
            }
            catch (IOException e) {
                display("Exception creating new Input/output Streams: " + e);
                return;
            }
            // have to catch ClassNotFoundException
            // but I read a String, I am sure it will work
            catch (ClassNotFoundException e) {
            }
            date = new Date().toString() + "\n";
        }
          
        /**
         * Starts the thread and runs
         * until the client disconnects
         */
        public void run() {
            // to loop until LOGOUT
            boolean keepGoing = true;
            while(keepGoing) {
                // read a String (which is an object)
                try {
                    cm = (ChatMessage) sInput.readObject();
                }
                catch (IOException e) {
                    display(username + " Exception reading Streams: " + e);
                    break;
                }
                catch(ClassNotFoundException e2) {
                    break;
                }
                // the messaage part of the ChatMessage
                String message = cm.getMessage();

                // Switch on the type of message receive
                switch(cm.getType()) {

                case ChatMessage.MESSAGE:
                    ml.add(message);
                    try {
                        FileWriter fw = new FileWriter("messages.log", true);
                        fw.write(message +" \n");
                        fw.close();
                    } catch (Exception e) {
                        display("Error while writing messages to file \n");
                    }
                    broadcast(username + ": " + message);
                    break;
                case ChatMessage.LOGOUT:
                    display(username + " disconnected with a LOGOUT message.");
                    keepGoing = false;
                    break;
                case ChatMessage.WHOISIN:
                    writeMsg("List of the users connected at " + sdf.format(new Date()) + "\n");
                    // scan al the users connected
                    for(int i = 0; i < al.size(); ++i) {
                        ClientThread ct = al.get(i);
                        writeMsg((i+1) + ") " + ct.username + " since " + ct.date);
                    }
                    break;
                case ChatMessage.HISTORY:
                    display(username + " request chat history with a HISTORY message.");
                    writeMsg("Chat history since server startup : \n");
                    for(int i=0; i<ml.size(); ++i) {
                        writeMsg(ml.get(i) + "\n");
                    }
                    break;
                }
            }

            // remove myself from the arrayList containing the list of the
            // connected Clients
            remove(id);
            close();
        }

        /**
         * Close remote connection
         */
        private void close() {
            try {
                if(sOutput != null) sOutput.close();
                if(sInput != null) sInput.close();
                if(socket != null) socket.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        /**
         * Writes a String to the Client output stream
         * 
         * @param msg a string object to be written
         */
        private boolean writeMsg(String msg) {
            // if Client is still connected send the message to it
            if(!socket.isConnected()) {
                close();
                return false;
            }
            // Write the message to the stream
            try {
                sOutput.writeObject(msg);
            }
            // if an error occurs, do not abort just inform the user
            catch(IOException e) {
                display("Error sending message to " + username);
                display(e.toString());
            }
            return true;
        }
    }
}
