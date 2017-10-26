package clientmessagehandler;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.IOException;

/**
 * Handles the executing of the user's commands.
 * 
 * @author Daniel Lovegrove
 */
public class ClientMessageHandler {
    
    private final userinterface.Userinterface UI;
    private final client.Client myClient;
    
    
    /**
     * Creates an instance with an associated user interface and client.
     * @param ui The UI user interface
     * @param client The client
     */
    public ClientMessageHandler(userinterface.Userinterface ui, client.Client client) {
        this.UI = ui;
        this.myClient = client;
    }
    
    
    /**
     * Parses the user's command and executes the appropriate task.
     * @param userCommand The string the user entered
     */
    public void execute(String userCommand) {
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
    }


    /**
     * Try to connect the client to the server if they aren't already connected.
     */
    private void connectClientToServer() {
        if (false == myClient.isConnected()) {
            // If we are not connected, try to connect
            try {
                // Connect
                myClient.connectToServer(InetAddress.getLocalHost());
                // Create Client Thread and start it
                Thread clientThread = new Thread(myClient);
                clientThread.start();
            } catch (UnknownHostException e) {
                UI.update("Could not determine host.");
            } catch (IOException e) {
                UI.update("Could not connect to server.");
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
        if (true == myClient.isConnected()) {
            // If we are connected, try to quit
            try {
                // Send disconnect message q or d to server
                sendStringToServer(disconnectString);
                // Then disconnect
                myClient.disconnectFromServer();
            } catch (IOException e) {
                UI.update("Disconnection error: " + e.toString());
            }
        } else {
            // We are not connected to a server\
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
        if (true == myClient.isConnected()) {
            try {
                myClient.sendMessageToServer(message);
            } catch (IOException e) {
                UI.update("Could not send message to server.");
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
