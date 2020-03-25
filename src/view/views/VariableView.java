package view.views;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Variable;

import java.io.IOException;
import java.util.*;

public class VariableView extends InformationView {
    private static final String STYLE = "vbox";
    private boolean changedVariables = false;
    private List<String> myVariables;
    private List<String> myValues;
    private List<Variable> myVariablesObjects;
    private Map<String,Integer> Map;

    public VariableView(ResourceBundle resources) {
        super(resources);
        setHeader();
        myVariables = new ArrayList<>();
        myValues = new ArrayList<>();
        myVariablesObjects = new ArrayList<>();
        Map = new HashMap<>();
    }

    /**
     * adds a new variable to be seen and manipulated by the user
     * @param variables the entire list of variables
     */
    public void addVariable(List<Variable> variables) {
        myVariablesObjects = new ArrayList<>();
        for (int i = 0; i < variables.size(); i ++) {
            Variable variable = variables.get(i);
            String varName = variable.getName();
            Double varVal = variable.getVal();
            Map.put(varName,myVariablesObjects.size());
            myVariablesObjects.add(variable);
            VBox variableNames = new VBox();
            VBox variableVals = new VBox();
            // Throw error here
            Text name = new VariableText(varName,myVariables.size());
            Text val = new VariableText(varVal.toString(),myVariables.size());
            myVariables.add(varName);
            myValues.add(varVal.toString());
            name.setWrappingWidth(250);
            val.setWrappingWidth(250);
            name.setOnMouseClicked(e->
                handleTextPressed(name,"Variable"));
            val.setOnMouseClicked(e->handleTextPressed(val,"Value"));
            variableNames.getChildren().add(name);
            variableVals.getChildren().add(val);
            createAndAddEntry(variableNames, variableVals);
        }
    }

    /**
     * creates a new entry to be seen by the user
     * @param names the names of the variables
     * @param vals the values of the variables
     */
    private void createAndAddEntry(Node names, Node vals) {
        HBox entry = new HBox();
        entry.getChildren().add(names);
        entry.getChildren().add(vals);
        names.getStyleClass().add(STYLE);
        vals.getStyleClass().add(STYLE);
        entry.getStyleClass().add(STYLE);
        super.addEntry(entry);
    }
    private void setHeader() {
        Text heading1 = new Text(resources.getString("VariableName"));
        heading1.setWrappingWidth(250);
        Text heading2 = new Text(resources.getString("VariableValue"));
        heading2.setWrappingWidth(250);
        header.getChildren().add(heading1);
        header.getChildren().add(heading2);
    }

    /**
     * opens a textArea for the user to change a variable name or value
     * @param text
     * @param type string that represents if it is the name or the value
     */
    private void handleTextPressed(Text text, String type){
        TextArea ta = new TextArea();
        ta.setPromptText("Change " +type);
        Stage s = new Stage();
        s.show();
        s.setScene(new Scene(ta));
        s.setAlwaysOnTop(true);
        ta.setOnMouseExited(f-> hideAndChangeText(s,ta,text,type));
        ta.setOnKeyPressed(k->{
            if (k.getCode() == KeyCode.ENTER){
                hideAndChangeText(s,ta,text,type);
            }
        });
    }

    /**
     *
     * @return booleen that states if variables have been changed or not
     */
    public boolean changedVariables(){
        return changedVariables;
    }

    /**
     * resets the boolean back to false meaning that the change has been handled
     */
    public void setChangedVariablesFalse(){
        changedVariables = false;
    }


    public void updateLanguage(ResourceBundle resources) {
        this.resources = resources;
        header.getChildren().clear();
        setHeader();
    }

    /**
     * hides the textarea and makes the apprpriate changes to the variableview
     * @param s the stage that holds the textarea
     * @param ta the textarea object
     * @param text
     * @param type String that reps if it was a name change or value change
     */
    private void hideAndChangeText(Stage s, TextArea ta, Text text, String type){
        s.hide();
        if(!ta.getText().equals("")){
            String newtext = ta.getText().replace("\n","");
            text.setText(newtext);
            changedVariables = true;
            updateMap(type,newtext,((VariableText)text).getIndex());
        }
    }

    /**
     * updates the map that stores the name of the variable and it's index
     * @param type string that reps if it was a value change or a name change
     * @param text the text writen in the textarea
     * @param index the index of the variable
     */
    private void updateMap(String type, String text,int index){
        Variable variable = myVariablesObjects.get(index);
        switch (type){
            case "Variable":
                variable.updateVariable(text);
                break;
            case "Value":
                try {
                    double d = Double.parseDouble(text);
                    variable.updateVariable(d);
                }
                catch (NumberFormatException e){
                    System.out.println("ERROR WRONG TYPE");
                }
                break;
        }
    }
    public List<Variable> getVariables(){
        return myVariablesObjects;
    }
    public List<String> getValues(){
        return myValues;

    }
}

