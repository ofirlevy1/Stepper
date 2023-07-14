package RunHistory;

import DataTypes.DataType;
import Flow.Flow;
import Steps.Step;

import java.util.ArrayList;
import java.util.HashMap;

public class FlowRunHistory {
    private String flowId;
    private String flowName;
    private String owner;
    private Flow.Status status;
    private double runTime;
    private String timeStamp;
    private ArrayList<FreeInputHistory> freeInputHistories;
    private ArrayList<OutputHistory> outputHistories;
    private ArrayList<StepHistory> stepHistories;
    private HashMap<String,String> freeInputsEnteredByUser;


    public FlowRunHistory(){
        freeInputHistories=new ArrayList<>();
        outputHistories=new ArrayList<>();
        stepHistories=new ArrayList<>();
        freeInputsEnteredByUser=new HashMap<>();
    }

    public String showGUIFlowHistory(){
        return "Flow name: "+flowName+"\nFlow ID: "+flowId+"\nTime Stamp: "+timeStamp+"\nRun Time: "+runTime+"\nStatus: "+status;
    }

    public String showMinimalFlowHistory(){
        return "Flow name: "+flowName+" Flow ID: "+flowId+" Time Stamp: "+timeStamp;
    }

    public String showExtensiveFlowHistory(){
        String flowHistory="Flow ID: "+flowId+" Name: "+flowName+" Status: "+status+" Run Time: "+ runTime;
        return flowHistory+"\nFREE INPUTS\n"+freeInputsHistories()+"OUTPUTS\n"+outputsHistories()+"STEPS\n"+stepsHistories();
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public Flow.Status getStatus() {
        return status;
    }

    public void setStatus(Flow.Status status) {
        this.status = status;
    }

    public double getRunTime() {
        return runTime;
    }

    public void setRunTime(double runTime) {
        this.runTime = runTime;
    }

    public void addFreeInput(DataType freeInput){
        if(freeInput.isMandatory())
            freeInputHistories.add(0,new FreeInputHistory(freeInput.getName(), freeInput.getAlias(),freeInput.getType().toString(), freeInput.getPresentableString(), freeInput.isMandatory()));
        else
            freeInputHistories.add(new FreeInputHistory(freeInput.getName(), freeInput.getAlias(), freeInput.getType().toString(), freeInput.getPresentableString(), freeInput.isMandatory()));
    }

    //used for console presentation
    private String freeInputsHistories(){
        int i=1;
        String consolePresentationFreeInput="------------------------------------------------------------------------\n";
        for(FreeInputHistory freeInputHistory:freeInputHistories){
            consolePresentationFreeInput+=i+".NAME: "+freeInputHistory.getName()+" TYPE: "+freeInputHistory.getType()+" CONTENT:\n"+freeInputHistory.getPresentableString()+"\ntTHE INPUT IS: "+(freeInputHistory.getMandatory()?"MANDATORY":"OPTIONAL")+"\n";
            consolePresentationFreeInput+="------------------------------------------------------------------------\n";
            i++;
        }
        return  consolePresentationFreeInput;
    }

    public void addOutput(DataType output){
        outputHistories.add(new OutputHistory(output.getName(), output.getAlias(), output.getType().toString(), output.getPresentableString()));
    }

    //used for console presentation
    private String outputsHistories(){
        int i=1;
        String consolePresentationOutputs="------------------------------------------------------------------------\n";
        for(OutputHistory outputHistory:outputHistories){
            consolePresentationOutputs+=i+".Name: "+outputHistory.getName()+" Type: "+outputHistory.getType()+" Content:\n"+outputHistory.getPresentableString()+"\n";
            consolePresentationOutputs+="------------------------------------------------------------------------\n";
            i++;
        }
        return consolePresentationOutputs;
    }

    public void addStep(Step step){
        stepHistories.add(new StepHistory(step.getName(), step.getRunTimeInMs(), step.getStatus(), step.getSummaryLine(), step.getLogsAsString(),step.getAllData()));

    }

    //used for console presentation
    private String stepsHistories(){
        int i=1;
        String consolePresentationSteps="------------------------------------------------------------------------\n";
        for(StepHistory stepHistory:stepHistories){
            consolePresentationSteps+=i+".Name "+stepHistory.getName()+" Step run time: "+stepHistory.getRunTimeInMs()+" Status: "+stepHistory.getStatus().toString()+"\n";
            consolePresentationSteps+="Summary: "+stepHistory.getSummery()+"\n"+"Logs:\n"+stepHistory.getLogs()+"\n";
            consolePresentationSteps+="------------------------------------------------------------------------\n";
            i++;
        }
        return  consolePresentationSteps;
    }

    public ArrayList<FreeInputHistory> getFreeInputHistories() {
        return freeInputHistories;
    }

    public void setFreeInputHistories(ArrayList<FreeInputHistory> freeInputHistories) {
        this.freeInputHistories = freeInputHistories;
    }

    public ArrayList<OutputHistory> getOutputHistories() {
        return outputHistories;
    }

    public void setOutputHistories(ArrayList<OutputHistory> outputHistories) {
        this.outputHistories = outputHistories;
    }

    public ArrayList<StepHistory> getStepHistories() {
        return stepHistories;
    }

    public void setStepHistories(ArrayList<StepHistory> stepHistories) {
        this.stepHistories = stepHistories;
    }

    public void addFreeInputEnteredByUser(DataType freeInput){
        if(freeInput.getData()!=null)
            freeInputsEnteredByUser.putIfAbsent(freeInput.getEffectiveName(),freeInput.getData().toString());
    }

    public HashMap<String ,String> getFreeInputsEnteredByUser() {
        return freeInputsEnteredByUser;
    }

    public void setFreeInputsEnteredByUser(HashMap<String,String> freeInputsEnteredByUser) {
        this.freeInputsEnteredByUser = freeInputsEnteredByUser;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
