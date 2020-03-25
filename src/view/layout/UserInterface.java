package view.layout;

import controller.Controller;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import model.TurtleContainer;
import view.util.ColorSelector;
import view.views.*;
import view.util.ControlPanel;
import view.util.LanguageSelector;
import view.util.Pen;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ResourceBundle;

public class UserInterface {

    private static final int HEIGHT = 576;
    private static final int WIDTH = 1080;
    private static final String RESOURCES = "resources/languages";
    private static final String DEFAULT_RESOURCE_FOLDER = "/" + RESOURCES + "/";
    private static final String DEFAULT_RESOURCE_PACKAGE = RESOURCES + ".";
    private static final String STYLESHEET = "resources/stylesheets/default.css";

    private ResourceBundle myResources;
    private HBox customizationPanel = new HBox();
    private HBox inputPanel = new HBox();
    private BorderPane mainView = new BorderPane();
    private BorderPane mainFrame = new BorderPane();
    private ViewSwitchText tabs;
    private TurtleWindow turtleWindow;
    private CommandReferenceView referenceView;
    private CommandHistoryView historyView;
    private VariableView variableView;
    private CustomizationView customizationView;
    private LibraryView libraryView;
    private ConsoleView commandPrompt;
    private LanguageSelector languageSelector;
    public InformationWindow historyWindow;
    private InformationWindow referenceWindow;
    private InformationWindow variableWindow;
    private InformationWindow libraryWindow;
    private InformationWindow customizationWindow;
    private String myLanguage;
    private final HBox console = new HBox();
    private Button turtleButton;
    private ColorSelector backgroundColorSelector;
    private ColorSelector penColorSelector;
    private ControlPanel controlPanel;

    public UserInterface(Stage stage, ResourceBundle myResources, TurtleWindow turtleWindow, ConsoleView console) throws IOException, InvocationTargetException, IllegalAccessException {
        this.turtleWindow = turtleWindow;
        this.referenceView = new CommandReferenceView(myResources, "English");
        this.commandPrompt = console;
        this.historyView = new CommandHistoryView(myResources);
        this.variableView = new VariableView(myResources);
        this.libraryView = new LibraryView(myResources);
        this.customizationView = new CustomizationView(myResources);
        this.languageSelector = new LanguageSelector(myResources);
        this.backgroundColorSelector = new ColorSelector(myResources.getString("ChooseBackground"));
        this.penColorSelector = new ColorSelector(myResources.getString("ChoosePen"));
        this.tabs = new ViewSwitchText(myResources);
        stage.setTitle(myResources.getString("Title"));
    }
    public Scene setupUI() {
        historyWindow = new InformationWindow(tabs, historyView);
        setCustomizationPanel();
        tabs.getHistoryTab().setOnMouseClicked(event -> setHistoryWindow());
        tabs.getReferenceTab().setOnMouseClicked(event -> {
            setCommandsWindow();
        });
        tabs.getVariableTab().setOnMouseClicked(event -> setVariableWindow());
        tabs.getLibraryTab().setOnMouseClicked(event -> setLibraryWindow());
        tabs.getCustomizationTab().setOnMouseClicked(event -> setCustomizationWindow());
        setFooter();
        mainView.setCenter(turtleWindow);
        mainView.setTop(customizationPanel);
        mainFrame.setCenter(mainView);
        mainFrame.setRight(historyWindow);

        Scene myScene = new Scene(mainFrame, WIDTH, HEIGHT);
        myScene.getStylesheets().add(STYLESHEET);
        return myScene;
    }
    private void setBackgroundColor(TurtleWindow turtleWindow) {
        turtleWindow.setBackground(new Background(new BackgroundFill(backgroundColorSelector.getColorPicker().getValue(), CornerRadii.EMPTY, Insets.EMPTY)));
        controlPanel.sendBGColorToController(backgroundColorSelector.getColorPicker().getValue());
//        turtleWindow.setColor(backgroundColorSelector.getColorPicker().getValue());
    }
    private void setPenColor() {
        controlPanel.sendPenColorToController(penColorSelector.getColorPicker().getValue());
    }
    private void updateLanguage() throws IOException {
        ResourceBundle r = ResourceBundle.getBundle("resources/parsing.Unicode");
        myLanguage = r.getString(languageSelector.getValue());
        myResources = ResourceBundle.getBundle(DEFAULT_RESOURCE_PACKAGE + myLanguage);
        commandPrompt.updateLanguage(myResources);
        referenceView.updateLanguage(myResources, myLanguage);
        customizationView.updateLanguage(myResources);
        controlPanel.updateLanguage(myResources, myLanguage);
        tabs.updateLanguage(myResources);
        libraryView.updateLanguage(myResources);
        variableView.updateLanguage(myResources);
        penColorSelector.updateLanguage(myResources.getString("ChoosePen"));
        backgroundColorSelector.updateLanguage(myResources.getString("ChooseBackground"));
        historyView.updateLanguage(myResources);
    }
    private void setFooter() {
        console.getChildren().add(commandPrompt.getPrompt());
        console.getChildren().add(commandPrompt);
    }
    private void setHistoryWindow() {
        mainFrame.getChildren().remove(mainFrame.getRight());
        historyWindow = new InformationWindow(tabs, historyView);
        mainFrame.setRight(historyWindow);
    }
    private void setCommandsWindow() {
        mainFrame.getChildren().remove(mainFrame.getRight());
        referenceWindow = new InformationWindow(tabs, referenceView);
        mainFrame.setRight(referenceWindow);
    }
    private void setVariableWindow() {
        mainFrame.getChildren().remove(mainFrame.getRight());
        variableWindow = new InformationWindow(tabs, variableView);
        mainFrame.setRight(variableWindow);
    }
    private void setLibraryWindow() {
        mainFrame.getChildren().remove(mainFrame.getRight());
        libraryWindow = new InformationWindow(tabs, libraryView);
        mainFrame.setRight(libraryWindow);
    }
    private void setCustomizationWindow() {
        mainFrame.getChildren().remove(mainFrame.getRight());
        customizationWindow = new InformationWindow(tabs, customizationView);
        mainFrame.setRight(customizationWindow);
    }
    private void setCustomizationPanel() {
        customizationPanel.getChildren().add(languageSelector);
        customizationPanel.getChildren().add(penColorSelector);
        customizationPanel.getChildren().add(backgroundColorSelector);
        turtleButton = controlPanel.getTurtleSwitcher();
        customizationPanel.getChildren().add(turtleButton);
        turtleButton.getStyleClass().add("turtleswitch");
        penColorSelector.getColorPicker().setOnAction(event -> setPenColor());
        backgroundColorSelector.getColorPicker().setOnAction(event -> setBackgroundColor(turtleWindow));
        languageSelector.setOnAction(event -> {
            try {
                updateLanguage();
            } catch (IOException e) {
//                    e.printStackTrace();
            }
        });
    }

    public CommandHistoryView getHistoryView() {
        return this.historyView;
    }
    public VariableView getVariableView() {
        return this.variableView;
    }
    public CustomizationView getCustomizationView() {
        return this.customizationView;
    }
    public LibraryView getLibraryView() {
        return this.libraryView;
    }
    public void addControlPanel(ControlPanel cp) {
        this.controlPanel = cp;
        inputPanel.getChildren().add(console);
        inputPanel.getChildren().add(cp);
        mainView.setBottom(inputPanel);
    }

}





