package view.views;

import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class TurtleView extends Rectangle {
    private final int TURTLE_SIZE = 50;
    private final String IMAGE_RESOURCE_PATH = "src/resources/images/";
    private final String DEFAULT_IMAGE = "turtle1.png";
    private final String INACTIVE_IMAGE = "largerinactive.png";
    private Image image;
    private ImagePattern turtlePattern;
    private int myId;
    private Tooltip myInfo;
    private String myInfoText;
    private boolean active;
    public TurtleView(int id) {
        active = true;
        myInfoText = "Id: " + id + " X: " + 0 + " Y: " + 0 + " Heading: " + 0 + "Active:" + active;
        this.image = new Image("file:" + IMAGE_RESOURCE_PATH + DEFAULT_IMAGE);
        this.myId = id;
        this.myInfo = new Tooltip(myInfoText);
        Tooltip.install(this, myInfo);
        this.turtlePattern = new ImagePattern(image);
        this.setFill(turtlePattern);
        this.setWidth(TURTLE_SIZE);
        this.setHeight(TURTLE_SIZE);
    }
    public void setTurtleRotation(double angle) {
        this.setRotate(angle);
    }
    public void setImage(String imageName) {
        this.setFill(new ImagePattern(new Image("file:" + IMAGE_RESOURCE_PATH + imageName)));
    }
    public void switchTurtleImage() {
        System.out.println("here");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(IMAGE_RESOURCE_PATH));
        Stage stage = new Stage();
        File selectedImg = fileChooser.showOpenDialog(stage);
        if (selectedImg.isFile() && checkForValidExtensions(selectedImg.getName())) {
            this.setImage(selectedImg.getName());
        }
        else {
            System.out.println("Invalid Image File Selected. Select a jpg or png File.");
        }
    }
    public int getViewId(){
        return myId;
    }
    private boolean checkForValidExtensions(String fileName) {
        return (fileName.contains(".png") || fileName.contains(".jpg"));
    }
    public void changeToolTip(int id, double xcor, double ycor, double heading){
        myInfoText = "Id: " + id + " X: " + xcor + " Y: " + ycor + " Heading: " + heading + " Active: " + active;
        myInfo.setText(myInfoText);
    }
    public void makeActive(){
        active = true;
    }
    public void makeInactive(){
        active = false;
    }
    public void changeActive(boolean b){
        active = b;
//        if(b == false){
//            turtlePattern = new ImagePattern(new Image("file:" + IMAGE_RESOURCE_PATH + INACTIVE_IMAGE));
//        }
//        else{
//            turtlePattern = new ImagePattern(new Image("file:" + IMAGE_RESOURCE_PATH + DEFAULT_IMAGE));
//        }
    }
}
