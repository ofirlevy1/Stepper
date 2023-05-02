package Flow;

import DataTypes.DataType;
import DataTypes.UserFriendly;
import Generated.*;
import RunHistory.FlowRunHistory;
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
    private ArrayList<Step> steps;
    private String name;
    private String description;
    private HashMap<String, DataType> outputs;
    private HashSet<String> formalOutputsNames;
    private boolean isReadOnly;
    private HashMap<String, HashSet<DataType>> freeInputs;
    private Flow.Status status;
    private String flowRunsummery;
    private  double durationAvgInMs = 0.0;
    private  long runTime=0;
    private  int flowRunsCounter = 0;
    private FlowLog flowLog;
    private FlowRunHistory flowRunHistory;

    private FlowMap map;

    private ArrayList<FreeInputDescriptor> freeInputsDescriptors;
    private ArrayList<StepOutputDescriptor> outputDescriptors;


    public Flow(STFlow flow)
    {
        this();
        name = flow.getName();
        description = flow.getSTFlowDescription();
        formalOutputsNames = new HashSet<>(Arrays.asList(flow.getSTFlowOutput().split(",")));
        freeInputsDescriptors = new ArrayList<FreeInputDescriptor>();
        outputDescriptors = new ArrayList<StepOutputDescriptor>();

        loadSteps(flow.getSTStepsInFlow());
        fillOutputsDescriptorsArray();
        setFlowLevelAliases(flow.getSTFlowLevelAliasing());
        setFlowMap(flow.getSTCustomMappings());


        findFreeInputs();
        formalOutputsValidation();
        setIsReadOnly();

    }

    private Flow() {
        this.steps = new ArrayList<>();
        this.outputs = new HashMap<>();
        this.freeInputs = new HashMap<>();
        this.map = new FlowMap();
        this.flowLog=new FlowLog();
    }

    // Input validation to do:
    // Non-existent steps/data types
    // outputs with the same name
    private void setFlowLevelAliases(STFlowLevelAliasing aliases) {
        if(aliases!=null&&aliases.getSTFlowLevelAlias()!=null) {
            for (STFlowLevelAlias alias : aliases.getSTFlowLevelAlias()) {
                Step current = getStepByFinalName(alias.getStep(), "flow level aliasing");
                if (!current.trySetDataAlias(alias.getSourceDataName(), alias.getAlias()))
                    throw new RuntimeException("In flow level aliasing:" + alias.getSourceDataName() + " does not exist");
            }
        }

        setFlowLevelAliasesValidation();
    }

    //checking if there are outputs with the same effective name
    private void setFlowLevelAliasesValidation(){
        HashSet<String> outputsSet= new HashSet<>();
        for(Step step:steps) {
            List<DataType> dataMembers = step.getAllData();
            for(DataType dataMember:dataMembers) {
                if(!dataMember.isInput())
                    if (!outputsSet.add(dataMember.getEffectiveName()))
                        throw new RuntimeException("In flow level aliasing: there more than one output named:"+dataMember.getEffectiveName()+" after aliasing");
            }
        }
    }

    private Step getStepByFinalName(String name, String exceptionString) {
        for(Step step : steps) {
            if(step.getFinalName().equals(name)) {
                return step;
            }
        }
        throw new RuntimeException("In "+exceptionString+": " + name + " does not exist");
    }

    // Input validation to do:
    // non existent steps
    private void loadSteps(STStepsInFlow stSteps) {
        if(stSteps==null||stSteps.getSTStepInFlow()==null)throw new RuntimeException("No steps provided");
        for(STStepInFlow stStep : stSteps.getSTStepInFlow()) {
            Step current = StepFactory.createStep(stStep.getName());
            if(current==null) throw  new RuntimeException("In step creation:"+stStep.getName()+" does not exist");
            if(stStep.getAlias() != null)
                current.setAlias(stStep.getAlias());
            if(stStep.isContinueIfFailing() != null)
                current.setBlocking(!stStep.isContinueIfFailing());
            steps.add(current);
        }
        //checking if there are steps with the same final name
        HashSet<String> stepsSet= new HashSet<>();
        for(Step step:steps)
            if(!stepsSet.add(step.getFinalName()))throw new RuntimeException("In step creation: there more than one step named:"+step.getFinalName()+" after aliasing");
    }


    private void setIsReadOnly() {
        for(Step step : steps) {
            if(!step.getReadOnly()) {
                isReadOnly = false;
                return;
            }
        }
        isReadOnly = true;
    }

    public enum Status {
        SUCCESS, WARNING, FAILURE
    }


    private void setFlowMap(STCustomMappings customMappings) {
        if(customMappings!=null&&customMappings.getSTCustomMapping()!=null)
            addCustomMappings(customMappings.getSTCustomMapping());
        addAutomaticMappings();
    }

    public void addCustomMappings(List<STCustomMapping> customMappings) {
        for(STCustomMapping mapping : customMappings) {
            map.addMapping(new StepMap(mapping.getSourceStep(), mapping.getSourceData(), mapping.getTargetStep(), mapping.getTargetData()));
            addCustomMappingsValidation(mapping);
            getStepByFinalName(mapping.getTargetStep(),"").tryAssignDataTypeByName(mapping.getTargetData());
        }
    }

    /*
    validation the following in custom mapping:
    1.source step exists
    2.target step exists
    3.source data exists
    4.target data exists
    5.if trying to assign two or more outputs to an input
    6.if trying to assign a source step that is located after the target step(like a loop)
    7.the source data is actually in input
    8.the target data is actually an output
    9.source data and target data are the same type
     */
    private void addCustomMappingsValidation(STCustomMapping mapping){
        Step sourceStep=getStepByFinalName(mapping.getSourceStep(),"custom mapping");
        Step targetStep = getStepByFinalName(mapping.getTargetStep(),"custom mapping");
        if(!sourceStep.containsDataMember(mapping.getSourceData()))throw new RuntimeException("In custom mapping:"+mapping.getSourceData()+" does not exist");
        if(!targetStep.containsDataMember(mapping.getTargetData()))throw new RuntimeException("In custom mapping:"+mapping.getTargetData()+" does not exist");
        if(targetStep.checkIfDataMemberIsAssigned(mapping.getTargetData()))throw new RuntimeException("In custom mapping, two outputs are assigned to:"+mapping.getTargetData());
        if(steps.indexOf(sourceStep)>=steps.indexOf(targetStep))throw new RuntimeException("in custom mapping: trying to assign source step "+ sourceStep.getFinalName()+" that is after target step "+targetStep.getFinalName());
        if(sourceStep.IsDataMemberIsInput(mapping.getSourceData()))throw new RuntimeException("In custom mapping:"+mapping.getSourceData()+" is an input, but referred as output");
        if(!targetStep.IsDataMemberIsInput(mapping.getTargetData()))throw new RuntimeException("In custom mapping:"+mapping.getTargetData()+" is an output, but referred as input");
        if(!targetStep.getTypeOfDataMember(mapping.getTargetData()).equals(sourceStep.getTypeOfDataMember(mapping.getSourceData())))throw new RuntimeException("In custom mapping:"+mapping.getTargetData()+" and "+mapping.getSourceData()+" are not the same type");
    }

    // loop one steps - by order
    //  get their outputs#
    //  put in outputs map
    //      assign outputs# to input
    //          go through all inputs and assign compatible ones (name & type) to the outputs.
    //
    //scan all inputs and check if they are free
    public void addAutomaticMappings() {
        for(int i = 0; i < steps.size(); i++) {
            List<DataType> currentStepOutputs = steps.get(i).getAllOutputs();
            addOutputs(currentStepOutputs);
            assignStepOutputsToMatchingInputs(currentStepOutputs, i);
        }
    }

    // gets a steps output and its index, goes through all the inputs of the steps that
    // come AFTER it, and assign matching ones.
    private void assignStepOutputsToMatchingInputs(List<DataType> currentStepOutputs, int stepIndex) {
        //for each of the step's outputs...
        for(DataType stepOutput : currentStepOutputs) {
            // if it's NOT a formal output
            if(formalOutputsNames.contains(stepOutput.getEffectiveName()))
                continue;
            // search each following step for matching data types (matching = is input, not assigned yet, same type, same effectiveName)
            for(int i = stepIndex + 1; i < steps.size(); i++) {
                List<DataType> stepDataMembers = steps.get(i).getAllData();
                for(DataType stepDataMember : stepDataMembers) {
                    if(stepDataMember.isInput() &&
                            !stepDataMember.isAssigned() &&
                            stepDataMember.getEffectiveName().equals(stepOutput.getEffectiveName()) &&
                            stepDataMember.getType() == stepOutput.getType()) {

                        map.addMapping(new StepMap(steps.get(stepIndex).getFinalName(), stepOutput.getEffectiveName(), steps.get(i).getFinalName(), stepDataMember.getEffectiveName()));
                        stepDataMember.setAssigned(true);
                    }
                }
            }
        }
    }

    private void formalOutputsValidation(){
        for(String formalOutput:formalOutputsNames){
            if(outputs.get(formalOutput)==null)throw new RuntimeException("Formal output:"+formalOutput+" does not exist");
        }
    }

    private void findFreeInputs(){
        for(Step step:steps){
            for(DataType dataMember:step.getAllData()){
                if(dataMember.isInput() && !dataMember.isAssigned()) {
                    if(!(dataMember instanceof UserFriendly) && dataMember.isMandatory())throw new RuntimeException("The free input:"+dataMember.getEffectiveName()+" is not user friendly");
                    addFreeInput(dataMember);
                    addFreeInputInformationToFreeInputDescriptorsArray(dataMember, step);
                }
            }
        }

        freeInputsValidation();
    }

    public void addFreeInput(DataType input){
        if(!freeInputs.containsKey(input.getEffectiveName())){
            freeInputs.put(input.getEffectiveName(), new HashSet<DataType>());
        }
        freeInputs.get(input.getEffectiveName()).add(input);
    }

    void freeInputsValidation(){
        Boolean firstElementEntered =false;
        HashSet<String> inputTypeset=new HashSet<>();
        for(String freeInputsSetName:freeInputs.keySet()){
            for(DataType freeInputByName :freeInputs.get(freeInputsSetName)) {
                if (inputTypeset.add(freeInputByName.getType().toString()) && firstElementEntered)
                    throw new RuntimeException("Free inputs by the name " + freeInputByName.getEffectiveName() + " have different data types");
                firstElementEntered =true;
            }
            inputTypeset.clear();
            firstElementEntered=false;
        }
    }


    private void addOutputs(List<DataType> outputsToAdd) {
        for(DataType outputToAdd : outputsToAdd) {
            outputs.put(outputToAdd.getEffectiveName(), outputToAdd);
        }
    }

    public String getName() {
        return name;
    }

    public void execute(){
        flowRunsCounter++;
        clearAllStepsLogs();
        if(!areAllMandatoryFreeInputsSet())
            throw new RuntimeException("An attempt was made to run the Flow while there are UNASSIGNED mandatory free inputs");
        Instant start=Instant.now();
        try {
            for (Step step : steps) {
                step.execute();
                if (step.getStatus() == Step.Status.Failure && step.isBlocking())
                    throw new RuntimeException("Error:" + step.getFinalName() + " has failed while executing, and does not continue in case of failure, source: "+step.getSummaryLine());
                if(map.getMappingsByStep(step.getFinalName())!=null) {
                    for (StepMap mapping : map.getMappingsByStep(step.getFinalName()))
                        getStepByFinalName(mapping.getTargetStepName(), "").setInputByName(step.getOutputs(mapping.getSourceDataName()).get(0),mapping.getTargetDataName());
                }
            }

            flowRunsummery="flow execution ended successfully";
            setFlowStatus();
        } catch (RuntimeException e) {
            status=Status.FAILURE;
            flowRunsummery=e.getMessage();
            throw e;
        }

        Instant finish=Instant.now();
        runTime= Duration.between(start,finish).toMillis();
        calculateAvgRunTime();
        createFlowLog();
        createFlowHistory();
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
        flowLog.setFlowName(name);
        flowLog.setStatus(status);
        for(String formalOutputName:formalOutputsNames)
            flowLog.addFormalOutputsPresentation(outputs.get(formalOutputName));
    }

    private void createFlowHistory(){
        flowRunHistory=new FlowRunHistory();
        flowRunHistory.setFlowId(flowLog.getFlowId());
        flowRunHistory.setRunTime(runTime);
        flowRunHistory.setFlowName(name);
        flowRunHistory.setTimeStamp(flowLog.getTimeStamp());
        flowRunHistory.setStatus(status);
        for(String freeInputString:freeInputs.keySet()){
            for(DataType freeInput:freeInputs.get(freeInputString))
                flowRunHistory.addFreeInput(freeInput);
        }
        for(Step step:steps)
            flowRunHistory.addStep(step);
        for(String outputString:outputs.keySet())
            flowRunHistory.addOutput(outputs.get(outputString));
    }

    public FlowLog getFlowLog() {
        return flowLog;
    }

    public FlowRunHistory getFlowRunHistory(){
        return flowRunHistory;
    }

    public FlowDescriptor getFlowDescriptor() {
        FlowDescriptor descriptor = new FlowDescriptor();
        descriptor.setFlowName(name);
        descriptor.setFlowDescription(description);
        descriptor.setFormalOutputNames((HashSet<String>) formalOutputsNames.clone());
        descriptor.setReadonly(isReadOnly);
        descriptor.setStepDescriptors(getStepDescriptors());
        descriptor.setFreeInputs(freeInputsDescriptors);
        descriptor.setOutputs(outputDescriptors);
        return descriptor;
    }

    private void fillOutputsDescriptorsArray() {
        for(Step step : steps)
            addStepsOutputsToDescriptors(step);
    }

    private void addStepsOutputsToDescriptors(Step step) {
        for(DataType output : step.getAllOutputs()) {
            outputDescriptors.add(new StepOutputDescriptor(output.getEffectiveName(), output.getType(), step.getFinalName()));
        }
    }

    private ArrayList<StepDescriptor> getStepDescriptors() {
        ArrayList<StepDescriptor> descriptors = new ArrayList<>();
        for(Step step : steps) {
            descriptors.add(step.getStepDescriptor());
        }
        return descriptors;
    }

    private void addFreeInputInformationToFreeInputDescriptorsArray(DataType freeInput, Step targetStep) {
        int freeInputDescriptorIndex = getIndexOfFreeInputInFreeInputDescriptorsArray(freeInput);
        if (freeInputDescriptorIndex == -1) {
            freeInputsDescriptors.add(new FreeInputDescriptor(freeInput.getEffectiveName(), freeInput.getUserFriendlyName(), freeInput.getType(), freeInput.isMandatory()));
            freeInputDescriptorIndex = freeInputsDescriptors.size() - 1;
        }
        addTargetStepToFreeInputDescriptor(freeInputDescriptorIndex, targetStep);
    }

    private int getIndexOfFreeInputInFreeInputDescriptorsArray(DataType freeInput) {
        for(int i = 0; i < freeInputsDescriptors.size(); i++) {
            if(freeInputsDescriptors.get(i).getInputEffectiveName().equals(freeInput.getEffectiveName()))
                return i;
        }
        return -1;
    }

    private void addTargetStepToFreeInputDescriptor(int freeInputDescriptorIndex, Step targetStep) {
        freeInputsDescriptors.get(freeInputDescriptorIndex).getAssociatedSteps().add(targetStep.getFinalName());
    }

    public ArrayList<FreeInputDescriptor> getFreeInputsDescriptors() {
        return freeInputsDescriptors;
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

    public boolean areAllMandatoryFreeInputsSet()
    {
        for(String freeInputName : freeInputs.keySet())
            for(DataType freeInput : freeInputs.get(freeInputName)) {
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
}
