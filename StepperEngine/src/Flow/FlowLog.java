package Flow;

import DataTypes.DataType;
import Steps.Step;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class FlowLog {
    private String flowId;
    private String flowName;
    private Flow.Status status;
    private String formalOutputsPresentation;
    private String timeStamp;


    FlowLog(){
        flowId= UUID.randomUUID().toString();
        timeStamp= new SimpleDateFormat("HH.mm.ss").format(new Date());
        formalOutputsPresentation="";
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

    public String getFlowId() {
        return flowId;
    }

    public String getFormalOutputsPresentation() {
        return formalOutputsPresentation;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void addFormalOutputsPresentation(DataType output){
        formalOutputsPresentation+="Formal Output name: "+output.getUserFriendlyName()+":\n"+output.getPresentableString()+"\n\n";
    }

    public String getFlowLogAsString(){
        return "Flow ID: "+flowId+" Flow name:"+flowName+" Flow status: "+status+"\nFormal outputs:\n"+formalOutputsPresentation;
    }
}
