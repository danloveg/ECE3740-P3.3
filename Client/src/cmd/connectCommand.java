package cmd;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author loveboat
 */
public class connectCommand extends UserCommandHandler {
    private userinterface.Userinterface myUI;
    private client.Client myClient;

    public connectCommand (userinterface.Userinterface ui, client.Client client) {
        super(ui, client);
        myUI = ui;
        myClient = client;
    }

    @Override
    public void execute(String cmd) {
        myUI.update("Secure command received: " + cmd);

        // Extract the actual command from the secure command
        Pattern pattern = Pattern.compile("^cmd\\.(.*)Command$");
        Matcher matcher = pattern.matcher(cmd);

        if (matcher.matches()) {
            myUI.update("Actual command: " + matcher.group(1));
        }
    }
}
