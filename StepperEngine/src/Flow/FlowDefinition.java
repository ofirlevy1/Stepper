package Flow;

import DataTypes.DataType;
import DataTypes.UserFriendly;
import Generated.*;
import Steps.Step;
import Steps.StepFactory;

import java.util.*;

public class FlowDefinition {

    private String name;
    private String description;
    private HashSet<String> formalOutputsNames;
    private ArrayList<FreeInputDescriptor> freeInputsDescriptors;
    private ArrayList<StepOutputDescriptor> outputDescriptors;
    private ArrayList<Step> steps;
    private FlowMap map;
    private HashMap<String, String> initialValues;
    private HashMap<String, DataType> outputs;
    private boolean isReadOnly;
    private HashMap<String, HashSet<DataType>> freeInputs;
    private boolean hasContinuations;
    private ArrayList<Continuation> continuations;
    public STFlow stFlow;


    public FlowDefinition(STFlow flow)
    {
        this();
        stFlow = flow;
        name = flow.getName();
        description = flow.getSTFlowDescription();
        formalOutputsNames = new HashSet<>(Arrays.asList(flow.getSTFlowOutput().split(",")));
        freeInputsDescriptors = new ArrayList<FreeInputDescriptor>();
        outputDescriptors = new ArrayList<StepOutputDescriptor>();


        loadStepsIntoStepsArray(flow.getSTStepsInFlow(), steps);
        fillOutputsDescriptorsArray();
        if(flow.getSTFlowLevelAliasing() != null && flow.getSTFlowLevelAliasing().getSTFlowLevelAlias() != null && !flow.getSTFlowLevelAliasing().getSTFlowLevelAlias().isEmpty())
            setFlowLevelAliases(flow.getSTFlowLevelAliasing());


        setFlowMap(flow.getSTCustomMappings());


        if(flow.getSTInitialInputValues() != null && flow.getSTInitialInputValues().getSTInitialInputValue() != null && !flow.getSTInitialInputValues().getSTInitialInputValue().isEmpty()) {
            loadInitialValues(flow.getSTInitialInputValues());
            validateInitialValues();
            setInitialValues();
        }


        findFreeInputs();
        formalOutputsValidation();
        setIsReadOnly();

        setContinuations(flow.getSTContinuations());
    }

    private FlowDefinition() {
        this.steps = new ArrayList<>();
        this.outputs = new HashMap<>();
        this.freeInputs = new HashMap<>();
        this.map = new FlowMap();
        this.continuations = new ArrayList<>();
        this.initialValues = new HashMap<>();
    }

    // Input validation to do:
    // non existent steps
    private void loadStepsIntoStepsArray(STStepsInFlow stSteps, ArrayList<Step> targetStepsArray) {
        if(stSteps==null||stSteps.getSTStepInFlow()==null)throw new RuntimeException("No steps provided");
        for(STStepInFlow stStep : stSteps.getSTStepInFlow()) {
            Step current = StepFactory.createStep(stStep.getName());
            if(current==null) throw  new RuntimeException("Step "+stStep.getName()+" does not exist");
            if(stStep.getAlias() != null)
                current.setAlias(stStep.getAlias());
            if(stStep.isContinueIfFailing() != null)
                current.setBlocking(!stStep.isContinueIfFailing());
            targetStepsArray.add(current);
        }
        //checking if there are steps with the same final name
        HashSet<String> stepsSet= new HashSet<>();
        for(Step step:targetStepsArray)
            if(!stepsSet.add(step.getFinalName()))throw new RuntimeException("There's more than one step named '"+step.getFinalName()+"' (after aliasing)");
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

    public Step getStepByFinalName(String name, String exceptionString) {
        for(Step step : steps) {
            if(step.getFinalName().equals(name)) {
                return step;
            }
        }
        throw new RuntimeException("In "+exceptionString+": " + name + " does not exist");
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


    // loop one steps - by order
    //    //  get their outputs#
    //    //  put in outputs map
    //    //      assign outputs# to input
    //    //          go through all inputs and assign compatible ones (name & type) to the outputs.
    //    //
    //    //scan all inputs and check if they are free
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
            //if(formalOutputsNames.contains(stepOutput.getEffectiveName()))
            //    continue;
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

    private void loadInitialValues(STInitialInputValues stInitialValues) {
        for(STInitialInputValue initialValue : stInitialValues.getSTInitialInputValue()) {
            initialValues.put(initialValue.getInputName(), initialValue.getInitialValue());
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

    private void addOutputs(List<DataType> outputsToAdd) {
        for(DataType outputToAdd : outputsToAdd) {
            outputs.put(outputToAdd.getEffectiveName(), outputToAdd);
        }
    }

    private void validateInitialValues() {
        for(String name : initialValues.keySet()) {
            if(!this.hasDataType(name)) {
                throw new RuntimeException("Flow '" + this.name + "' defines an initial value for non-existent data type '" + name + "'");
            }
        }
    }

    private boolean hasInitialValue(String dataTypeEffectiveName) {
        return initialValues.containsKey(dataTypeEffectiveName);
    }

    public boolean hasDataType(String name) {
        for(Step step : steps) {
            if(step.containsDataMember(name))
                return true;
        }
        return false;
    }

    private void setInitialValues() {
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

    private void formalOutputsValidation(){
        for(String formalOutput:formalOutputsNames){
            if(outputs.get(formalOutput)==null)throw new RuntimeException("Formal output:"+formalOutput+" does not exist");
        }
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

    public ArrayList<Step> getStepsArrayCopy() {
        ArrayList<Step> result = new ArrayList<>();
        loadStepsIntoStepsArray(stFlow.getSTStepsInFlow(), result);
        return result;
    }

    public FlowMap getMap() {return map;}

    public String getName() {
        return name;
    }

    public HashSet<String> getFormalOutputsNames() {
        return formalOutputsNames;
    }

    public HashMap<String, DataType> getOutputs() {
        return outputs;
    }

    public HashMap<String, HashSet<DataType>> getFreeInputs() {
        return freeInputs;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<StepOutputDescriptor> getOutputDescriptors() {
        return outputDescriptors;
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public HashMap<String, String> getInitialValues() {
        return initialValues;
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    public boolean isHasContinuations() {
        return hasContinuations;
    }

    public ArrayList<Continuation> getContinuations() {
        return continuations;
    }
}
