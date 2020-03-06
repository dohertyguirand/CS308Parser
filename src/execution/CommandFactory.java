package execution;

import java.lang.reflect.InvocationTargetException;

public class CommandFactory {
    private static final String PACKAGE = "execution.all_commands.";
    public Command getCommand(String command) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class c = Class.forName(PACKAGE + command);

        Command command1 = (Command) c.getConstructor().newInstance();

        return command1;
    }


}
