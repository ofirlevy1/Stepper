package Steps;

import DataTypes.DataType;
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
    private HashMap<String,InputConnections> inputConnections;
    private HashMap<String,OutputConnections> outputConnections;

    public StepDescriptor(String stepName, String stepAlias, boolean hasAlias, boolean isReadOnly) {
        this.stepName = stepName;
        this.stepAlias = stepAlias;
        this.hasAlias = hasAlias;
        this.isReadOnly = isReadOnly;
        inputConnections=new HashMap<>();
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

    public String getStepEffectiveName(){
        if(hasAlias)
            return stepAlias;
        else
            return stepName;
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

    public HashMap<String,InputConnections> getInputConnections() {
        return inputConnections;
    }

    public void addInputConnections(InputConnections inputConnections) {
        this.inputConnections.put(inputConnections.getInputName(),inputConnections);
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

    public void addUnconnectedDataMembers(ArrayList<DataType> dataMembers){
        for(DataType dataType:dataMembers){
            if(dataType.isInput() && !inputConnections.containsKey(dataType.getEffectiveName())){
                inputConnections.put(dataType.getEffectiveName(),new InputConnections(dataType.getEffectiveName(),dataType.isMandatory()?"Mandatory":"Optional","free input"));
            }
            if(!dataType.isInput() && !outputConnections.containsKey(dataType.getEffectiveName())){
                outputConnections.put(dataType.getEffectiveName(),new OutputConnections(dataType.getEffectiveName(),"Formal output",""));
            }
        }
    }
}
