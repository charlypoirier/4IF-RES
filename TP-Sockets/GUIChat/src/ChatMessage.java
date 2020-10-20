import java.io.*;

/** 
 * This class defines the different type of messages that will be exchanged between the 
 * Clients and the Server. 
 */
public class ChatMessage implements Serializable {
    
    // The different types of message sent by the Client
    // WHOISIN to receive the list of the users connected
    // MESSAGE an ordinary message
    // LOGOUT to disconnect from the Server
   
    /**
    *   The type attribute of the message
    */
    private int type;
    /**
     * The different type of messages sent by the client.
     */ 
    public static final int WHOISIN = 0,MESSAGE = 1,LOGOUT  = 2,HISTORY = 3;
    private String message;
    
    /**
    *   Constructor
    *   @param type the message type
    *   @param message the content of the message
    */ 
    ChatMessage(int type, String message)    {
        this.type = type;
        this.message = message;    
    }
    
    // getters
    int getType() {
        return type;    
    }

    String getMessage() {
        return message;    
    }
}
