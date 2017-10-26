package userinterface;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 * Interacts with the client through the console. Reads a client's commands by
 * using a non-blocking concurrent thread. Executing the user's commands is
 * delegated to an associated command handler.
 * 
 * @author Daniel Lovegrove
 */
public class StandardIO implements Runnable, Userinterface {
    
    private final BufferedReader console;
    private clientmessagehandler.ClientMessageHandler commandHandler;
    
    
    /**
     * Creates an instance without a command handler.
     */
    public StandardIO() {
        this.console = new BufferedReader(new InputStreamReader(System.in));
        
        if (console == null) {
            System.err.println("No Standard Input device, exiting program.");
            System.exit(1);
        }
    }
    
    
    /**
     * Creates an instance with an associated command handler.
     * @param handler The handler to be associated with this instance.
     */
    public StandardIO(clientmessagehandler.ClientMessageHandler handler) {
        this.commandHandler = handler;
        this.console = new BufferedReader(new InputStreamReader(System.in));
        
        if (console == null) {
            System.err.println("No Standard Input device, exiting program.");
            System.exit(1);
        }
    }
    
    
    /**
     * Set the command handler for this instance.
     * @param handler 
     */
    public void setCommandHandler(clientmessagehandler.ClientMessageHandler handler) {
        this.commandHandler = handler;
    }
    
    
    /**
     * Displays a string on the console. Automatically creates a new line after
     * the message.
     * @param message The message to be displayed on the console. 
     */
    @Override
    public void update(String message) {
        System.out.println(message);
    }
    
    
    /**
     * Continuously reads the user's input through the console and passes the
     * strings to a command handler every time the user presses enter.
     */
    @Override
    public void run() {
        while (true) {    
            try {
                // Try to execute every command the user enters on the console
                commandHandler.execute(console.readLine());
            } catch (IOException e) {
                System.err.println(e);
                System.exit(1);
            }
        }
    }
}
