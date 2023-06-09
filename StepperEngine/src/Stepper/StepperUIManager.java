package Stepper;

// This object acts as a Facade, offering the Stepper system UI functions
// and managing the stepper object itself

import Flow.*;
import RunHistory.FlowRunHistory;
import Steps.*;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class StepperUIManager {
    Stepper stepper;
    boolean isLoaded;
    boolean isFlowRan;

    String mostRecentFlowName;

    public StepperUIManager() {
        isLoaded = false;
        isFlowRan=false;
    }

    public void LoadStepperFromXmlFile(String xmlFilePath) throws FileNotFoundException, JAXBException {
        // First assigning it to a new Stepper object, to not override anything in case of failure.
        Stepper stepperCpy = new Stepper(xmlFilePath);

        // If we got here, no exceptions were thrown, thus the stepper was loaded successfully.
        // So now we can override the actual stepper:
        this.stepper = stepperCpy;
        isFlowRan = false;
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
        mostRecentFlowName = flowName;
        isFlowRan=true;
    }

    public Vector<FlowRunHistory> getFlowsRunHistories() {
        return stepper.getFlowsRunHistories();
    }

    public FlowLog getFlowLog(String flowName) {
        return stepper.getFlowLog(flowName);
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

    public boolean isFlowRan(){return  isFlowRan;}
    public boolean doesFlowHasContinuations(String flowName) {return stepper.doesFlowHaveContinuations(flowName);}
    public ArrayList<String> getFlowContinuationOptions(String flowName) {return stepper.getFlowContinuationOptions(flowName);}
    public void activateContinuation(String sourceFlowName, String targetFlowName) {activateContinuation(sourceFlowName, targetFlowName);}
    public HashMap<String, String> getFreeInputsCurrentValues(String flowName) {return stepper.getFreeInputsCurrentValues(flowName);}
    public int getMostRecentFlowTotalSteps() {
        return stepper.getFlowTotalNumberOfSteps(mostRecentFlowName);
    }
    public int getMostRecentFlowCompletedStepsCounter() {
        return stepper.getFlowNumberOfCompletedSteps(mostRecentFlowName);
    }
    public String getMostRecentFlowName() {
        return mostRecentFlowName;
    }
}
