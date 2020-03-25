package execution;

import java.util.List;

import model.*;

/**
 * this is the interface for all of the Commands
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
