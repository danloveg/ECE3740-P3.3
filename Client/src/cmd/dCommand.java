package cmd;

/**
 *
 * @author loveboat
 */
public class dCommand extends UserCommandHandler {
    private userinterface.Userinterface myUI;
    private client.Client myClient;

    public dCommand (userinterface.Userinterface ui, client.Client client) {
        super(ui, client);
        myUI = ui;
        myClient = client;
    }

    @Override
    public void execute(String cmd) {
    }
}
