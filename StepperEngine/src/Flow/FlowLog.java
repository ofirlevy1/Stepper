package Flow;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class FlowLog {
    private String flowId;
    private String logs;
    private String flowName;
    private Flow.Status status;
    private String formalOutputsPresentation;
    private HashMap<String,String> formalOutputs;
    private String timeStamp;
    private long totalRuntimeInMs;
    private ArrayList<String> inputsPresentation;
    private ArrayList<String> outputsPresentation;

    FlowLog(){
        flowId= UUID.randomUUID().toString();
        formalOutputs=new HashMap<>();
        logs="";
        timeStamp= new SimpleDateFormat("HH.mm.ss").format(new Date());
        inputsPresentation =new ArrayList<>();
        outputsPresentation=new ArrayList<>();
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

    public void addStepLogs(String stepLogs){
        this.logs+=stepLogs;
    }

    public String getLogs() {
        return logs;
    }

    public String getFormalOutputsPresentation() {
        return formalOutputsPresentation;
    }

    public long getTotalRuntimeInMs() {
        return totalRuntimeInMs;
    }

    public void setTotalRuntimeInMs(long totalRuntimeInMs) {
        this.totalRuntimeInMs = totalRuntimeInMs;
    }
}
