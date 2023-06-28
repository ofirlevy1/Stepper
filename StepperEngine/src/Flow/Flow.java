package Flow;

import DataTypes.DataType;
import DataTypes.UserFriendly;
import Generated.*;
import RunHistory.FlowRunHistory;
import StepConnections.InputConnections;
import StepConnections.OutputConnections;
import Steps.Step;
import Steps.StepDescriptor;
import Steps.StepFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.*;


/*
 * formal outputs
 *
 * readonly - if ALL steps are readonly
 *
 * aliasing
 *
 * initial values & continuation - constant inputs that are known in advance - REQUIRED ONLY FROM 2ND ASSIGNMENT
 *
 * free inputs (mandatory & optional)
 *
 * customMap (source, target)
 *
 * FlowLog / FlowRunSummary - unique ID
 *
 *
 *
 * */

public class Flow {
    private FlowDefinition flowDefinition;
    String flowID = UUID.randomUUID().toString();
    private Flow.Status status;
    private String flowRunsummery;
    private  double durationAvgInMs = 0.0;
    private  long runTime=0;
    //private  int flowRunsCounter = 0; // OFIR  - this should be removed because this class represent a flow that runs once
    private FlowLog flowLog;
    private FlowRunHistory flowRunHistory;
    private ArrayList<Step> steps;
    private int completedStepsCounter;
    private HashMap<String, HashSet<DataType>> freeInputs;
    private HashMap<String, DataType> outputs;

    public enum Status {
        NOT_RUN_YET, RUNNING, SUCCESS, WARNING, FAILURE
    }

    public Flow(FlowDefinition flowDefinition)
    {
        this.flowID = UUID.randomUUID().toString();
        this.flowDefinition = flowDefinition;
        this.flowLog = new FlowLog(flowID);
        this.status = Status.NOT_RUN_YET;

        // Getting copies of the DYNAMIC data from the flowDefinition:
        this.steps = flowDefinition.getStepsArrayCopy();
        this.freeInputs = flowDefinition.getFreeInputs(steps);
    }

    public synchronized FlowRunHistory execute(){
        status = Status.RUNNING;
        //flowRunsCounter++; // OFIR  - this should be removed because this class represents a flow that runs once
        completedStepsCounter = 0;
        //clearAllStepsLogs(); // OFIR  - this should be removed because this class represents a flow that runs once
        if(!areAllMandatoryFreeInputsSet())
            throw new RuntimeException("An attempt was made to run the Flow while there are UNSET mandatory free inputs");
        Instant start=Instant.now();
        try {
            for (Step step : steps) {
                step.execute();
                completedStepsCounter++;
                if (step.getStatus() == Step.Status.Failure && step.isBlocking())
                    throw new RuntimeException("'" + step.getFinalName() + "' has failed while executing, and does not continue in case of failure, source: "+step.getSummaryLine());
                if(flowDefinition.getMap().getMappingsByStep(step.getFinalName())!=null) {
                    for (StepMap mapping : flowDefinition.getMap().getMappingsByStep(step.getFinalName()))
                        flowDefinition.getStepByFinalName(mapping.getTargetStepName(), "").setInputByName(step.getOutputs(mapping.getSourceDataName()).get(0),mapping.getTargetDataName());
                }
            }
            flowRunsummery="flow execution ended successfully";
            setFlowStatus();
        } catch (RuntimeException e) {
            status=Status.FAILURE;
            flowRunsummery=e.getMessage();
            createFlowHistory();
        }

        Instant finish=Instant.now();
        runTime= Duration.between(start,finish).toMillis();
        calculateAvgRunTime();
        createFlowLog();
        createFlowHistory();
        return flowRunHistory;
    }

    private void setFlowStatus(){
        if(steps.get(steps.size()-1).getStatus()== Step.Status.Failure){
            status=Status.FAILURE;
            return;
        }

        for(Step step:steps){
            if(step.getStatus()== Step.Status.Warning){
                status=Status.WARNING;
                return;
            }
        }
        status=Status.SUCCESS;
    }

    private void createFlowLog(){
        flowLog=new FlowLog(flowID);
        flowLog.setFlowName(flowDefinition.getName());
        flowLog.setStatus(status);
        for(String formalOutputName:flowDefinition.getFormalOutputsNames())
            flowLog.addFormalOutputsPresentation(flowDefinition.getOutputs().get(formalOutputName));
    }

