package RunHistory;

import DataTypes.DataType;
import Steps.Step;

import java.util.ArrayList;

public class StepHistory {
    private String name;
    private double runTimeInMs;
    private Step.Status status;
    private String summery;
    private String logs;
    private ArrayList<FreeInputHistory> inputs;
    private ArrayList<OutputHistory> outputs;

    public StepHistory(String name, double runTimeInMs, Step.Status status, String summery, String logs, ArrayList<DataType> allDataMembers) {
        this.name = name;
        this.runTimeInMs = runTimeInMs;
        this.status = status;
        this.summery = summery;
        this.logs = logs;
        this.inputs=new ArrayList<>();
        this.outputs=new ArrayList<>();
        addDataMembers(allDataMembers);
    }

    private void addDataMembers(ArrayList<DataType> allDataMembers){
        for(DataType dataType: allDataMembers){
            if(dataType.isInput())
                inputs.add(new FreeInputHistory(dataType.getEffectiveName(), dataType.getAlias(), dataType.getType().toString(), dataType.getPresentableString(), dataType.isMandatory()));
            else
                outputs.add(new OutputHistory(dataType.getName(), dataType.getAlias(), dataType.getType().toString(), dataType.getPresentableString()));
        }

    }

    public ArrayList<OutputHistory> getOutputs() {
        return outputs;
    }

    public ArrayList<FreeInputHistory> getInputs() {
        return inputs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRunTimeInMs() {
        return runTimeInMs;
    }

    public void setRunTimeInMs(double runTimeInMs) {
        this.runTimeInMs = runTimeInMs;
    }

    public Step.Status getStatus() {
        return status;
    }

    public void setStatus(Step.Status status) {
        this.status = status;
    }

    public String getSummery() {
        return summery;
    }

    public void setSummery(String summery) {
        this.summery = summery;
    }

    public String getLogs() {
        return logs;
    }

    public void setLogs(String logs) {
        this.logs = logs;
    }
}
