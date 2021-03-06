package clienttest;

/**
 *
 * @author Daniel Lovegrove
 */
public class ClientTest {
    
    public static void main (String[] args) {
        // Instantiate a user interface
        final userinterfacegui.ClientUserInterface userInterface = 
                new userinterfacegui.ClientUserInterface();
        
        // Instantiate a new Client
        client.Client myClient = new client.Client(7777, userInterface);
        
        // Instantiate a command handler for the user
        commandinterface.Command commandHandler =
                new cmd.UserCommandInvoker(userInterface, myClient);
        
        // Set the user interface's command handler
        userInterface.setCommandHandler(commandHandler);
        
        // Start the user interface thread
        java.awt.EventQueue.invokeLater(() -> {
            userInterface.setVisible(true);
        });

        // Display the initial menu
        userInterface.update("Connect to server to begin.");
    }
}
