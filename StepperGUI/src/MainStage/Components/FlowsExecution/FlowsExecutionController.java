package MainStage.Components.FlowsExecution;

import Flow.FreeInputDescriptor;
import MainStage.Components.FlowsExecution.SubComponents.InputGUI.InputGUIController;
import MainStage.Components.Main.MainStepperController;
import RunHistory.FlowRunHistory;
import Stepper.StepperUIManager;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText("it works");
        errorAlert.setContentText("\"all of it just works\"\n  -Todd Howard");
        errorAlert.show();
    }

    public void setMainStepperController(MainStepperController mainStepperController){
        this.mainStepperController=mainStepperController;
    }

    public void loadFlowsExecutionFlowDetails(String flowName){
        ArrayList<FlowRunHistory> flowRunHistories =mainStepperController.getStepperUIManager().getFlowsRunHistories();
        List<FlowRunHistory> specifiedFlowRunHistories=flowRunHistories.stream().filter(flowRunHistory -> flowRunHistory.getFlowName().equals(flowName)).collect(Collectors.toList());
        for(FlowRunHistory flowRunHistory:specifiedFlowRunHistories){
            flowDetailsFlowPane.getChildren().add(new Label(flowRunHistory.showMinimalFlowHistory()));
        }
    }

    public void loadFlowsExecutionInputs(String flowName){
        StepperUIManager stepperUIManager;

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

}
