package Flow;

import Steps.StepDescriptor;

import java.util.ArrayList;
import java.util.HashSet;

public class FlowDescriptor {
    private String flowName;
    private String flowDescription;
    private HashSet<String> formalOutputNames; //maybe not needed? can be extracted from "outputs" member
    private boolean isReadonly;
    private ArrayList<StepDescriptor> stepDescriptors;
    private ArrayList<FreeInputDescriptor> freeInputs;
    private ArrayList<StepOutputDescriptor> outputs;

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public String getFlowDescription() {
        return flowDescription;
    }

    public void setFlowDescription(String flowDescription) {
        this.flowDescription = flowDescription;
    }

    public HashSet<String> getFormalOutputNames() {
        return formalOutputNames;
    }

    public void setFormalOutputNames(HashSet<String> formalOutputNames) {
        this.formalOutputNames = formalOutputNames;
    }

    public boolean isReadonly() {
        return isReadonly;
    }

    public void setReadonly(boolean readonly) {
        isReadonly = readonly;
    }

    public ArrayList<StepDescriptor> getStepDescriptors() {
        return stepDescriptors;
    }

    public void setStepDescriptors(ArrayList<StepDescriptor> stepDescriptors) {
        this.stepDescriptors = stepDescriptors;
    }

    public ArrayList<FreeInputDescriptor> getFreeInputs() {
        return freeInputs;
    }

    public void setFreeInputs(ArrayList<FreeInputDescriptor> freeInputs) {
        this.freeInputs = freeInputs;
    }

    public ArrayList<StepOutputDescriptor> getOutputs() {
        return outputs;
    }

    public void setOutputs(ArrayList<StepOutputDescriptor> outputs) {
        this.outputs = outputs;
    }
}
