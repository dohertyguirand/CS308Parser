package view.views;

import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;

public class Library extends HBox {
    private String myText;

    /**
     * library is an object that is able to be clicked on to be updated to draged into the console
     * @param text the text associated with that object
     */
    public Library(String text){
        myText = text;
        this.setOnDragDetected(e-> {
            Dragboard db = this.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            content.putString((this).getText());
            db.setContent(content);
        });
    }

    /**
     *
     * @return the text associated with the object
     */
    public String getText(){
        return myText;
    }

    /**
     *
     * @param text the new text associated with that object
     */
    public void setText(String text){
        myText = text;
    }
}
