package cmd;

/**
 *
 * @author loveboat
 */
public class gpb3Command extends UserCommandHandler {
    private userinterface.Userinterface myUI;
    private client.Client myClient;

    public gpb3Command (userinterface.Userinterface ui, client.Client client) {
        super(ui, client);
        myUI = ui;
        myClient = client;
    }

    @Override
    public void execute(String cmd) {
    }
}
