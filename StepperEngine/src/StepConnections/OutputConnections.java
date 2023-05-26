package StepConnections;

import java.util.ArrayList;

public class OutputConnections {

    private String outputName;
    private ArrayList<String> connectedInputsName;
    private ArrayList<String> connectedStepsName;

    public OutputConnections(String outputName, String connectedInputsName, String connectedStepsName) {
        this.outputName = outputName;
        this.connectedInputsName = new ArrayList<>();
        this.connectedInputsName.add(connectedInputsName);
        this.connectedStepsName = new ArrayList<>();
        this.connectedStepsName.add(connectedStepsName);
    }

    public String getOutputName() {
        return outputName;
    }

    public ArrayList<String> getConnectedInputsName() {
        return connectedInputsName;
    }

    public ArrayList<String> getConnectedStepsName() {
        return connectedStepsName;
    }

    public void setOutputName(String outputName) {
        this.outputName = outputName;
    }

    public void setConnectedInputsName(ArrayList<String> connectedInputsName) {
        this.connectedInputsName = connectedInputsName;
    }

    public void setConnectedStepsName(ArrayList<String> connectedStepsName) {
        this.connectedStepsName = connectedStepsName;
    }

    public void addConnection(String connectedInputName, String connectedStepsName){
        this.connectedInputsName.add(connectedInputName);
        this.connectedStepsName.add(connectedStepsName);
    }
}
