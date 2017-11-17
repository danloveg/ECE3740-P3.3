package cmd;

import java.lang.reflect.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

/**
 *
 * @author loveboat
 */
public class UserCommandHandler implements commandinterface.Command {
    private UserCommandHandler commandExecutor;
    private final userinterface.Userinterface UI;
    private final client.Client myClient;
    private final ExecutorService executor;

    public UserCommandHandler(userinterface.Userinterface ui, client.Client client) {
        this.UI = ui;
        this.myClient = client;
        executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void execute(final String command) {
        executor.submit(() -> {
            String secureCommand = "cmd." + command + "Command";
            try {
                // Create a parameter array for the constructor of the object
                Class[] parameters = new Class[2];
                parameters[0] = userinterface.Userinterface.class;
                parameters[1] = client.Client.class;

                // Get the Class corresponding to the command
                Class newObject = Class.forName(secureCommand);

                // Get the class's constructor
                Constructor<UserCommandHandler> objCtor = newObject.getDeclaredConstructor(parameters);

                // Create a new instance of the Class
                commandExecutor = objCtor.newInstance(this.UI, this.myClient);

                // Execute the command
                commandExecutor.execute(secureCommand);

            } catch (ClassNotFoundException |
                     NoSuchMethodException |
                     InstantiationException |
                     IllegalAccessException |
                     InvocationTargetException ex) {
                UI.update(ex.toString());
            }
        });
    }
}
