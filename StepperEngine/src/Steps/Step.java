package Steps;

import DataTypes.DataType;
import java.util.ArrayList;

public abstract class  Step {
    private String name;
    private String alias;
    private Boolean hasAlias;
    private Boolean isReadOnly; // a readonly step doesn't change anything in the system.
    private static double durationAvgInMs = 0.0;
    private static int stepRunsCounter = 0;

    private ArrayList<StepLog> logs;
    private String summaryLine;
    // Not sure if this is the best way to do this. not all steps have outputs (but only one doesn't);
    protected ArrayList<DataType> outputs;

    public enum Status {
        Success,
        Warning,
        Failure,
        NotRunYet
    }
    private Status status;

    public Step(String stepName, Boolean isReadOnly){
        this.name =stepName;
        this.isReadOnly=isReadOnly;
        this.logs=new ArrayList<>();
        this.hasAlias=false;
        this.status=Status.NotRunYet;
        outputs = new ArrayList<DataType>();
    }

    public abstract void execute();

    protected abstract void runStepFlow() throws Exception;

    public abstract void setInputs(DataType ... inputs);

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

    public abstract ArrayList<DataType> getOutputs(String... outputNames);

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

    protected void setStatusAndLog(Status status, String summaryLine, String log) {
        this.status = status;
        setSummaryLine(summaryLine);
        addLog(log);
    }
}
