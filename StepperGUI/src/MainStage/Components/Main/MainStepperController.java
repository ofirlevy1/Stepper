package MainStage.Components.Main;

import Flow.FlowDescriptor;
import MainStage.Components.ExecutionsHistory.ExecutionsHistoryController;
import MainStage.Components.FlowsDefinition.FlowsDefinitionController;
import MainStage.Components.FlowsExecution.FlowsExecutionController;
import MainStage.Components.Statistics.StatisticsController;
import Stepper.StepperUIManager;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

public class MainStepperController {

    @FXML
    private Label filePathLabel;
    @FXML
    private Button loadFileButton;
    @FXML
    private TabPane selectionTabPane;
    @FXML
    private BorderPane flowsDefinition;
    @FXML
    private FlowsDefinitionController flowsDefinitionController;
    @FXML
    private BorderPane flowsExecution;
    @FXML
    private FlowsExecutionController flowsExecutionController;
    @FXML
    private BorderPane executionsHistory;
    @FXML
    private ExecutionsHistoryController executionsHistoryController;
    @FXML
    private BorderPane statistics;
    @FXML
    private StatisticsController statisticsController;

    private SimpleStringProperty absoluteFilePath;
    private SimpleBooleanProperty fileLoaded;

    private Stage primaryStage;
    private StepperUIManager stepperUIManager;
    private Thread historyAndStatisticsUpdater;

    public MainStepperController(){
        absoluteFilePath=new SimpleStringProperty("File Not Loaded");
        fileLoaded=new SimpleBooleanProperty(false);
    }

    public void setPrimaryStage(Stage primaryStage){
        this.primaryStage=primaryStage;
        stepperUIManager=new StepperUIManager();
    }

    @FXML
    private void initialize(){
        filePathLabel.textProperty().bind(absoluteFilePath);
        selectionTabPane.disableProperty().bind(fileLoaded.not());
        this.flowsDefinitionController.setMainStepperController(this);
        this.flowsExecutionController.setMainStepperController(this);
        this.executionsHistoryController.setMainStepperController(this);
        this.statisticsController.setMainStepperController(this);
    }

    @FXML
    void loadFileButtonAction(ActionEvent event) {
        FileChooser fileChooser=new FileChooser();
        fileChooser.setTitle("Select xml file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile == null)
            return;
        if(historyAndStatisticsUpdater!=null)
            historyAndStatisticsUpdater.interrupt();
        try {
            stepperUIManager.LoadStepperFromXmlFile(selectedFile.getAbsolutePath());
        }
        catch (Exception e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("File Invalid");
            errorAlert.setContentText(e.getMessage());
            absoluteFilePath.set("File Not Loaded");
            fileLoaded.set(false);
            errorAlert.show();
            if(historyAndStatisticsUpdater!=null) {
                historyAndStatisticsUpdater = new Thread(this::updateHistoryAndStatistics);
                historyAndStatisticsUpdater.start();
            }
            return;
        }
        restartUIElements();
        flowsDefinitionController.loadFlowsButtons(stepperUIManager); //need to obtain all the flow descriptors;
        String absolutPath=selectedFile.getAbsolutePath();
        absoluteFilePath.set(absolutPath);
        fileLoaded.set(true);
        historyAndStatisticsUpdater=new Thread(this::updateHistoryAndStatistics);
        historyAndStatisticsUpdater.start();
    }

    public void switchTabs(Tabs tab,String flowName){
        selectionTabPane.getSelectionModel().select(tab.ordinal());
        flowsExecutionController.loadFlowsExecutionInputs(flowName);
        flowsExecutionController.loadFlowsExecutionFlowDetails(flowName);
    }

    public void updateHistoryAndStatistics(){
        try{
            while (true) {
                Thread.sleep(1000);
                if (stepperUIManager.getFlowsRunHistories() != null && !stepperUIManager.getFlowsRunHistories().isEmpty())
                    Platform.runLater(() -> executionsHistoryController.updateExecutionsTable());
                if (stepperUIManager.getFlowStatistics() != null && !stepperUIManager.getFlowStatistics().isEmpty())
                    Platform.runLater(() -> statisticsController.updateStatisticsTables());
            }
        }
        catch (InterruptedException e){
        }
    }

    public void rerunFlow(String flowName, HashMap<String ,String> freeInputsMap){
        selectionTabPane.getSelectionModel().select(Tabs.FlowsExecutionTab.ordinal());
        flowsExecutionController.loadFlowsExecutionInputsRerun(flowName, freeInputsMap);
    }

    public void restartUIElements(){
        this.flowsDefinitionController.restartUIElements();
        this.flowsExecutionController.restartUIElements();
        this.executionsHistoryController.restartUIElements();
        this.statisticsController.restartUIElements();
    }

    public void updateStatisticsTables(){
        this.statisticsController.updateStatisticsTables();
    }

    public StepperUIManager getStepperUIManager() {
        return stepperUIManager;
    }

    public FlowDescriptor getFlowDescriptor(String flowName){
        return stepperUIManager.getFlowDescriptor(flowName);
    }

    public void updatePastExecutionsTable(){
        this.executionsHistoryController.updateExecutionsTable();
    }

    public ObservableList<Node> getFlowRunHistoryChildrenNodesFromFlowsExecution(){
        return this.flowsExecutionController.getExecutionDetailsFlowPaneChildrenNodes();
    }

    public enum Tabs{
        FlowsDefinitionTab,
        FlowsExecutionTab,
        ExecutionsHistoryTab,
        Statistics

    }
}
