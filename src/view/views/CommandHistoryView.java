package view.views;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CommandHistoryView extends InformationView {
    private int lineNum;
    private static final String STYLE = "vbox";

    public CommandHistoryView(ResourceBundle resources) {
        super(resources);
        lineNum = 1;
        setHeader();
    }
    public void updateLanguage(ResourceBundle resources) {
        this.resources = resources;
        header.getChildren().clear();
        setHeader();
    }
    public void updateHistory(String input, double returnVal) {
        Text consoleInput = new Text(input);
        Text lineNumber = new Text(Integer.toString(lineNum));
        Text consoleOutput = new Text(Double.toString(returnVal));
        consoleInput.setWrappingWidth(250); //remove magic number
        consoleOutput.setWrappingWidth(250);
        VBox lineNumbers =  new VBox();
        VBox inputs = new VBox();
        VBox outputs = new VBox();
        lineNumbers.getStyleClass().add(STYLE);
        inputs.getStyleClass().add(STYLE);
        outputs.getStyleClass().add(STYLE);;
        lineNumbers.getChildren().add(lineNumber);
        inputs.getChildren().add(consoleInput);
        outputs.getChildren().add(consoleOutput);
        createAndAddEntry(lineNumbers, inputs, outputs,input);
        lineNum ++;
    }
    private void createAndAddEntry(Node nums, Node in, Node out, String input) {
        HBox entry = new Library(input);
        entry.getStyleClass().add(STYLE);
        entry.getChildren().add(nums);
        entry.getChildren().add(in);
        entry.getChildren().add(out);
        super.addEntry(entry);
    }
    public void displayError(String input) {
        if (input != null) {
            Text error = new Text(input);
            Text lineNumber = new Text(Integer.toString(lineNum));
            error.setFill(Color.RED);
            VBox lineNumbers =  new VBox();
            VBox inputs = new VBox();
            lineNumbers.getChildren().add(lineNumber);
            inputs.getChildren().add(error);
            lineNumbers.getStyleClass().add(STYLE);
            inputs.getStyleClass().add(STYLE);
            createAndAddEntry(lineNumbers, inputs, new Text(""),"ERROR");

            lineNum++;
        }
    }
    private void setHeader() {
        Text heading1 = new Text(resources.getString("HistoryInput"));
        Text heading2 = new Text(resources.getString("HistoryOutput"));
        heading1.setWrappingWidth(250);
        heading2.setWrappingWidth(250);
        header.getChildren().add(heading1);
        header.getChildren().add(heading2);
        //header.getStyleClass().add()
    }
    public void clearHistory() {
        lineNum=1;
        clear();
    }
}

