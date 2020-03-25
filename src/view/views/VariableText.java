package view.views;

import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.VariableModel;

public class VariableText extends Text{
    private int myIndex;

    /**
     * variableText object stores the text needed to create that variable in the console
     * @param name the name of the variable
     * @param index that variable's index
     */
    public VariableText(String name, int index){
        myIndex= index;
        setText(name);

    }

    /**
     *
     * @return the variable's index
     */
    public int getIndex(){
        return myIndex;
    }
}
