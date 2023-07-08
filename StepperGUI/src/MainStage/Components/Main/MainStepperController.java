package MainStage.Components.Main;

import Flow.FlowDescriptor;
import MainStage.Components.ExecutionsHistory.ExecutionsHistoryController;
import MainStage.Components.FlowsDefinition.FlowsDefinitionController;
import MainStage.Components.FlowsExecution.FlowsExecutionController;
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
    private Label userNameLabel;
    @FXML
    private Label isManagerLabel;
    @FXML
    private Label rolesLabel;
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

    private Stage primaryStage;


    public MainStepperController(){
    }

    public void setPrimaryStage(Stage primaryStage){
        this.primaryStage=primaryStage;
    }

    @FXML
    private void initialize(){
        this.flowsDefinitionController.setMainStepperController(this);
        this.flowsExecutionController.setMainStepperController(this);
        this.executionsHistoryController.setMainStepperController(this);
    }

    public void switchTabs(Tabs tab,String flowName){
        selectionTabPane.getSelectionModel().select(tab.ordinal());
        flowsExecutionController.loadFlowsExecutionInputs(flowName);
        flowsExecutionController.loadFlowsExecutionFlowDetails(flowName);
    }

    public void updateHistoryAndStatistics(){
    }

    public void rerunFlow(String flowName, HashMap<String ,String> freeInputsMap){
        selectionTabPane.getSelectionModel().select(Tabs.FlowsExecutionTab.ordinal());
        flowsExecutionController.loadFlowsExecutionInputsRerun(flowName, freeInputsMap);
    }

    public void restartUIElements(){
        this.flowsDefinitionController.restartUIElements();
        this.flowsExecutionController.restartUIElements();
        this.executionsHistoryController.restartUIElements();
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
