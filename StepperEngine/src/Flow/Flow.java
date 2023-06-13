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
    private HashMap<String, String> initialValues;

    private FlowMap map;

    private ArrayList<FreeInputDescriptor> freeInputsDescriptors;
    private ArrayList<StepOutputDescriptor> outputDescriptors;
    private boolean hasContinuations;
    private ArrayList<Continuation> continuations;

    private int completedStepsCounter;
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
        if(flow.getSTFlowLevelAliasing() != null && flow.getSTFlowLevelAliasing().getSTFlowLevelAlias() != null && !flow.getSTFlowLevelAliasing().getSTFlowLevelAlias().isEmpty())
            setFlowLevelAliases(flow.getSTFlowLevelAliasing());
        if(flow.getSTInitialInputValues() != null && flow.getSTInitialInputValues().getSTInitialInputValue() != null && !flow.getSTInitialInputValues().getSTInitialInputValue().isEmpty())
            loadAndSetInitialValues(flow.getSTInitialInputValues());
        setFlowMap(flow.getSTCustomMappings());
        findFreeInputs();
        formalOutputsValidation();
        setIsReadOnly();

        setContinuations(flow.getSTContinuations());
    }

    private Flow() {
        this.steps = new ArrayList<>();
        this.outputs = new HashMap<>();
        this.freeInputs = new HashMap<>();
        this.map = new FlowMap();
        this.flowLog=new FlowLog();
        this.continuations = new ArrayList<>();
        this.initialValues = new HashMap<>();
    }

    // Input validation to do:
    // Non-existent steps/data types
    // outputs with the same name
    private void setFlowLevelAliases(STFlowLevelAliasing aliases) {
        if(aliases!=null&&aliases.getSTFlowLevelAlias()!=null) {
            for (STFlowLevelAlias alias : aliases.getSTFlowLevelAlias()) {
                Step current = getStepByFinalName(alias.getStep(), "flow level aliasing");
                if (!current.trySetDataAlias(alias.getSourceDataName(), alias.getAlias()))
                    throw new RuntimeException(alias.getSourceDataName() + "Step does not exist!");
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
                        throw new RuntimeException("There's more than one output named '" + dataMember.getEffectiveName() + "' (after aliasing)");
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
            if(current==null) throw  new RuntimeException("Step "+stStep.getName()+" does not exist");
            if(stStep.getAlias() != null)
                current.setAlias(stStep.getAlias());
            if(stStep.isContinueIfFailing() != null)
                current.setBlocking(!stStep.isContinueIfFailing());
            steps.add(current);
        }
        //checking if there are steps with the same final name
        HashSet<String> stepsSet= new HashSet<>();
        for(Step step:steps)
            if(!stepsSet.add(step.getFinalName()))throw new RuntimeException("There's more than one step named '"+step.getFinalName()+"' (after aliasing)");
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
        if(initialValues.containsKey(mapping.getTargetData())) throw new RuntimeException("in Custom Mapping: A mapping exist to '" + mapping.getTargetData() + "', which has an initial value! a dataType with an initial value cannot be a mapping target");
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
                            stepDataMember.getType() == stepOutput.getType() &&
                            !initialValues.containsKey(stepDataMember.getEffectiveName())) {

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
                if(dataMember.isInput() && !dataMember.isAssigned() && !initialValues.containsKey(dataMember.getEffectiveName())) {
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

    public synchronized FlowRunHistory execute(){
        flowRunsCounter++;
        completedStepsCounter = 0;
        clearAllStepsLogs();
        if(!areAllMandatoryFreeInputsSet())
            throw new RuntimeException("An attempt was made to run the Flow while there are UNSET mandatory free inputs");
        Instant start=Instant.now();
        try {
            for (Step step : steps) {
                step.execute();
                completedStepsCounter++;
                if (step.getStatus() == Step.Status.Failure && step.isBlocking())
                    throw new RuntimeException("'" + step.getFinalName() + "' has failed while executing, and does not continue in case of failure, source: "+step.getSummaryLine());
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
        flowLog.setFlowName(name);
        flowLog.setStatus(status);
        for(String formalOutputName:formalOutputsNames)
            flowLog.addFormalOutputsPresentation(outputs.get(formalOutputName));
    }

    private void createFlowHistory(){
        try {
            flowRunHistory = new FlowRunHistory();
            flowRunHistory.setFlowId(flowLog.getFlowId());
            flowRunHistory.setRunTime(runTime);
            flowRunHistory.setFlowName(name);
            flowRunHistory.setTimeStamp(flowLog.getTimeStamp());
            flowRunHistory.setStatus(status);
            for (String freeInputString : freeInputs.keySet()) {
                for (DataType freeInput : freeInputs.get(freeInputString))
                    flowRunHistory.addFreeInput(freeInput);
            }
            for (Step step : steps)
                flowRunHistory.addStep(step);
            for (String outputString : outputs.keySet())
                flowRunHistory.addOutput(outputs.get(outputString));
            for (String freeInputName : freeInputs.keySet())
                for (DataType freeInput : freeInputs.get(freeInputName))
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
        descriptor.setFlowName(name);
        descriptor.setFlowDescription(description);
        descriptor.setFormalOutputNames((HashSet<String>) formalOutputsNames.clone());
        descriptor.setReadonly(isReadOnly);
        descriptor.setStepDescriptors(getStepDescriptors());
        descriptor.setFreeInputs(freeInputsDescriptors);
        descriptor.setOutputs(outputDescriptors);
        descriptor.setNumberOfContinuations(continuations.size());
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
            StepDescriptor stepDescriptor=step.getStepDescriptor();
            if(map.getMappingsByStep(step.getName())!=null) {
                for (StepMap stepMap : map.getMappingsByStep(step.getName())) {
                    stepDescriptor.addOutputConnections(new OutputConnections(stepMap.getSourceDataName(), step.getOutputs(stepMap.getSourceDataName()).get(0).hasAlias(),step.getOutputs(stepMap.getSourceDataName()).get(0).getName(), stepMap.getTargetDataName(), stepMap.getTargetStepName()));
                }
            }
            if(map.getInputMappingsByStep(step.getName())!=null) {
                for (StepMap stepMap : map.getInputMappingsByStep(step.getName())) {
                    stepDescriptor.addInputConnections(new InputConnections(stepMap.getTargetDataName(), step.getSingleInput(stepMap.getTargetDataName()).get(0).hasAlias(), step.getSingleInput(stepMap.getTargetDataName()).get(0).getName(),stepMap.getSourceDataName(), stepMap.getSourceStepName()));
                }
            }

            stepDescriptor.addUnconnectedDataMembers(step.getAllData());
            descriptors.add(stepDescriptor);
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

    private void setContinuations(STContinuations continuations)
    {
        if(continuations == null || continuations.getSTContinuation() == null || continuations.getSTContinuation().isEmpty()) {
            hasContinuations = false;
            return;
        }
        hasContinuations = true;
        for(STContinuation stContinuation : continuations.getSTContinuation()) {
            this.continuations.add(new Continuation(stContinuation));
        }
    }

    public boolean hasContinuations() {
        return hasContinuations;
    }

    public ArrayList<String> getContinuationTargets() {
        if(!hasContinuations)
            throw new RuntimeException("An Attempt was made to get continuation targets on a flow that has no continuations");

        ArrayList<String> continuationTargets = new ArrayList<>();

        for(Continuation continuation : continuations) {
            continuationTargets.add(continuation.targetFlow);
        }

        return continuationTargets;
    }

    public Continuation getContinuation(String targetFlow) {
        if(!hasContinuations || !getContinuationTargets().contains(targetFlow))
            throw new RuntimeException("An attempt was made to get a non existent continuation");

        return continuations.get(getContinuationTargets().indexOf(targetFlow));
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

    private void loadAndSetInitialValues(STInitialInputValues stInitialValues) {
        loadInitialValues(stInitialValues);
        ArrayList<DataType> stepDataTypes;

        // Go through all the steps, and insert the initial value on every DataType that's an input,
        // is userFriendly. (and has a defined initial value)

        for(Step step : steps) {
            stepDataTypes = step.getAllData();
            for(DataType dataType : stepDataTypes) {
                if(dataType.isInput() && dataType instanceof UserFriendly && initialValues.containsKey(dataType.getEffectiveName())) {
                    ((UserFriendly)dataType).setData(initialValues.get(dataType.getEffectiveName()));
                }
            }
        }
    }

    private void loadInitialValues(STInitialInputValues stInitialValues) {
        for(STInitialInputValue initialValue : stInitialValues.getSTInitialInputValue()) {
            initialValues.put(initialValue.getInputName(), initialValue.getInitialValue());
        }
    }

    private boolean hasInitialValue(String dataTypeEffectiveName) {
        return initialValues.containsKey(dataTypeEffectiveName);
    }

    public int getTotalNumberOfSteps() {
        return steps.size();
    }

    public int getCompletedStepsCounter() {
        return completedStepsCounter;
    }
}
