package Steps;

import Flow.StepMap;
import StepConnections.InputConnections;
import StepConnections.OutputConnections;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class StepDescriptor {
    private String stepName;
    private String stepAlias;
    private boolean hasAlias;
    private boolean isReadOnly;
    private ArrayList<InputConnections> inputConnections;
    private HashMap<String,OutputConnections> outputConnections;

    public StepDescriptor(String stepName, String stepAlias, boolean hasAlias, boolean isReadOnly) {
        this.stepName = stepName;
        this.stepAlias = stepAlias;
        this.hasAlias = hasAlias;
        this.isReadOnly = isReadOnly;
        inputConnections=new ArrayList<>();
        outputConnections=new HashMap<>();
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public String getStepAlias() {
        return stepAlias;
    }

    public void setStepAlias(String stepAlias) {
        this.stepAlias = stepAlias;
    }

    public boolean isHasAlias() {
        return hasAlias;
    }

    public void setHasAlias(boolean hasAlias) {
        this.hasAlias = hasAlias;
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    public void setReadOnly(boolean readOnly) {
        isReadOnly = readOnly;
    }

    public ArrayList<InputConnections> getInputConnections() {
        return inputConnections;
    }

    public void addInputConnections(InputConnections inputConnections) {
        this.inputConnections.add(inputConnections);
    }

    public HashMap<String,OutputConnections> getOutputConnections() {
        return outputConnections;
    }

    public void addOutputConnections(OutputConnections outputConnections) {
        if(!this.outputConnections.containsKey(outputConnections.getOutputName())){
            this.outputConnections.put(outputConnections.getOutputName(),outputConnections);
            return;
        }
        this.outputConnections.get(outputConnections.getOutputName()).addConnection(outputConnections.getConnectedInputsName().get(0),outputConnections.getConnectedStepsName().get(0));
    }
}
