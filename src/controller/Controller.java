package controller;

import javafx.animation.RotateTransition;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import model.*;
import parsing.Parser;
import view.util.Pen;
import view.views.*;
import view.layout.TurtleWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Controller {
    private TurtleWindow turtleWindow;
    private TurtleContainer turtleContainer;

    private CustomizationView customization;
    private VariableView variableView;
    private CommandHistoryView historyView;
    private LibraryView libraryView;

    private PaletteModel paletteModel;
    private ConsoleModel consoleModel;
    private VariableModel variableModel;
    private Map<String, MethodModel> methodModels;
    private Pen pen;
    private Parser parser;
    private int index = 1;
    private double point;
    private ModelContainer allModels;
    private boolean isErrorDisplayed=false;


    public Controller(TurtleWindow turtleWindow, CustomizationView dynamicStats, CommandHistoryView historyView, VariableView variableView, LibraryView libraryView) {
        this.turtleWindow = turtleWindow;
        this.turtleContainer = new TurtleContainer(turtleWindow);
        turtleContainer.addTurtle(1);
        turtleContainer.getTurtleModelContainer().makeTurtleActive(1);
        this.consoleModel = new ConsoleModel();
        this.variableModel = new VariableModel();
        this.paletteModel = new PaletteModel();
        this.variableView = variableView;
        this.libraryView = libraryView;
        this.customization = dynamicStats;
        this.methodModels = new HashMap<>();
        this.historyView = historyView;
        this.pen = new Pen();
        for(TurtleModel turtleModel : turtleContainer.getTurtleModelContainer().getTurtleModels()){
            TurtleView turtleView = turtleContainer.getTurtleView(turtleModel.getModelId());
            turtleView.setX(turtleModel.getX() + turtleWindow.getViewWidth()/2 - turtleView.getWidth()/2);
            turtleView.setY(-turtleModel.getY() + turtleWindow.getViewHeight()/2 - turtleView.getHeight()/2);

        }

    }


    /**
     * updates the variable model if the user changes the variables through code
     */
    public void updateVariables(){
        if (variableView.changedVariables()) {
            List<Variable> viewVariables = variableView.getVariables();
            for(int i = 0; i < viewVariables.size(); i++){
                Variable variable = viewVariables.get(i);
                variableModel.updateVariable(":"+variable.getName(),variable.getVal(),true);
                System.out.println(variable.getName());
                System.out.println(variableModel.getVariables().get(0).getName());
                System.out.println(variable.getVal());
            }
            variableView.setChangedVariablesFalse();
        }
    }

    /**
     * updates the background color is the user changes the color through code
     */
    public void updateColor(){
        if(turtleContainer.getTurtleModelContainer().getActiveTurtles().get(0).getIsColorChanged()){
            List<Double> colorVals = turtleContainer.getTurtleModelContainer().getActiveTurtles().get(0).getBackgroundColor();
            turtleWindow.setColor(new Color(colorVals.get(0)/255, colorVals.get(1)/255, colorVals.get(2)/255, 1));
            allModels.getTurtleModelContainer().getActiveTurtles().get(0).setColorChanged(false);
        }
    }

    public void update() {
        updateVariables();
        updateColor();

        
        for(int t = 1; t <= turtleContainer.getTurtleModelContainer().getTurtleModels().size(); t++){
            TurtleModel turtleModel = turtleContainer.getTurtleModelContainer().getTurleModel(t);
            if (turtleModel.getDisabledStatus() && isErrorDisplayed == false) {
                turtleWindow.displayWarning();
                isErrorDisplayed = true;
            }
            else if (! turtleModel.getDisabledStatus()) {
                isErrorDisplayed = false;
            }

            int id = turtleModel.getModelId();
            handleStats(turtleModel, id);

            if ( turtleContainer.getTurtleView(id) == null){
                TurtleView newTurtleView = turtleContainer.addTurtleView(id);
                turtleWindow.getChildren().add(newTurtleView);
                newTurtleView.setX(turtleWindow.getViewWidth()/2 - newTurtleView.getWidth()/2);
                newTurtleView.setY( turtleWindow.getViewHeight()/2 - newTurtleView.getHeight()/2);
            }
            TurtleView turtleView = turtleContainer.getTurtleView(id);
            turtleView.changeActive(turtleModel.getIsActive());

            if(customization.getPenChanged()){
                turtleModel.setPenSize(customization.getPenStrokeWidth());
                customization.setPenChangedFalse();
            }
            pen.setStrokeWidth(turtleModel.getPenSize());
            pen.setDashOffset(customization.getPenStrokeOffset());


            if (turtleModel.getClearedStatus()) {
                pen.clear(turtleWindow);
                turtleView.setX(turtleWindow.getViewWidth() / 2 - turtleView.getWidth() / 2);
                turtleView.setY(turtleWindow.getViewHeight() / 2 - turtleView.getHeight() / 2);
                turtleView.setRotate(0);
                turtleModel.setCleared(false);
            }
            else {
                if (turtleModel.getPenStatus()) {
                    if (point > turtleWindow.getViewHeight() && point > turtleWindow.getViewWidth() && point < 0 && point < 0) {
                        turtleView.setVisible(false);
                    }
            createLines(turtleModel.getPointList());


                    turtleModel.clearList();
                    turtleWindow.getChildren().add(pen.draw(pen.getColor()));
                }
                else {
                    turtleModel.trackPos();
                    pen.pickUp();
                }
                turtleView.setX(turtleModel.getX() + turtleWindow.getViewWidth() / 2 - turtleView.getWidth() / 2);
                turtleView.setY(-turtleModel.getY() + turtleWindow.getViewHeight() / 2 - turtleView.getHeight() / 2);

                performRotationAnimation(turtleView, turtleModel);
                turtleView.setVisible(turtleModel.getShowing());
            }
            turtleView.changeToolTip(turtleView.getViewId(), turtleModel.getX(),turtleModel.getY(),turtleModel.getAngle());
        }
    }
    public void setPenColor(Paint color) {
        pen.setColor(color);
    }
    private void handleStats(TurtleModel model, int id) {
        customization.updateTurtleX(model.getX());
        customization.updateTurtleY(model.getY());
        customization.updatePenOffset();
        customization.updatePenThickness();
        customization.updateTurtleID(id);
        customization.updateHeading(model.getAngle());
        customization.updatePenStatus(model.getPenStatus());
        customization.updatePenColor(pen.getColor());
        customization.updateBackgroundColor(turtleWindow.getColor());
    }
    private void createLines(List<Double> linePoints) {
        for (Double d : linePoints) {
            if (index % 2 == 1) {
                point = d + turtleWindow.getViewWidth() / 2;
                index = 2;
            } else {
                point = d * -1 + turtleWindow.getViewHeight() / 2;
                index = 1;
            }
            pen.addPoint(point);
        }
    }
    private void performRotationAnimation(TurtleView view, TurtleModel model) {
        RotateTransition rt = new RotateTransition(Duration.millis(60));
        rt.setNode(view);
        rt.setAxis(Rotate.Z_AXIS);
        rt.setToAngle(model.getAngle());
        rt.play();
    }
    public void updateVariableView() {
        if (variableModel.newVarAdded()) {
            variableView.addVariable(variableModel.getVariables());
            variableModel.clearVarInfo();
        }
        variableModel.varReceived();
    }
    public void updateLibraryView() {
        for (String k :  methodModels.keySet()) {
            libraryView.addMethod(methodModels.get(k).getMethodName(), methodModels.get(k).getVariablesFE(), methodModels.get(k).getMethodBody());
        }
    }
    public void updateInputHistory(String commands){
        if (! consoleModel.getErrorMessage().equals("")) {
            historyView.displayError(consoleModel.getErrorMessage());
        }
        else {
            historyView.updateHistory(commands, consoleModel.getReturnVal());
        }
        consoleModel.setErrorMessage("");
    }
    public void createParser(String commands) {
        allModels = new ModelContainer(variableModel, consoleModel, methodModels, turtleContainer.getTurtleModelContainer(), paletteModel);
        parser = new Parser(commands, "English", allModels);
    }
    public void setBGColor(Color color) {
        for(int t = 1; t <= turtleContainer.getTurtleModelContainer().getTurtleModels().size(); t++) {
            TurtleModel turtleModel = turtleContainer.getTurtleModelContainer().getTurleModel(t);
            List<Double> l = new ArrayList<>();
            l.add(color.getGreen()*255);
            l.add(color.getRed()*255);
            l.add(color.getBlue()*255);
            turtleModel.setBackgroundColor(l);
        }
    }
    public void updateTurtleImages() {
        for(TurtleView turtleView : turtleContainer.getTurtleViews()){
            turtleView.switchTurtleImage();
        }
    }
}

