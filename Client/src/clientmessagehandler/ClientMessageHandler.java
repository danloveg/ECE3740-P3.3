package clientmessagehandler;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

/**
 * Handles the executing of the user's commands.
 * 
 * @version November 8/2017
 * @author Daniel Lovegrove
 */
public class ClientMessageHandler {
    
    private final userinterface.Userinterface UI;
    private final client.Client myClient;
    private final ExecutorService executor;
    
    
    /**
     * Creates an instance with an associated user interface and client.
     * @param ui The UI user interface
     * @param client The client
     */
    public ClientMessageHandler(userinterface.Userinterface ui, client.Client client) {
        this.UI = ui;
        this.myClient = client;
        // Set up the executor
        executor = Executors.newSingleThreadExecutor();
    }


    /**
     * Parses the user's command and executes the appropriate task.
     * @param userCommand The string the user entered
     */
    public void execute(final String userCommand) {
        // Handle the user's command in a concurrent thread
        executor.submit(() -> {
            // First, trim any whitespace
            String cmd = userCommand.trim();

            switch (cmd) {
                case "connect":
                    connectClientToServer();
                    break;
                case "d":
                case "q":
                    disconnectClientFromServer(cmd);
                    break;
                case "t":
                case "L1on":
                case "L2on":
                case "L3on":
                case "L4on":
                case "L1off":
                case "L2off":
                case "L3off":
                case "L4off":
                case "gpb1":
                case "gpb2":
                case "gpb3":
                    sendStringToServer(cmd);
                    break;
                case "":
                    break;
                default:
                    invalidCommand(cmd);
                    break;
            }
        });
    }


    /**
     * Try to connect the client to the server if they aren't already connected.
     */
    private void connectClientToServer() {
        if (false == myClient.getConnected()) {
            try {
                // Try to connect to server
                myClient.connectToServerIP("192.168.1.214");
                // Then create the Client Thread and start it
                Thread clientThread = new Thread(myClient);
                clientThread.start();
            } catch (IOException ex) {
                UI.update("Could not create a connection to server.");
            }

        } else {
            // We are already connected
            UI.update("Already connected!");
        }
    }


    /**
     * Try to disconnect the client from the server if they are connected to a
     * server.
     * @param disconnectString The disconnect code that will be sent to the
     * server
     */
    private void disconnectClientFromServer(String disconnectString) {
        if (true == myClient.getConnected()) {
            try {
                // Send disconnect message q or d to server
                sendStringToServer(disconnectString);
                // Then disconnect the Client
                myClient.disconnectFromServer();
            } catch (IOException ex) {
                UI.update("Disconnection error: " + ex.toString());
            }

        } else if (false == disconnectString.equals("q")) {
            UI.update("No connected server.");
        }
        // If we want to quit, close the program as well
        if (disconnectString.equals("q")) {
            System.exit(0);
        }
    }


    /**
     * Use the client to send a String to a connected server.
     * @param message The message to send
     */
    private void sendStringToServer(String message) {
        if (true == myClient.getConnected()) {
            try {
                myClient.sendMessageToServer(message);
            } catch (IOException e) {
                UI.update("Could not send message to server.");
                disconnectClientFromServer("d");
            }
        } else {
            UI.update("No connected server.");
        }
    }


    /**
     * Notify the user that their command is not valid.
     * @param command The command that was invalid
     */
    private void invalidCommand(String command) {
        UI.update("\"" + command + "\" is not recognized.");
    }
}
