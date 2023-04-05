import java.util.List;

public abstract class  Step {
    private String stepName;
    private String aliasName;
    private Boolean hasAlias;
    private Boolean isReadOnly;
    private int runTimeInMs;
    private int startUpCount;
    private List<Log> logs;
    private String summaryLine;


    public  void Execute(){}

    public String getLogs(){
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

    public Boolean getIsReadOnly() {
        return isReadOnly;
    }

    public void setIsReadOnly(Boolean readOnly) {
        isReadOnly = readOnly;
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
