package cmd;

/**
 *
 * @author loveboat
 */
public class UserCommandHandler implements commandinterface.Command {
    private UserCommandHandler commandExecutor;
    private final userinterface.Userinterface UI;
    private final client.Client myClient;

    public UserCommandHandler(userinterface.Userinterface ui, client.Client client) {
        this.UI = ui;
        this.myClient = client;
    }

    @Override
    public void execute(String command) {
        UI.update("Command: " + command + " received.");
    }
}
