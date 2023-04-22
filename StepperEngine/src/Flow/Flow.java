package Flow;

import DataTypes.DataType;
import Generated.*;
import Steps.Step;
import Steps.StepFactory;

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
    private HashSet<String> freeInputsNames;

    private Flow.Status status;

    private static double durationAvgInMs = 0.0;
    private static int flowRunsCounter = 0;
    // FlowLog flowLog; // This should have a unique ID, time stamps...

    FlowMap map;


    public Flow(STFlow flow)
    {
        this();
        name = flow.getName();
        description = flow.getSTFlowDescription();
        formalOutputsNames = new HashSet<>(Arrays.asList(flow.getSTFlowOutput().split(",")));

        loadSteps(flow.getSTStepsInFlow().getSTStepInFlow());
        setFlowLevelAliases(flow.getSTFlowLevelAliasing().getSTFlowLevelAlias());
        setFlowMap(flow.getSTCustomMappings().getSTCustomMapping());

        findFreeInputs();

        setIsReadOnly();

    }

    private Flow() {
        this.steps = new ArrayList<>();
        this.outputs = new HashMap<>();
        this.freeInputs = new HashMap<>();
        this.freeInputsNames = new HashSet<>();
        this.map = new FlowMap();
    }

    // Input validation to do:
    // Non-existent steps/data types
    // outputs with the same name
    private void setFlowLevelAliases(List<STFlowLevelAlias> aliases) {
        for(STFlowLevelAlias alias : aliases) {
            Step current = getStepByFinalName(alias.getStep(),"flow level aliasing");
            if(current.trySetDataAlias(alias.getSourceDataName(), alias.getAlias()))throw new RuntimeException("In flow level aliasing:"+alias.getSourceDataName()+" does not exist");
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
    private void loadSteps(List<STStepInFlow> stSteps) {
        for(STStepInFlow stStep : stSteps) {
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


    private void setFlowMap(List<STCustomMapping> customMappings) {
        addCustomMappings(customMappings);
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

    private void findFreeInputs(){
        for(Step step:steps){
            for(DataType dataMember:step.getAllData()){
                if(dataMember.isInput() && !dataMember.isAssigned()) {
                    if(!dataMember.isUserFriendly())throw new RuntimeException("The free input:"+dataMember.getEffectiveName()+" is not user friendly");
                    addFreeInput(dataMember);
                }
            }
        }
    }

    public void addFreeInput(DataType input){
        if(!freeInputs.containsKey(input.getEffectiveName())){
            freeInputs.put(input.getEffectiveName(), new HashSet<DataType>());
        }
        freeInputs.get(input.getEffectiveName()).add(input);
    }


    private void addOutputs(List<DataType> outputsToAdd) {
        for(DataType outputToAdd : outputsToAdd) {
            outputs.put(outputToAdd.getEffectiveName(), outputToAdd);
        }
    }

    public String getName() {
        return name;
    }
}
