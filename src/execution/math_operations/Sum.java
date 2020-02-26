package execution.math_operations;

import execution.Command;
import model.TurtleModel;
import model.VariableModel;

import java.util.List;

public class Sum implements Command {

    @Override
    public double execute(List<Double> parameters, TurtleModel turtleModel, VariableModel variableModel) {
        double sum = parameters.get(0) + parameters.get(1);
        return sum;
    }

    @Override
    public int getNumParams() {
        return 2;
    }
}