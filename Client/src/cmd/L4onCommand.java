package cmd;

/**
 *
 * @author loveboat
 */
public class L4onCommand extends UserCommandHandler {
    private userinterface.Userinterface myUI;
    private client.Client myClient;

    public L4onCommand (userinterface.Userinterface ui, client.Client client) {
        super(ui, client);
        myUI = ui;
        myClient = client;
    }

    @Override
    public void execute(String cmd) {
    }
}
