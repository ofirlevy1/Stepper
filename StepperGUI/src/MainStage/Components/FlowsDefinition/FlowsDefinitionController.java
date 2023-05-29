package MainStage.Components.FlowsDefinition;

import Flow.FlowDescriptor;
import Flow.FreeInputDescriptor;
import Flow.StepOutputDescriptor;
import MainStage.Components.FlowsDefinition.SubComponents.FlowDefinitionButtonController;
import MainStage.Components.FlowsDefinition.SubComponents.FlowsDefinitionStepToolTipLabelController;
import MainStage.Components.Main.MainStepperController;
import Stepper.StepperUIManager;
import Steps.StepDescriptor;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;

import java.io.IOException;
import java.util.HashMap;

public class FlowsDefinitionController {


    @FXML
    private FlowPane availableFlowsFlowPane;
    @FXML
    private FlowPane flowDetailsFlowPane;
    @FXML
    private Button executeFlowButton;

    private MainStepperController mainStepperController;

    private SimpleStringProperty currentSelectedFlow;

    private HashMap<String,FlowDefinitionButtonController> flowsButtonsMap;
    private HashMap<String, FlowsDefinitionStepToolTipLabelController> flowsDefinitionStepToolTipLabelControllerMap;

    @FXML
    private void initialize(){
        flowsButtonsMap=new HashMap<>();
        flowsDefinitionStepToolTipLabelControllerMap =new HashMap<>();
        currentSelectedFlow=new SimpleStringProperty();
    }

    @FXML
    void executeFlowButtonAction(ActionEvent event) {
        mainStepperController.switchTabs(MainStepperController.Tabs.FlowsExecutionTab, currentSelectedFlow.get());

    }

    public void setMainStepperController(MainStepperController mainStepperController){
        this.mainStepperController=mainStepperController;
    }

    public void loadFlowsButtons(StepperUIManager stepperUIManager){
        flowsButtonsMap.clear();
        availableFlowsFlowPane.getChildren().clear();

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
        currentSelectedFlow.set(String.valueOf(flowName));
        FlowDescriptor flowDescriptor= mainStepperController.getFlowDescriptor(flowName.get());
        flowDetailsFlowPane.getChildren().clear();
        flowsDefinitionStepToolTipLabelControllerMap.clear();

        flowDetailsFlowPane.getChildren().add(new Label("Flow Name: " + flowDescriptor.getFlowName()));
        flowDetailsFlowPane.getChildren().add(new Label("Flow Description: " + flowDescriptor.getFlowDescription()));
        flowDetailsFlowPane.getChildren().add(new Label("Formal Outputs: " + flowDescriptor.getFormalOutputNames().toString()));
        flowDetailsFlowPane.getChildren().add(new Label(flowDescriptor.isReadonly()?"Read only":"Not read only"));
        flowDetailsFlowPane.getChildren().add(new Label("Steps"));
        for(StepDescriptor stepDescriptor:flowDescriptor.getStepDescriptors())
            flowDetailsFlowPane.getChildren().add(new Label("Step Name:"+stepDescriptor.getStepName()+(stepDescriptor.isHasAlias()?(", Alias name:"+stepDescriptor.getStepAlias()):"")+(stepDescriptor.isReadOnly()?" is read only":"is not read only")));
        flowDetailsFlowPane.getChildren().add(new Label("Free Inputs:"));
        for(FreeInputDescriptor freeInputDescriptor:flowDescriptor.getFreeInputs())
            flowDetailsFlowPane.getChildren().add(new Label("Free Input Name:"+freeInputDescriptor.getInputEffectiveName()+", Type: "+freeInputDescriptor.getInputType().toString()+", Connected Steps: "+freeInputDescriptor.getAssociatedSteps()+" the input is"+(freeInputDescriptor.isMandatory()?" mandatory":" not mandatory")));
        flowDetailsFlowPane.getChildren().add(new Label("Formal Outputs"));
        for(StepOutputDescriptor stepOutputDescriptor:flowDescriptor.getOutputs())
            flowDetailsFlowPane.getChildren().add(new Label("Formal Output Name: "+stepOutputDescriptor.getOutputEffectiveName()+" Type: "+stepOutputDescriptor.getOutputType().toString()+" produced by step: "+stepOutputDescriptor.getSourceStepName()));
        flowDetailsFlowPane.getChildren().add(new Label("Hover on Next Steps For Further Info:"));

        for(StepDescriptor stepDescriptor:flowDescriptor.getStepDescriptors())
        {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/MainStage/Components/FlowsDefinition/SubComponents/FlowsDefinitionStepToolTipLabel.fxml"));
                Label stepLabel = loader.load();

                FlowsDefinitionStepToolTipLabelController flowsDefinitionStepToolTipLabelController = loader.getController();
                flowsDefinitionStepToolTipLabelController.setFlowsDefinitionController(this);
                flowsDefinitionStepToolTipLabelController.setStepLabelToolTipText(stepDescriptor);

                flowDetailsFlowPane.getChildren().add(stepLabel);
                flowsDefinitionStepToolTipLabelControllerMap.put(stepDescriptor.getStepEffectiveName(),flowsDefinitionStepToolTipLabelController);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
