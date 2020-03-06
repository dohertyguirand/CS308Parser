package execution.all_commands;

import execution.Command;
import execution.LoopCommand;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import execution.MultipleTurtlesCommand;
import model.*;

public class MakeUserInstruction extends LoopCommand implements MultipleTurtlesCommand {

  @Override
  public double execute(List<String> parameters, VariableModel variableModel, ConsoleModel consoleModel, Map<String, MethodModel> methodModels, TurtleModelContainer turtleModelContainer, TurtleModel currentTurtleModel) {
      List <String> symbolList = Arrays.asList(parameters.get(0).split("[ ]+"));

      // to varname [ :var1 :var2 ] [ command1 command2 ]
      String language = symbolList.get(0);

      String methodName = symbolList.get(1);

      String [] loopBodies = getLoopBodies(symbolList);

      MethodModel myMethodModel = new MethodModel(loopBodies[0], loopBodies[1]);

      methodModels.put(methodName, myMethodModel);

      System.out.println("DONE MAKING METHOD BODY FOR METHOD NAME: " + methodName);
//    System.out.println(language);
//    System.out.println(numRepeats);
//    System.out.println(loopBody);
      return 1; // there is no error checking, should return 0 if there was an error
    }

  @Override
  public int getNumParams() {
    return 1;
  }
}
