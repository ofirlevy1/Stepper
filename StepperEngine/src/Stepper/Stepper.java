package Stepper;

/*

Things To Consider:

*


 */


import DataTypes.DataType;
import Flow.*;
import Generated.STFlow;
import Generated.STStepper;
import RunHistory.FlowRunHistory;
import Steps.*;
import Exceptions.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Stepper {

    HashSet<FlowDefinition> flowsDefinitions;  // just the static characteristics of the flows.
    Vector<Flow> flows; // the actual flows (that can be executed) - holds all the dynamic data
    String exceptionString;
    Vector<FlowRunHistory> flowsRunHistories;
    ExecutorService threadPool;

    // attempting to load from an invalid file should NOT override any data.
    public Stepper(String xmlFilePath) throws FileNotFoundException, JAXBException{
        validatePathPointsToXMLFile(xmlFilePath);
        STStepper stStepper = deserializeFrom(new FileInputStream(new File(xmlFilePath)));
        validateFlowNames(stStepper);
        flowsDefinitions = new HashSet<>();
        flows = new Vector<Flow>();
        flowsRunHistories = new Vector<>();
        for(STFlow stFlow : stStepper.getSTFlows().getSTFlow())
            flowsDefinitions.add(new FlowDefinition(stFlow));
        validateContinuations();
        if(stStepper.getSTThreadPool() < 1)
            throw new RuntimeException("The Stepper XML file defined a thread pool of size lower than 1");
        threadPool = Executors.newFixedThreadPool(stStepper.getSTThreadPool());
    }

    public FlowDescriptor getFlowDescriptor(String flowName) {
        return getFlowDefinitionByName(flowName).getFlowDescriptor();
    }

    private FlowDefinition getFlowDefinitionByName(String flowName) {
        for(FlowDefinition flowDefinition : flowsDefinitions) {
            if(flowDefinition.getName().equals(flowName))
                return flowDefinition;
        }
        throw new RuntimeException("getFlowByName: flow '" + flowName + " not found");
    }

    private STStepper deserializeFrom(FileInputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance("Generated");
        Unmarshaller u = jc.createUnmarshaller();
        return (STStepper) u.unmarshal(in);
    }

    public ArrayList<String> getFlowNames(){
        ArrayList<String> flowNames = new ArrayList<>();
        for(FlowDefinition flowDefinition : flowsDefinitions)
            flowNames.add(flowDefinition.getName());
        return flowNames;
    }

    public ArrayList<FreeInputDescriptor> getFreeInputDescriptorsByFlow(String flowName) {
        return getFlowDefinitionByName(flowName).getFreeInputsDescriptors();
    }

    public void setFreeInput(String flowID, String freeInputEffectiveName, String dataStr) {
        getFlowByID(flowID).setFreeInput(freeInputEffectiveName, dataStr);
    }

    public ArrayList<FlowStatistics> getFlowStatistics(){
        ArrayList<FlowStatistics> flowStatistics=new ArrayList<>();
        for(Flow flow:flows)
            flowStatistics.add(flow.getFlowStatistics());
        return flowStatistics;
    }

    public ArrayList<StepStatistics> getStepsStatistics(){
        ArrayList<StepStatistics> stepStatistics=new ArrayList<>();
        stepStatistics.add(new StepStatistics(CollectFilesInFolderStep.getStepStartUpCount(), CollectFilesInFolderStep.getStepAvgDuration(), new CollectFilesInFolderStep().getName()));
        stepStatistics.add(new StepStatistics(CsvExporterStep.getStepStartUpCount(), CsvExporterStep.getStepAvgDuration(), new CsvExporterStep().getName()));
        stepStatistics.add(new StepStatistics(FilesDeleterStep.getStepStartUpCount(), FilesDeleterStep.getStepAvgDuration(), new FilesDeleterStep().getName()));
        stepStatistics.add(new StepStatistics(FilesContentExtractorStep.getStepStartUpCount(), FilesContentExtractorStep.getStepAvgDuration(), new FilesContentExtractorStep().getName()));
        stepStatistics.add(new StepStatistics(FileDumperStep.getStepStartUpCount(), FileDumperStep.getStepAvgDuration(), new FileDumperStep().getName()));
        stepStatistics.add(new StepStatistics(FilesRenamerStep.getStepStartUpCount(), FilesRenamerStep.getStepAvgDuration(), new FilesRenamerStep().getName()));
        stepStatistics.add(new StepStatistics(PropertiesExporterStep.getStepStartUpCount(), PropertiesExporterStep.getStepAvgDuration(), new PropertiesExporterStep().getName()));
        stepStatistics.add(new StepStatistics(SpendSomeTimeStep.getStepStartUpCount(), SpendSomeTimeStep.getStepAvgDuration(), new SpendSomeTimeStep().getName()));
        return  stepStatistics;
    }

    public boolean areAllMandatoryFreeInputsSet(String flowID) {
        return getFlowByID(flowID).areAllMandatoryFreeInputsSet();
    }

    public void runFlow(String flowID) {
        Flow flow = getFlowByID(flowID);
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                flowsRunHistories.add(flow.execute());
            }
        });
    }

    public Vector<FlowRunHistory> getFlowsRunHistories() {
        return flowsRunHistories;
    }

    private void validatePathPointsToXMLFile(String path) {
        if (!path.endsWith("xml")) {
            throw new PathDoesNotPointToXMLFileException();
        }
        File file = new File(path);
        if (!file.isFile())
            throw new PathDoesNotPointToXMLFileException();
    }

    private void validateFlowNames(STStepper stStepper)throws RuntimeException{
        HashSet<String> flowNamesSet=new HashSet<>();
        for(STFlow stFlow:stStepper.getSTFlows().getSTFlow())
            if(!flowNamesSet.add(stFlow.getName()))throw new RuntimeException("There is more than one flow named: "+stFlow.getName()+", please provide a unique name for each flow");

    }

    public FlowLog getFlowLog(String flowID) {
        return getFlowByID(flowID).getFlowLog();
    }

    public boolean doesFlowHaveContinuations(String flowName) {
        return getFlowDefinitionByName(flowName).hasContinuations();
    }

    // This returns an array of the target flows names.
    public ArrayList<String> getFlowContinuationOptions(String flowName) {
        return getFlowDefinitionByName(flowName).getContinuationTargets();
    }

    public HashMap<String, String> getFlowContinuationMap(String sourceFlowName, String targetFlowName){
        HashMap<String,String> dataMap=new HashMap<>();
        Flow sourcFlow=getFlowByName(sourceFlowName);
        Flow targetFlow=getFlowByName(targetFlowName);
        Continuation continuation=sourcFlow.getContinuation(targetFlowName);
        if(!continuation.hasCustomContinuationDataMappings())
            return dataMap;
        for(String sourceDataName:continuation.getDataMap().keySet()){
            DataType sourceData=sourcFlow.getDataTypeByEffectiveName(sourceDataName);
            dataMap.put(continuation.getDataMap().get(sourceDataName),sourceData.getPresentableString());
        }
        return dataMap;
    }

    public void activateContinuation(String sourceFlowID, String targetFlowID) {
        Flow sourceFlow = getFlowByID(sourceFlowID);
        Flow targetFlow = getFlowByID(targetFlowID);
        if(!sourceFlow.hasContinuations() || !sourceFlow.getContinuationTargets().contains(targetFlow.getName()))
            throw new RuntimeException("An attempt was made to activate an undefined continuation");

        Continuation continuation = sourceFlow.getContinuation(targetFlow.getName());
        if(continuation.hasCustomContinuationDataMappings()) {
            HashMap<String, String> customDataMappings = continuation.getDataMap();
            for(String sourceDataName : customDataMappings.keySet()) {
                DataType sourceFlowDataType = sourceFlow.getDataTypeByEffectiveName(sourceDataName);
                targetFlow.setFreeInput(customDataMappings.get(sourceDataName), sourceFlowDataType.getPresentableString());
            }
        }
    }

    public HashMap<String, String> getFreeInputsCurrentValues(String flowID) {
        return getFlowByID(flowID).getFreeInputsCurrentValues();
    }

    public int getFlowTotalNumberOfSteps(String flowName) {
        return getFlowDefinitionByName(flowName).getTotalNumberOfSteps();
    }

    public int getFlowNumberOfCompletedSteps(String flowID) {
        return getFlowByID(flowID).getCompletedStepsCounter();
    }

    // ofir - this method should no longer be necessary in the new design (FlowDefinition & Flow...).

    //    public boolean hasFlowMostRecentRunFailed(String flowName) {
    //        return (!getFlowByName(flowName).isRunning()) && (getFlowByName(flowName).getStatus() != null && getFlowByName(flowName).getStatus() == Flow.Status.FAILURE);
    //    }

    private void validateContinuations() {
        for(FlowDefinition flowDefinition : flowsDefinitions) {
            if(flowDefinition.hasContinuations()) {
                for(String continuationTarget : flowDefinition.getContinuationTargets()) {
                    // Making sure the target continuation flow actually exists in the XML
                    if(!this.getFlowNames().contains(continuationTarget)) {
                        throw new RuntimeException("Flow '" + flowDefinition.getName() + "' has an undefined continuation target: '" + continuationTarget + "'");
                    }

                    if(flowDefinition.getContinuation(continuationTarget).hasCustomContinuationDataMappings()) {
                        HashMap<String, String> customMap = flowDefinition.getContinuation(continuationTarget).getDataMap();
                        for(String sourceDataType : customMap.keySet()) {
                            if(!flowDefinition.hasDataType(sourceDataType)) {
                                throw new RuntimeException("Flow '" + flowDefinition.getName() + "' defined a non existing data type as a continuation source: '" + sourceDataType + "'");
                            }
                            if(!getFlowDefinitionByName(continuationTarget).isFreeInput(customMap.get(sourceDataType))) {
                                throw new RuntimeException("Flow '" + flowDefinition.getName() + "' defined a continuation target that either doesn't exists, or is not a free input: " + customMap.get(sourceDataType) + "'");
                            }
                        }
                    }
                }

            }
        }
    }

    public Flow getFlowByID(String id) {
        for(Flow flow : flows) {
            if(flow.getFlowID().equals(id)) {
                return flow;
            }
        }
        throw new RuntimeException("An attempt was made to get a flow using a non existent flow ID");
    }

    public String getFlowNameByID(String flowID) {
        return getFlowByID(flowID).getName();
    }
}
