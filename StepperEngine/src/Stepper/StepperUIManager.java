package Stepper;

// This object acts as a Facade, offering the Stepper system UI functions
// and managing the stepper object itself

import Flow.*;
import RunHistory.FlowRunHistory;
import Steps.*;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class StepperUIManager {
    Stepper stepper;
    boolean isLoaded;

    public StepperUIManager() {
        isLoaded = false;
    }

    public void LoadStepperFromXmlFile(String xmlFilePath) throws FileNotFoundException, JAXBException {
        // First assigning it to a new Stepper object, to not override anything in case of failure.
        Stepper stepperCpy = new Stepper(xmlFilePath);

        // If we got here, no exceptions were thrown, thus the stepper was loaded successfully.
        // So now we can override the actual stepper:
        this.stepper = stepperCpy;

        isLoaded = true;
    }

    public ArrayList<String> getFlowNames(){
        return stepper.getFlowNames();
    }

    public FlowDescriptor getFlowDescriptor(String flowName) {
        return stepper.getFlowDescriptor(flowName);
    }

    public ArrayList<FreeInputDescriptor> getFreeInputDescriptorsByFlow(String flowName) {
        return stepper.getFreeInputDescriptorsByFlow(flowName);
    }

    public void setFreeInput(String flowName, String freeInputEffectiveName, String dataStr) {
        stepper.setFreeInput(flowName, freeInputEffectiveName, dataStr);
    }

    public boolean areAllMandatoryFreeInputsSet(String flowName) {
        return stepper.areAllMandatoryFreeInputsSet(flowName);
    }

    public void runFlow(String flowName) {
        stepper.runFlow(flowName);
    }

    public ArrayList<FlowRunHistory> getFlowsRunHistories() {
        return stepper.getFlowsRunHistories();
    }

    public ArrayList<FlowStatistics> getFlowStatistics() {
        return stepper.getFlowStatistics();
    }

    public ArrayList<StepStatistics> getStepsStatistics() {
        return stepper.getStepsStatistics();
    }

    public boolean isLoaded() {
        return isLoaded;
    }
}
