package MainStage.Components.FlowsExecution;

import Flow.FreeInputDescriptor;
import MainStage.Components.FlowsExecution.SubComponents.InputGUI.InputGUIController;
import MainStage.Components.Main.MainStepperController;
import RunHistory.FlowRunHistory;
import RunHistory.FreeInputHistory;
import RunHistory.OutputHistory;
import RunHistory.StepHistory;
import Stepper.StepperUIManager;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class FlowsExecutionController {

    @FXML
    private FlowPane flowInputsFlowPane;
    @FXML
    private FlowPane flowDetailsFlowPane;
    @FXML
    private FlowPane executionDetailsFlowPane;
    @FXML
    private Button flowContinuationExecuteButton;
    @FXML
    private FlowPane continuationDataFlowPane;
    @FXML
    private Button startFlowExecutionButton;


    private MainStepperController mainStepperController;
    private HashSet<InputGUIController> inputGUIControllers;

    private SimpleBooleanProperty allMandatoryInputsFilled;
    private SimpleStringProperty selectedFlow;

    @FXML
    public  void  initialize(){
        allMandatoryInputsFilled=new SimpleBooleanProperty(false);
        startFlowExecutionButton.disableProperty().bind(allMandatoryInputsFilled.not());
        selectedFlow=new SimpleStringProperty("");
        inputGUIControllers =new HashSet<>();
    }

    @FXML
    void flowContinuationExecuteAction(ActionEvent event) {

    }

    @FXML
    void startFlowExecutionAction(ActionEvent event) {
        StepperUIManager stepperUIManager=mainStepperController.getStepperUIManager();
        String inputName="";

        try {
            for (InputGUIController inputGUIController : inputGUIControllers) {
                inputName=inputGUIController.getInputName();
                stepperUIManager.setFreeInput(selectedFlow.get(), inputGUIController.getInputName(), inputGUIController.getInput());
            }
        }
        catch (Exception e){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Input Invalid");
            errorAlert.setContentText("Input: "+inputName+" "+e.getMessage());
            errorAlert.show();
            return;
        }
        try{
        stepperUIManager.runFlow(selectedFlow.get());
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        mainStepperController.updatePastExecutionsTable();
        startFlowExecutionButton.setText("Rerun Flow");
        updateFlowDetailsFlowPane();
    }

    public void setMainStepperController(MainStepperController mainStepperController){
        this.mainStepperController=mainStepperController;
    }

    private void updateFlowDetailsFlowPane(){
        flowDetailsFlowPane.getChildren().clear();
        FlowRunHistory flowRunHistory=mainStepperController.getStepperUIManager().getFlowsRunHistories().get(mainStepperController.getStepperUIManager().getFlowsRunHistories().size()-1);
        flowDetailsFlowPane.getChildren().add(new Label(flowRunHistory.showGUIFlowHistory()));
        flowDetailsFlowPane.getChildren().add(new Label());
        flowDetailsFlowPane.getChildren().add(new Label("Steps:"));
        for(StepHistory stepHistory:flowRunHistory.getStepHistories()) {
            Hyperlink stepHyperLink=new Hyperlink(stepHistory.getName()+" Status: "+stepHistory.getStatus());
            stepHyperLink.setOnAction(event -> updateExecutionDetailsFlowPane(stepHistory));
            flowDetailsFlowPane.getChildren().add(stepHyperLink);
        }
    }

    private void updateExecutionDetailsFlowPane(StepHistory stepHistory){
        executionDetailsFlowPane.getChildren().clear();
        executionDetailsFlowPane.getChildren().add(new Label("Step name: "+stepHistory.getName()));
        executionDetailsFlowPane.getChildren().add(new Label("Status: "+stepHistory.getStatus()));
        executionDetailsFlowPane.getChildren().add(new Label("Run Time: "+stepHistory.getRunTimeInMs()));
        executionDetailsFlowPane.getChildren().add(new Label());
        if(!stepHistory.getInputs().isEmpty()) {
            executionDetailsFlowPane.getChildren().add(new Label("Inputs:"));
            for (FreeInputHistory input : stepHistory.getInputs())
                executionDetailsFlowPane.getChildren().add(new Label("Name: " + input.getName() + (input.getAlias() != null ? (" Alias: " + input.getAlias()) : "") + " User presentation:\n" + input.getPresentableString()));
        }

        if(!stepHistory.getOutputs().isEmpty()) {
            executionDetailsFlowPane.getChildren().add(new Label("Outputs:"));
            for (OutputHistory output : stepHistory.getOutputs())
                executionDetailsFlowPane.getChildren().add(new Label("Name: " + output.getName() + (output.getAlias() != null ? (" Alias: " + output.getAlias()) : "") + " User presentation:\n" + output.getPresentableString()));
        }
    }

    public void loadFlowsExecutionFlowDetails(String flowName){
//        ArrayList<FlowRunHistory> flowRunHistories =mainStepperController.getStepperUIManager().getFlowsRunHistories();
//        List<FlowRunHistory> specifiedFlowRunHistories=flowRunHistories.stream().filter(flowRunHistory -> flowRunHistory.getFlowName().equals(flowName)).collect(Collectors.toList());
//        for(FlowRunHistory flowRunHistory:specifiedFlowRunHistories){
//            flowDetailsFlowPane.getChildren().add(new Label(flowRunHistory.showMinimalFlowHistory()));
//        }
    }

    public void loadFlowsExecutionInputs(String flowName){
        StepperUIManager stepperUIManager;

        startFlowExecutionButton.setText("Start!");
        allMandatoryInputsFilled.set(false);
        selectedFlow.set(flowName);
        flowInputsFlowPane.getChildren().clear();
        inputGUIControllers.clear();

        if(flowName.isEmpty())
            return;
        stepperUIManager = mainStepperController.getStepperUIManager();
        flowInputsFlowPane.setPrefWrapLength(0);
        for(FreeInputDescriptor freeInputDescriptor:stepperUIManager.getFreeInputDescriptorsByFlow(flowName)){
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/MainStage/Components/FlowsExecution/SubComponents/InputGUI/InputGUI.fxml"));
                GridPane inputGUI = loader.load();

                InputGUIController inputGUIController=loader.getController();
                inputGUIController.setFlowsExecutionController(this);
                inputGUIController.setInputLabel(freeInputDescriptor.getInputEffectiveName());
                inputGUIController.setPromptTextFieldText(freeInputDescriptor.isMandatory()?"Mandatory":"Optional");
                inputGUIController.setMandatory(freeInputDescriptor.isMandatory());
                inputGUIController.getInputTextField().textProperty().addListener((observable, oldValue, newValue)->checkFieldsAreFilled());

                flowInputsFlowPane.setPrefWrapLength(flowInputsFlowPane.getPrefWrapLength()+inputGUI.getPrefWidth()+30.0);
                if(freeInputDescriptor.isMandatory())
                    flowInputsFlowPane.getChildren().add(0,inputGUI);
                else
                    flowInputsFlowPane.getChildren().add(inputGUI);
                inputGUIControllers.add(inputGUIController);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void checkFieldsAreFilled(){
        allMandatoryInputsFilled.set(inputGUIControllers
                .stream()
                .allMatch(inputGUIController -> !inputGUIController.isMandatory() || !inputGUIController.getInputTextField().getText().isEmpty()));
    }

    public ObservableList<Node> getExecutionDetailsFlowPaneChildrenNodes() {
        return executionDetailsFlowPane.getChildren();
    }

    public void restartUIElements() {
        flowDetailsFlowPane.getChildren().clear();
        flowInputsFlowPane.getChildren().clear();
        executionDetailsFlowPane.getChildren().clear();
        continuationDataFlowPane.getChildren().clear();
        selectedFlow.set("");
        inputGUIControllers.clear();
        allMandatoryInputsFilled.set(false);
    }
}
