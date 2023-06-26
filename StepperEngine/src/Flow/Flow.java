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

    private Flow.Status status;
    private String flowRunsummery;
    private  double durationAvgInMs = 0.0;
    private  long runTime=0;
    //private  int flowRunsCounter = 0; // OFIR  - this should be removed because this class represent a flow that runs once
    private FlowLog flowLog;
    private FlowRunHistory flowRunHistory;
    private ArrayList<Step> steps;
    private int completedStepsCounter;

    public enum Status {
        NOT_RUN_YET, RUNNING, SUCCESS, WARNING, FAILURE
    }

    public Flow(FlowDefinition flowDefinition)
    {
        this.flowDefinition = flowDefinition;
        this.flowLog = new FlowLog();
        this.status = Status.NOT_RUN_YET;
        this.steps = flowDefinition.getStepsArrayCopy();
    }

    public synchronized FlowRunHistory execute(){
        status = Status.RUNNING;
        //flowRunsCounter++; // OFIR  - this should be removed because this class represent a flow that runs once
        completedStepsCounter = 0;
        //clearAllStepsLogs(); // OFIR  - this should be removed because this class represent a flow that runs once
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
        flowLog=new FlowLog();
        flowLog.setFlowName(flowDefinition.getName());
        flowLog.setStatus(status);
        for(String formalOutputName:flowDefinition.getFormalOutputsNames())
            flowLog.addFormalOutputsPresentation(flowDefinition.getOutputs().get(formalOutputName));
    }

    private void createFlowHistory(){
        try {
            flowRunHistory = new FlowRunHistory();
            flowRunHistory.setFlowId(flowLog.getFlowId());
            flowRunHistory.setRunTime(runTime);
            flowRunHistory.setFlowName(flowDefinition.getName());
            flowRunHistory.setTimeStamp(flowLog.getTimeStamp());
            flowRunHistory.setStatus(status);
            for (String freeInputString : flowDefinition.getFreeInputs().keySet()) {
                for (DataType freeInput : flowDefinition.getFreeInputs().get(freeInputString))
                    flowRunHistory.addFreeInput(freeInput);
            }
            for (Step step : steps)
                flowRunHistory.addStep(step);
            for (String outputString : flowDefinition.getOutputs().keySet())
                flowRunHistory.addOutput(flowDefinition.getOutputs().get(outputString));
            for (String freeInputName : flowDefinition.getFreeInputs().keySet())
                for (DataType freeInput : flowDefinition.getFreeInputs().get(freeInputName))
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

    public FlowDescriptor getFlowDescriptor() {
        FlowDescriptor descriptor = new FlowDescriptor();
        descriptor.setFlowName(flowDefinition.getName());
        descriptor.setFlowDescription(flowDefinition.getDescription());
        descriptor.setFormalOutputNames((HashSet<String>) flowDefinition.getFormalOutputsNames().clone());
        descriptor.setReadonly(flowDefinition.isReadOnly());
        descriptor.setStepDescriptors(getStepDescriptors());
        descriptor.setFreeInputs(flowDefinition.getFreeInputsDescriptors());
        descriptor.setOutputs(flowDefinition.getOutputDescriptors());
        descriptor.setNumberOfContinuations(flowDefinition.getContinuations().size());
        return descriptor;
    }



    private ArrayList<StepDescriptor> getStepDescriptors() {
        ArrayList<StepDescriptor> descriptors = new ArrayList<>();
        for(Step step : steps) {
            StepDescriptor stepDescriptor=step.getStepDescriptor();
            if(flowDefinition.getMap().getMappingsByStep(step.getName())!=null) {
                for (StepMap stepMap : flowDefinition.getMap().getMappingsByStep(step.getName())) {
                    stepDescriptor.addOutputConnections(new OutputConnections(stepMap.getSourceDataName(), step.getOutputs(stepMap.getSourceDataName()).get(0).hasAlias(),step.getOutputs(stepMap.getSourceDataName()).get(0).getName(), stepMap.getTargetDataName(), stepMap.getTargetStepName()));
                }
            }
            if(flowDefinition.getMap().getInputMappingsByStep(step.getName())!=null) {
                for (StepMap stepMap : flowDefinition.getMap().getInputMappingsByStep(step.getName())) {
                    stepDescriptor.addInputConnections(new InputConnections(stepMap.getTargetDataName(), step.getSingleInput(stepMap.getTargetDataName()).get(0).hasAlias(), step.getSingleInput(stepMap.getTargetDataName()).get(0).getName(),stepMap.getSourceDataName(), stepMap.getSourceStepName()));
                }
            }

            stepDescriptor.addUnconnectedDataMembers(step.getAllData());
            descriptors.add(stepDescriptor);
        }

        return descriptors;
    }





    public boolean areAllMandatoryFreeInputsSet()
    {
        for(String freeInputName : flowDefinition.getFreeInputs().keySet())
            for(DataType freeInput : flowDefinition.getFreeInputs().get(freeInputName)) {
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




    public int getTotalNumberOfSteps() {
        return steps.size();
    }

    public int getCompletedStepsCounter() {
        return completedStepsCounter;
    }

    public Status getStatus() {
        return status;
    }



    public boolean isFreeInput(String name) {
        return freeInputs.containsKey(name);
    }

    public boolean isRunning() {return isRunning;}
}
