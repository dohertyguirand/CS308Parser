package model;

import view.layout.TurtleWindow;
import view.views.TurtleView;

import java.util.ArrayList;
import java.util.List;

public class TurtleContainer {
    private List<TurtleView> turtleViews;
    private TurtleWindow turtleWindow;
    private TurtleModelContainer turtleModelContainer;

    /**
     * wrapper class that contains all of the turtle views and models
     * @param turtlewindow the specific turtle window that will store the turtleviews
     */
    public TurtleContainer(TurtleWindow turtlewindow){
        turtleWindow = turtlewindow;
        turtleViews = new ArrayList<>();
        turtleModelContainer = new TurtleModelContainer();
    }

    /**
     * creates a new turtle by adding a new turtle model and turtle view
     * @param id the turtle's unique id
     * @return the new turtle model
     */
    public TurtleModel addTurtle(int id){
        TurtleModel turtleModel = turtleModelContainer.addToTurtleModels(id);
        TurtleView turtleView = addTurtleView(id);
        turtleView.setX(turtleModel.getX() + turtleWindow.getViewWidth()/2 - turtleView.getWidth()/2);
        turtleView.setY(turtleModel.getY() + turtleWindow.getViewHeight()/2 - turtleView.getHeight()/2);
        turtleWindow.getChildren().add(turtleView);
        turtleView.setManaged(true);
        return turtleModel;
    }

    /**
     * creates a new TurtleView object and adds it to the list of TurtleViews
     * @param id the turtle's id
     * @return the vew TurtleView
     */
    public TurtleView addTurtleView(int id){
        TurtleView turtleView = new TurtleView(id);
        turtleViews.add(turtleView);
        return turtleView;
    }

    /**
     *
     * @return list of TurtleViews
     */
    public List<TurtleView> getTurtleViews(){
        return turtleViews;
    }

    /**
     *
     * @return the turtleModelContainer storing all of the turtle models
     */
    public TurtleModelContainer getTurtleModelContainer(){
        return turtleModelContainer;
    }

    /**
     * loops through all of the turtleviews returns the turtleview with the given id
     * @param id the turtle view id
     * @return specified TurtleView
     */
    public TurtleView getTurtleView(int id){
        for(TurtleView turtleView: turtleViews){
            if(id == turtleView.getViewId()){
                return turtleView;
            }
        }
        return null;
    }

}
