import java.net.*;
import java.io.*;
import java.util.*;

public class ReadThread implements Runnable
{
    private MulticastSocket s;
    private InetAddress group;
    private int port;
    private static final int MAX_LEN = 1000;
    
    // Constructeur
    ReadThread(MulticastSocket socket,InetAddress group,int port)
    {
        this.s = socket;
        this.group = group;
        this.port = port;
    }
     
    @Override
    public void run()
    {
        while(!GroupChat.finished)
        {
                byte[] buffer = new byte[ReadThread.MAX_LEN];
                DatagramPacket datagram = new
                DatagramPacket(buffer,buffer.length,group,port);
                String message;
            try
            {
                s.receive(datagram);
                message = new
                
                //String(byte[] bytes,  int offset, int length, Charset charset)
                String(buffer,0,datagram.getLength(),"UTF-8");
                
                System.out.println(message);
            
            }
            catch(IOException e)
            {
                System.out.println("Socket closed!");
            }
        }
    }
}
