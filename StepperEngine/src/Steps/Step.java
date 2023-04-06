package Steps;

import DataTypes.StepDataType;


import java.util.ArrayList;
import java.util.List;

public abstract class  Step {
    protected ArrayList<StepDataType> inputs;
    protected ArrayList<StepDataType> outputs;
    private String stepName;
    private String aliasName;
    private Boolean hasAlias;
    private Boolean isReadOnly;
    private int runTimeInMs;
    private int startUpCount;
    private ArrayList<StepLog> logs;
    private String summaryLine;
    private StepStatus status;

    public Step(String stepName, Boolean isReadOnly, List<StepDataType> inputs, List<StepDataType> outputs){
        this.stepName=stepName;
        this.isReadOnly=isReadOnly;
        this.inputs=new ArrayList<>();
        this.outputs=new ArrayList<>();
        this.logs=new ArrayList<>();
        this.inputs.addAll(inputs);
        if(outputs!=null)
            this.outputs.addAll(outputs);
        this.hasAlias=false;
        this.status=StepStatus.NotRunYet;
    }

    public abstract void execute();

    public String getLogsAsString(){
        String logsString="";
        for(StepLog log : logs){
            logsString=logsString+log.toString()+"\n";
        }
        return  logsString;
    }

    public void addLog(String logString){
        this.logs.add(new StepLog(logString));
    }

    public String getFinalName(){
        return hasAlias ?aliasName:stepName;
    }

    //Methods that were made automatically, might be deleted later

    public List<StepDataType> getInputs() {
        return inputs;
    }

    public void setInputs(ArrayList<StepDataType> inputs) {
        this.inputs = inputs;
    }

    public void setOutputs(ArrayList<StepDataType> outputs) {
        this.outputs = outputs;
    }

    public List<StepDataType> getOutputs() {
        return outputs;
    }

    public String getSummaryLine() {
        return summaryLine;
    }

    public void setSummaryLine(String summaryLine) {
        this.summaryLine = summaryLine;
    }

    public Boolean getReadOnly() {
        return isReadOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        isReadOnly = readOnly;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
        this.hasAlias =true;
    }

    public int getRunTimeInMs() {
        return runTimeInMs;
    }

    public void setRunTimeInMs(int runTimeInMs) {
        this.runTimeInMs = runTimeInMs;
    }

    public int getStartUpCount() {
        return startUpCount;
    }

    public void setStartUpCount(int startUpCount) {
        this.startUpCount = startUpCount;
    }

    public Boolean getHasAlias() {
        return hasAlias;
    }

    public void setHasAlias(Boolean hasAlias) {
        this.hasAlias = hasAlias;
    }

    public StepStatus getStatus() {
        return status;
    }

    public void setStatus(StepStatus status) {
        this.status = status;
    }
}
