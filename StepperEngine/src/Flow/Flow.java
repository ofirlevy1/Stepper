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
    // Non existent steps/data types
    // outputs with the same name
    private void setFlowLevelAliases(List<STFlowLevelAlias> aliases) {
        for(STFlowLevelAlias alias : aliases) {
            Step current = getStepByFinalName(alias.getStep());
            current.trySetDataAlias(alias.getSourceDataName(), alias.getAlias());
        }
    }

    private Step getStepByFinalName(String name) {
        for(Step step : steps) {
            if(step.getFinalName().equals(name)) {
                return step;
            }
        }
        throw new RuntimeException("In flow level aliasing: " + name + " does not exist");
    }

    // Input validation to do:
    // non existent steps
    private void loadSteps(List<STStepInFlow> stSteps) {
        for(STStepInFlow stStep : stSteps) {
            Step current = StepFactory.createStep(stStep.getName());
            if(stStep.getAlias() != null)
                current.setAlias(stStep.getAlias());
            if(stStep.isContinueIfFailing() != null)
                current.setBlocking(!stStep.isContinueIfFailing());
            steps.add(current);
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
            getStepByFinalName(mapping.getTargetStep()).tryAssignDataTypeByName(mapping.getTargetData());
        }
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

//        for(Step step : steps) {
//
//        }


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


    private void addOutputs(List<DataType> outputsToAdd) {
        for(DataType outputToAdd : outputsToAdd) {
            outputs.put(outputToAdd.getEffectiveName(), outputToAdd);
        }
    }

    public String getName() {
        return name;
    }
}
