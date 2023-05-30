package MainStage.Components.FlowsExecution;

import Flow.FreeInputDescriptor;
import MainStage.Components.FlowsExecution.SubComponents.InputGUI.InputGUIController;
import MainStage.Components.Main.MainStepperController;
import Stepper.StepperUIManager;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.HashMap;

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
    private HashMap<String,InputGUIController> inputGUIControllerHashMap;

    private SimpleBooleanProperty allMandatoryInputsFilled;
    private SimpleStringProperty selectedFlow;

    @FXML
    public  void  initialize(){
        allMandatoryInputsFilled=new SimpleBooleanProperty(false);
        startFlowExecutionButton.disableProperty().bind(allMandatoryInputsFilled.not());
        selectedFlow=new SimpleStringProperty("");
        inputGUIControllerHashMap=new HashMap<>();
    }

    @FXML
    void flowContinuationExecuteAction(ActionEvent event) {

    }

    @FXML
    void startFlowExecutionAction(ActionEvent event) {

    }

    public void setMainStepperController(MainStepperController mainStepperController){
        this.mainStepperController=mainStepperController;
    }

    public void loadFlowsExecutionInputs(String flowName){
        StepperUIManager stepperUIManager;

        flowInputsFlowPane.getChildren().clear();
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
                allMandatoryInputsFilled.bind(inputGUIController.isTextFieldFilled());

                flowInputsFlowPane.setPrefWrapLength(flowInputsFlowPane.getPrefWrapLength()+inputGUI.getPrefWidth()+30.0);
                flowInputsFlowPane.getChildren().add(inputGUI);
                inputGUIControllerHashMap.put(freeInputDescriptor.getInputEffectiveName(),inputGUIController);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
