package model;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class TurtleModelContainer {

    private List<TurtleModel> activeTurtles;
    private List<TurtleModel> turtleModels;
    private List<Integer>     turtleIds;

    /**
     * wrapper object that holds all of the turtlemodels
     */
    public TurtleModelContainer(){
        activeTurtles = new ArrayList<>();
        turtleModels = new ArrayList<>();
        turtleIds = new ArrayList<>();
    }

    /**
     *
     * @return list of active turtles
     */
    public List<TurtleModel> getActiveTurtles(){
        return activeTurtles;
    }

    /**
     *
     * @return list of all turtles
     */
    public List<TurtleModel> getTurtleModels(){
        return turtleModels;
    }

    /**
     * makes the specified turtle active
     * @param id the unqiue turtle id
     */
    public void makeTurtleActive(int id){
        TurtleModel turtleModel = turtleModels.get(id-1);
        activeTurtles.add(turtleModel);
    }

    /**
     * makes the specified turtle inactive
     * @param id the unique turtle id
     */
    public void makeTurtleInactive(int id){
        TurtleModel turtleModel = turtleModels.get(id);
        activeTurtles.remove(turtleModel);
    }

    /**
     * adds a new turtle model ans sets its x,y,and angle to be 0 as default
     * @param id the new turtle model's id
     * @return returns the new turtle model
     */
    public TurtleModel addToTurtleModels(int id){
        TurtleModel turtleModel = new TurtleModel(id,0,0,0);
        turtleModels.add(turtleModel);
        turtleIds.add(id);
        return turtleModel;
    }

    /**
     * sets the list of turtles as the new active turtles
     * @param activeturtles the list of turtles
     */
    public void setActiveTurtles(List<TurtleModel> activeturtles){
        activeTurtles = activeturtles;
    }

    /**
     *
     * @return returns all of the turtle ids in a list
     */
    public List<Integer> getTurtleIds(){
        return turtleIds;
    }

    /**
     * gets specified turtle model
     * @param id the turtle's id
     * @return returns TurtleModel object
     */
    public TurtleModel getTurleModel(int id){
        for(TurtleModel turtleModel: turtleModels){
            if(id == turtleModel.getModelId()){
                return turtleModel;
            }
        }
        return null;
    }
}
