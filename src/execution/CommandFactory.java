package execution;

import java.lang.reflect.InvocationTargetException;

/**
 * this class creates the approriate command given the command name
 */

public class CommandFactory {
    private static final String PACKAGE = "execution.all_commands.";


    /**
     *
     * @param command - the command key that matches the name of a command class EXACTLY
     * @return - returns the appropriate command based on the given command name
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    public Command getCommand(String command) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class c = Class.forName(PACKAGE + command);
        Command command1 = (Command) c.getConstructor().newInstance();
        return command1;
    }

}
