package view.views;

import javafx.scene.control.ScrollPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;
public class LibraryView extends InformationView {
    private static final String STYLE = "vbox";
    private final int SPACING = 250;

    public LibraryView(ResourceBundle resources) {
        super(resources);
        setHeader();

    }

    /**
     * adds a new method to be seen by the user
     * @param name the name of the method
     * @param variables variables needed to run the method
     * @param body the body of the method
     */
    public void addMethod(String name, List<String> variables, String body) {
        Text methodBody = new Text(body);
        methodBody.setWrappingWidth(SPACING);

        String v = "";
        for(String d : variables){
            v += " " + d;
        }
        v = " [ " + v + " ]";
        body = " [ " + body + " ]";
        Text methodName = new Text(name + v);
        methodName.setWrappingWidth(250);
        methodBody.setWrappingWidth(250);
        VBox names = new VBox();
        VBox bodies = new VBox();
        names.getStyleClass().add(STYLE);
        bodies.getStyleClass().add(STYLE);
        names.getChildren().add(methodName);
        bodies.getChildren().add(methodBody);
        HBox entry = new Library(  "to "+name + " " +v + body);
        entry.getChildren().add(names);
        entry.getChildren().add(bodies);
        entry.getStyleClass().add(STYLE);
        super.addEntry(entry);
    }
    private void setHeader() {
        Text heading1 = new Text(resources.getString("MethodName"));
        heading1.setWrappingWidth(SPACING);
        Text heading2 = new Text(resources.getString("MethodBody"));
        heading2.setWrappingWidth(SPACING);
        header.getChildren().add(heading1);
        header.getChildren().add(heading2);
    }
    public void updateLanguage(ResourceBundle resources) {
        this.resources = resources;
        header.getChildren().clear();
        setHeader();
    }




}
