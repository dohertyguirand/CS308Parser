package execution.all_commands;

import execution.Command;
import java.util.List;
import model.TurtleModel;
import model.VariableModel;

public class Forward implements Command {
    @Override
    public double execute(List<String> parameters, TurtleModel turtleModel, VariableModel variableModel) {
        System.out.println("Moving forward by: "+ parameters.get(0));
        double radians = turtleModel.getAngle() * Math.PI/180;
        double xChange = Double.parseDouble(parameters.get(0)) * Math.sin(radians);
        double yChange = Double.parseDouble(parameters.get(0)) * Math.cos(radians);
        turtleModel.setX(turtleModel.getX() + xChange);
        turtleModel.setY(turtleModel.getY() - yChange);
        return  Double.parseDouble(parameters.get(0));
    }

    @Override
    public int getNumParams() {
        return 1;
    }
}