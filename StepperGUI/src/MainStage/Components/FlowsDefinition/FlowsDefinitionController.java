package MainStage.Components.FlowsDefinition;

import MainStage.Components.FlowsDefinition.SubComponents.FlowDefinitionButtonController;
import MainStage.Components.Main.MainStepperController;
import Stepper.StepperUIManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FlowsDefinitionController {


    @FXML
    private FlowPane availableFlowsFlowPane;
    @FXML
    private FlowPane flowDetailsFlowPane;
    @FXML
    private Button executeFlowButton;

    private MainStepperController mainStepperController;

    private Map<String,FlowDefinitionButtonController> flowsButtonsMap;

    @FXML
    private void initialize(){
        flowsButtonsMap=new HashMap<>();
    }

    @FXML
    void executeFlowButtonAction(ActionEvent event) {

    }

    public void setMainStepperController(MainStepperController mainStepperController){
        this.mainStepperController=mainStepperController;
    }

    public void loadFlowsButtons(StepperUIManager stepperUIManager){
        for(String flowName:stepperUIManager.getFlowNames()){
            try {
                FXMLLoader loader=new FXMLLoader();
                loader.setLocation(getClass().getResource("/MainStage/Components/FlowsDefinition/SubComponents/FlowDefinitionButton.fxml"));
                Button flowButton=loader.load();

                FlowDefinitionButtonController flowDefinitionButtonController = loader.getController();
                flowDefinitionButtonController.setFlowsDefinitionsController(this);
                flowDefinitionButtonController.setFlowButtonText(stepperUIManager.getFlowDescriptor(flowName));

                availableFlowsFlowPane.getChildren().add(flowButton);
                flowsButtonsMap.put(flowName,flowDefinitionButtonController);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void showFlowDetails(SimpleStringProperty flowName){

    }

}
