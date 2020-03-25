package model;

import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class    VariableModel {

    /**
     * holds and manipulates all of the variable objects that stores the user created variables
     */

    private HashMap<String, Double> myMap;
    private boolean isNewVarAdded;
    private List<Variable> myVariables;
    private Map<String,Integer> Map;

    public VariableModel(){
        myMap = new HashMap<>();
        myVariables = new ArrayList<>();
        Map = new HashMap<>();
    }

    /**
     * adds a new variable to the variable model by created a new variable object and putting it in the map
     * @param name the name of the variable ie x
     * @param value the value associated with that variable
     * @return returns the Variable created
     */
    public Variable addVariable(String name, double value){
        Variable variable = new Variable(myVariables.size(),name,value);
        Map.put(name,myVariables.size());
        myVariables.add(variable);
        return variable;
    }

    /**
     * updates an existing variable or create a new one
     * @param variableName the name associated with that variable
     * @param value that value associated with that variable
     * @param fromFront booleen that tells if the change is from the User interface or from the parser
     */
    public void updateVariable(String variableName, double value,boolean fromFront){
        String variable = variableName.substring(1);
        if(!Map.containsKey(variable)) {
            addVariable(variable, value);
        }
        else{
            int index = Map.get(variable);
            System.out.println(index);
            Variable v = myVariables.get(index);
            String vName = v.getName();
            double vVal = v.getVal();
            if(!vName.equals(variable)){
                v.updateVariable(variable);
            }
            if(vVal != value){
                v.updateVariable(value);
            }

        }
        if(!fromFront) isNewVarAdded = true;
        System.out.println(variable + value);
    }



    public boolean checkIfVariableExists(String variableName){
        String variable = variableName.substring(1);
        return myMap.containsKey(variable);
    }

    /**
     * returns the value associated with a given name
     * @param variableName string that represents the variable name
     * @return returns the value or returns 0 if doesn't exist
     */
    public double getValue(String variableName){
        String variable = variableName.substring(1);
        if(!Map.containsKey(variable)) return 0;
        int index = Map.get(variable);
        Variable v = myVariables.get(index);
        return v.getVal();
    }

    public double addVariables(String variable1, String variable2){
        String var1 = variable1.substring(1);
        String var2 = variable2.substring(1);
        double varival = myMap.get(var1);
        double varival2 = myMap.get(var2);
        double sum = varival +varival2;
        return sum;
    }

    /**
     *
     * @return boolean that states if a new variable has been added to the model from the parser
     */
    public boolean newVarAdded() {
        return this.isNewVarAdded;
    }

    /**
     * changes isNewVarAdded back to false when the controller makes the appropriate changes to the front end
     */
    public void varReceived() {
        isNewVarAdded = false;
    }

    /**
     * clears all of the variables
     */
    public void clearVarInfo() {
      //  myVariables.clear();
    }

    /**
     *
     * @return returns the list of variables
     */
    public List<Variable> getVariables(){
        return myVariables;
    }
}
