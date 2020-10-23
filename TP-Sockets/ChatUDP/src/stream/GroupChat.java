import java.net.*;
import java.io.*;
import java.util.*;

/**
 * GroupChat class
 * 
 * A multicast chat application
 */
public class GroupChat
{
    private static final String TERMINATE = "Exit";
    public static String name;
    public static volatile boolean finished = false;
    
    public static void main(String[] args)
    {
        if (args.length != 2)
            System.out.println(
                "Usage : java GroupChat  <multicast-host> <port-number>"
            );
        else
        {
            try
            {
                // Récupération de l'hôte et du port
                InetAddress group = InetAddress.getByName(args[0]);
                int port = Integer.parseInt(args[1]);
                
                // Récupération du nom (décoratif)
                Scanner sc = new Scanner(System.in);
                System.out.print("Enter your name: ");
                name = sc.nextLine();
                
                // Create a multicast socket
                MulticastSocket s = new MulticastSocket(port);
             
                // Join the group
                s.joinGroup(group);

                Thread t = new Thread(new ReadThread(s,group,port));
             
                // Spawn a thread for reading messages
                t.start(); 
                
                System.out.println("Connected ! Start typing messages...\n");
                while(true)
                {
                    // Read messsage 
                    String message;
                    message = sc.nextLine();


                    if(message.equals(TERMINATE))
                    {
                        finished = true;
                        s.leaveGroup(group);
                        s.close();
                        break;
                    }
                    
                    // Add user to message
                    message = name + ": " + message;

                    // Build a datagram packet for a message to send to the group
                    byte[] buffer = message.getBytes();
                    DatagramPacket datagram = 
                        new DatagramPacket(buffer,buffer.length,group,port);
                    
                    // Send a multicast message to the group
                    s.send(datagram);
                }
            }
            catch(SocketException se)
            {
                System.out.println("Error creating socket");
                se.printStackTrace();
            }
            catch(IOException ie)
            {
                System.out.println("Error reading/writing from/to socket");
                ie.printStackTrace();
            }
        }
    }
}
