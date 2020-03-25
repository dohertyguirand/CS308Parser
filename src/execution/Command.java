package execution;

import java.util.List;

import model.*;

/**
 * this is the interface for all of the Commands
 * I think that this is well designed because it will be applicable to all of the commands. This is because the ModelContainer is passed into the execute method. The ModelContainer holds all
 * of the models so any Command is able to maniuplate the data it needs to. Furthermore, creating a new model or command does not affect the any of the old commands.
 */

public interface Command {
    /**
     * execute alters current model, performs action based on each specific command
     * @param parameters - is a list of strings that holds all of the required parameters to execute
     * @param TurtleModel - holds the current turtle model on which the command is being run
     * @param allModels - is a wrapper object that contains all of the user defined methods and variables
     * @return - a double that is the appropriate value based on each specific command
     */
    double execute(List<String> parameters, TurtleModel TurtleModel, ModelContainer allModels);
    int getNumParams();
}
