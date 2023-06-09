package Steps;

import DataTypes.DataType;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class  Step {
    private String name;
    private String alias;
    private Boolean hasAlias;
    private Boolean isReadOnly; // a readonly step doesn't change anything in the system.
    protected   double durationAvgInMs = 0.0;
    protected   double runTimeInMs = 0;
    protected   int startUpCounter = 0;

    private ArrayList<StepLog> logs;
    private String summaryLine;
    // Not sure if this is the best way to do this. not all steps have outputs (but only one doesn't);
    protected ArrayList<DataType> outputs;

    private boolean isBlocking;

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
        isBlocking = true;
    }

    public  void execute(){
        startUpCounter++;
        Instant start=Instant.now();
        outerRunStepFlow();
        Instant finish=Instant.now();
        runTimeInMs= Duration.between(start,finish).toMillis();
        updateAverageRunTime();
        updateStaticTimers();
    }
    protected abstract void updateStaticTimers();

    protected abstract void outerRunStepFlow();

    protected abstract void runStepFlow() throws Exception;

    public abstract void setInputs(DataType ... inputs);

    public abstract void setInputByName(DataType input, String inputName);

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

    public  abstract ArrayList<DataType> getAllData();

    public boolean trySetDataAlias(String name, String alias) {
        List<DataType> matchedDataMembers = getDataMembersByName(name);
        if(matchedDataMembers.isEmpty())
            return false;
        matchedDataMembers.get(0).setAlias(alias);
        return true;
    }

    private List<DataType> getDataMembersByName(String name) {
        return getAllData().stream().filter(d -> d.getEffectiveName().equals(name)).collect(Collectors.toList());
    }

    public List<DataType> getSingleInput(String name){
        return getDataMembersByName(name);
    }

    public boolean containsDataMember(String name){
        return !getDataMembersByName(name).isEmpty();
    }

    public boolean checkIfDataMemberIsAssigned(String name){
        return  getDataMembersByName(name).get(0).isAssigned();
    }

    public boolean IsDataMemberIsInput(String name){
        return  getDataMembersByName(name).get(0).isInput();
    }

    public String getTypeOfDataMember(String name){
        return  getDataMembersByName(name).get(0).getType().toString();
    }

    public List<DataType> getAllOutputs() {
        return getAllData().stream().filter(d -> !d.isInput()).collect(Collectors.toList());
    }

    public boolean tryAssignDataTypeByName(String name) {
        List<DataType> matchedDataMembers = getDataMembersByName(name);
        if(matchedDataMembers.isEmpty())
            return false;
        matchedDataMembers.get(0).setAssigned(true);
        return true;
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

    public boolean isBlocking() {
        return isBlocking;
    }

    public void setBlocking(boolean blocking) {
        isBlocking = blocking;
    }

    public StepDescriptor getStepDescriptor() {
        return new StepDescriptor(name, alias, hasAlias, isReadOnly);
    }

    private void updateAverageRunTime(){
        durationAvgInMs=durationAvgInMs+((runTimeInMs-durationAvgInMs)/ startUpCounter);
    }

    public double getRunTimeInMs() {
        return runTimeInMs;
    }

    public abstract void clearDataMembers();

    public void clearLogs(){
        logs=new ArrayList<>();
    }
}
