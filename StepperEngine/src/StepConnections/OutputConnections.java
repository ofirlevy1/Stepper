package StepConnections;

import java.util.ArrayList;

public class OutputConnections {

    private String outputAliasName;
    private boolean hasAlias;
    private String outputName;
    private ArrayList<String> connectedInputsName;
    private ArrayList<String> connectedStepsName;

    public OutputConnections(String outputAliasName, boolean hasAlias, String outputName, String connectedInputsName, String connectedStepsName) {
        this.outputAliasName = outputAliasName;
        this.hasAlias=hasAlias;
        this.outputName=outputName;
        this.connectedInputsName = new ArrayList<>();
        this.connectedInputsName.add(connectedInputsName);
        this.connectedStepsName = new ArrayList<>();
        this.connectedStepsName.add(connectedStepsName);
    }

    public String getOutputAliasName() {
        return outputAliasName;
    }

    public ArrayList<String> getConnectedInputsName() {
        return connectedInputsName;
    }

    public ArrayList<String> getConnectedStepsName() {
        return connectedStepsName;
    }

    public void setOutputAliasName(String outputAliasName) {
        this.outputAliasName = outputAliasName;
    }

    public void setConnectedInputsName(ArrayList<String> connectedInputsName) {
        this.connectedInputsName = connectedInputsName;
    }

    public void setConnectedStepsName(ArrayList<String> connectedStepsName) {
        this.connectedStepsName = connectedStepsName;
    }

    public boolean isHasAlias() {
        return hasAlias;
    }

    public void setHasAlias(boolean hasAlias) {
        this.hasAlias = hasAlias;
    }

    public String getOutputName() {
        return outputName;
    }

    public void setOutputName(String outputName) {
        this.outputName = outputName;
    }

    public void addConnection(String connectedInputName, String connectedStepsName){
        this.connectedInputsName.add(connectedInputName);
        this.connectedStepsName.add(connectedStepsName);
    }
}
