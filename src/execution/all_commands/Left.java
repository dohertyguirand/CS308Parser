package execution.all_commands;

import execution.Command;
import java.util.List;
import model.TurtleModel;
import model.VariableModel;

public class Left implements Command {
    @Override
    public double execute(List<String> parameters, TurtleModel turtleModel, VariableModel variableModel) {
        System.out.println("Rotating Left by: "+ parameters.get(0));
        turtleModel.setAngle(turtleModel.getAngle() - Double.parseDouble(parameters.get(0)));
        return Double.parseDouble(parameters.get(0)) ;
    }

    @Override
    public int getNumParams() {
        return 1;
    }
}