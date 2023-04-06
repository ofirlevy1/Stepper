import DataTypes.StepDataType;

import java.util.List;

public abstract class  Step {
    private List<StepDataType> inputs;
    private List<StepDataType> outputs;
    private String stepName;
    private String aliasName;
    private Boolean hasAlias;
    private Boolean isReadOnly;
    private int runTimeInMs;
    private int startUpCount;
    private List<Log> logs;
    private String summaryLine;


    public  void Execute(){}

    public List<StepDataType> getInputs() {
        return inputs;
    }

    public void setInputs(List<StepDataType> inputs) {
        this.inputs = inputs;
    }

    public void setOutputs(List<StepDataType> outputs) {
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

    public String getLogsAsString(){
        String logsString="";
        for(Log log : logs){
            logsString=logsString+log.toString()+"\n";
        }
        return  logsString;
    }

    public void addLog(String logString){
        this.logs.add(new Log(logString));
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

    public String getFinalName(){
        return hasAlias ?aliasName:stepName;
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
}
