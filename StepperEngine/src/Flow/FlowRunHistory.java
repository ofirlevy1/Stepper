package Flow;

import DataTypes.DataType;
import Steps.Step;

public class FlowRunHistory {
    private String flowId;
    private String flowName;
    private Flow.Status status;
    private double runTime;
    private String timeStamp;

    private String freeInputsHistory;
    private String outputsHistory;
    private String stepsHistory;

    public FlowRunHistory(){
        freeInputsHistory="";
        outputsHistory="";
        stepsHistory="";
    }

    public String showMinimalFlowHistory(){
        return "Flow name: "+flowName+" Flow ID: "+flowId+" Time Stamp: "+timeStamp;
    }

    public String showExtensiveFlowHistory(){
        String flowHistory="Flow ID: "+flowId+" Name: "+flowName+" Status: "+status+" Run Time: "+ runTime+"\n";
        return flowHistory+"FREE INPUTS\n"+freeInputsHistory+"OUTPUTS\n"+outputsHistory+"STEPS\n"+stepsHistory;
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
        String freeInputHistory="Name: "+freeInput.getEffectiveName()+" Type: "+freeInput.getType()+" content:\n"+freeInput.getPresentableString()+"\nthe input is: "+(freeInput.isMandatory()?"mandatory":"optional")+"\n";
        //the mandatory inputs are presented first
        if(freeInput.isMandatory())
            freeInputsHistory=freeInputHistory+freeInputsHistory;//put at top
        else
            freeInputsHistory=freeInputsHistory+freeInputHistory;//put at bottom
    }

    public void addOutput(DataType output){
        outputsHistory+="Name: "+output.getEffectiveName()+" Type: "+output.getType()+" Content:\n"+output.getPresentableString()+"\n";
    }

    public void addStep(Step step){
        stepsHistory+="Name "+step.getFinalName()+" Step run time: "+step.getRunTimeInMs()+" Status: "+step.getStatus().toString()+"\n";
        stepsHistory+="Summery: "+step.getSummaryLine()+"\n"+"Logs:\n"+step.getLogsAsString()+"\n";
    }
}
