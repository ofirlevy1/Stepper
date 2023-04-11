package Steps;

import DataTypes.DataType;
import java.util.ArrayList;
import java.util.List;

public abstract class  Step {
    protected ArrayList<DataType> inputs;
    protected ArrayList<DataType> outputs;
    private String name;
    private String alias;
    private Boolean hasAlias;
    private Boolean isReadOnly; // a readonly step doesn't change anything in the system.
    private int runTimeInMs;
    private int startUpCount;
    private ArrayList<StepLog> logs;
    private String summaryLine;

    public enum Status {
        Success,
        Warning,
        Failure,
        NotRunYet
    }
    private Status status;

    public Step(String stepName, Boolean isReadOnly, List<DataType> inputs, List<DataType> outputs){
        this.name =stepName;
        this.isReadOnly=isReadOnly;
        this.inputs=new ArrayList<>();
        this.outputs=new ArrayList<>();
        this.logs=new ArrayList<>();
        this.inputs.addAll(inputs);
        if(outputs!=null)
            this.outputs.addAll(outputs);
        this.hasAlias=false;
        this.status=Status.NotRunYet;
    }

    public abstract void execute();

    protected abstract void runStepFlow() throws Exception;

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
        return hasAlias ? alias : name;
    }

    //Methods that were made automatically, might be deleted later

    public List<DataType> getInputs() {
        return inputs;
    }

    public void setInputs(ArrayList<DataType> inputs) {
        this.inputs = inputs;
    }

    public void setOutputs(ArrayList<DataType> outputs) {
        this.outputs = outputs;
    }

    public List<DataType> getOutputs() {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getEffectiveName() {
        return hasAlias ? alias : name;
    }
}
