package view.views;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
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
            variableNames.setOnMouseEntered(e-> variableNames.setStyle("-fx-border-color: pink;"));
            variableNames.setOnMouseExited(e-> variableNames.setStyle("-fx-border-color: black;"));
            variableVals.setOnMouseEntered(e-> variableVals.setStyle("-fx-border-color: pink;"));
            variableVals.setOnMouseExited(e-> variableVals.setStyle("-fx-border-color: black;"));
            variableNames.getChildren().add(name);
            variableVals.getChildren().add(val);
            String text = "set :" + varName + " " + varVal;
            createAndAddEntry(variableNames, variableVals,text);
        }
    }
    private void createAndAddEntry(Node names, Node vals, String text) {
        HBox entry = new Library(text);
        entry.setOnDragDetected(e-> {
            Dragboard db = entry.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            content.putString(((Library)entry).getText());
            db.setContent(content);
        });
        entry.setOnMouseEntered(e-> entry.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY))));
        entry.setOnMouseExited(e-> entry.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY))));
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

    private void handleTextPressed(Text text, String type){
        TextArea ta = new TextArea();
        ta.setPromptText("Change " +type+ " Name");
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
    public boolean isChangedVariables(){
        return changedVariables;
    }
    public void setChangedVariablesFalse(){
        changedVariables = false;
    }

    public void updateLanguage(ResourceBundle resources) {
        this.resources = resources;
        header.getChildren().clear();
        setHeader();
    }
    private void hideAndChangeText(Stage s, TextArea ta, Text text, String type){
        s.hide();
        if(!ta.getText().equals("")){
            String newtext = ta.getText().replace("\n","");
            text.setText(newtext);
            changedVariables = true;
            updateMap(type,newtext,((VariableText)text).getIndex());
        }
    }
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