    private void createFlowHistory(){
        try {
            flowRunHistory = new FlowRunHistory();
            flowRunHistory.setFlowId(flowID);
            flowRunHistory.setRunTime(runTime);
            flowRunHistory.setFlowName(flowDefinition.getName());
            flowRunHistory.setTimeStamp(flowLog.getTimeStamp());
            flowRunHistory.setStatus(status);
            for (String freeInputString : flowDefinition.getFreeInputs().keySet()) {
                for (DataType freeInput : getFreeInputs().get(freeInputString))
                    flowRunHistory.addFreeInput(freeInput);
            }
            for (Step step : steps)
                flowRunHistory.addStep(step);
            for (String outputString : getOutputs().keySet())
                flowRunHistory.addOutput(getOutputs().get(outputString));
            for (String freeInputName : getFreeInputs().keySet())
                for (DataType freeInput : getFreeInputs().get(freeInputName))
                    flowRunHistory.addFreeInputEnteredByUser(freeInput);
        }
        catch(Exception e) {
            System.out.println("Failure while creating FlowHistory: " + e.getMessage());
            System.out.println(e.getStackTrace());
        }
    }

    public FlowLog getFlowLog() {
        return flowLog;
    }

//    public FlowRunHistory getFlowRunHistory(){
//        return flowRunHistory;
//    }




    public boolean areAllMandatoryFreeInputsSet()
    {
        for(String freeInputName : getFreeInputs().keySet())
            for(DataType freeInput : getFreeInputs().get(freeInputName)) {
                if(!freeInput.isDataSet() && freeInput.isMandatory())
                    return false;
            }
        return true;
    }

    private void calculateAvgRunTime(){
        durationAvgInMs=durationAvgInMs+((runTime-durationAvgInMs)/flowRunsCounter);
    }

    public FlowStatistics getFlowStatistics(){
        return new FlowStatistics(flowRunsCounter, durationAvgInMs, name);
    }

    public void clearAllStepsDataMembers(){
        for(Step step:steps)
            step.clearDataMembers();
    }

    private void clearAllStepsLogs(){
        for(Step step:steps)
            step.clearLogs();
    }



    public DataType getDataTypeByEffectiveName(String effectiveName) {
        HashSet<DataType> result = new HashSet<>();
        if(outputs.containsKey(effectiveName))
            return outputs.get(effectiveName);
        if(freeInputs.containsKey(effectiveName))
            return freeInputs.get(effectiveName).iterator().next();
        throw new RuntimeException("Flow " + this.name + " does not have inputs or outputs called '" + effectiveName + "' ");
    }

    public HashMap<String, String> getFreeInputsCurrentValues() {
        HashMap<String, String> result = new HashMap<String, String>();

        // Using Iterator.next to get the value of the first dataType in the HashSet. It shouldn't matter which one
        // because all of them should have the same value at any given time.
        for(String effectiveName : freeInputs.keySet())
            result.put(effectiveName, freeInputs.get(effectiveName).iterator().next().getPresentableString());

        return result;
    }


    public int getCompletedStepsCounter() {
        return completedStepsCounter;
    }

    public Status getStatus() {
        return status;
    }


    public String getFlowID() {
        return flowID;
    }

    public void setFreeInput(String inputEffectiveName, String dataStr) {
        if(!freeInputs.keySet().contains(inputEffectiveName))
            throw new RuntimeException("input '" + inputEffectiveName + " was not found in the Flow's free inputs");
        for(DataType freeInput : freeInputs.get(inputEffectiveName)) {
            if(!(freeInput instanceof UserFriendly))
                throw new RuntimeException("An attempt was made to set a NONE user-friendly data type with a string");
            ((UserFriendly) freeInput).setData(dataStr);
        }
    }

    public String getName() {
        return flowDefinition.getName();
    }

    public boolean hasContinuations() {
        return flowDefinition.hasContinuations();
    }

    public ArrayList<String> getContinuationTargets() {
        return flowDefinition.getContinuationTargets();
    }

    public Continuation getContinuation(String targetFlow) {
        return flowDefinition.getContinuation(targetFlow);
    }

    public boolean isRunning() {return isRunning;}
}
