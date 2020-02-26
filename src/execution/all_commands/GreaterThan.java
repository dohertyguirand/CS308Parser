package execution.all_commands;

import execution.Command;
import java.util.List;
import model.TurtleModel;
import model.VariableModel;

public class GreaterThan implements Command {

    @Override
    public double execute(List<String> parameters, TurtleModel turtleModel, VariableModel variableModel) {
        if(Double.parseDouble(parameters.get(1)) > Double.parseDouble(parameters.get(0))) return 1;
        return 0;
    }

    @Override
    public int getNumParams() {
        return 2;
    }
}