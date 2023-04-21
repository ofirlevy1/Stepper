package Flow;

import DataTypes.DataType;
import Generated.STFlow;
import Generated.STFlowLevelAlias;
import Generated.STStepInFlow;
import Generated.STStepper;
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




    public Flow(STFlow flow)
    {
        name = flow.getName();
        description = flow.getSTFlowDescription();
        formalOutputsNames = new HashSet<>(Arrays.asList(flow.getSTFlowOutput().split(",")));

        System.out.println("The formal outputs: " + formalOutputsNames);

        loadSteps(flow.getSTStepsInFlow().getSTStepInFlow());
        setFlowLevelAliases(flow.getSTFlowLevelAliasing().getSTFlowLevelAlias());

        setIsReadOnly();

    }

    // Input validation to do:
    // Non existent steps/data types
    // outputs with the same name
    private void setFlowLevelAliases(List<STFlowLevelAlias> aliases) {
        for(STFlowLevelAlias alias : aliases) {
            //Step current = getStepByEffectiveName(alias.getStep());

        }
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



    private void Map() {

    }

}
