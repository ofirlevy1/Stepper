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

    HashSet<Flow> flows;
    String exceptionString;
    Vector<FlowRunHistory> flowsRunHistories;
    ExecutorService threadPool;

    // attempting to load from an invalid file should NOT override any data.
    public Stepper(String xmlFilePath) throws FileNotFoundException, JAXBException{
        validatePathPointsToXMLFile(xmlFilePath);
        STStepper stStepper = deserializeFrom(new FileInputStream(new File(xmlFilePath)));
        validateFlowNames(stStepper);
        flows = new HashSet<>();
        flowsRunHistories = new Vector<>();
        for(STFlow stFlow : stStepper.getSTFlows().getSTFlow())
            flows.add(new Flow(stFlow));
        if(stStepper.getSTThreadPool() < 1)
            throw new RuntimeException("The Stepper XML file defined a thread pool of size lower than 1");
        threadPool = Executors.newFixedThreadPool(stStepper.getSTThreadPool());
    }

    public FlowDescriptor getFlowDescriptor(String flowName) {
        return getFlowByName(flowName).getFlowDescriptor();
    }

    private Flow getFlowByName(String flowName) {
        for(Flow flow : flows) {
            if(flow.getName().equals(flowName))
                return flow;
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
        for(Flow flow : flows)
            flowNames.add(flow.getName());
        return flowNames;
    }

    public ArrayList<FreeInputDescriptor> getFreeInputDescriptorsByFlow(String flowName) {
        return getFlowByName(flowName).getFreeInputsDescriptors();
    }

    public void setFreeInput(String flowName, String freeInputEffectiveName, String dataStr) {
        getFlowByName(flowName).setFreeInput(freeInputEffectiveName, dataStr);
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

    public boolean areAllMandatoryFreeInputsSet(String flowName) {
        return getFlowByName(flowName).areAllMandatoryFreeInputsSet();
    }

    public void runFlow(String flowName) {
        Flow flow = getFlowByName(flowName);
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

    public FlowLog getFlowLog(String flowName) {
        return getFlowByName(flowName).getFlowLog();
    }

    public boolean doesFlowHaveContinuations(String flowName) {
        return getFlowByName(flowName).hasContinuations();
    }

    // This returns an array of the target flows names.
    public ArrayList<String> getFlowContinuationOptions(String flowName) {
        return getFlowByName(flowName).getContinuationTargets();
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

    public void activateContinuation(String sourceFlowName, String targetFlowName) {
        Flow sourceFlow = getFlowByName(sourceFlowName);
        Flow targetFlow = getFlowByName(targetFlowName);
        if(!sourceFlow.hasContinuations() || !sourceFlow.getContinuationTargets().contains(targetFlowName))
            throw new RuntimeException("An attempt was made to activate an undefined continuation");

        Continuation continuation = sourceFlow.getContinuation(targetFlowName);
        if(continuation.hasCustomContinuationDataMappings()) {
            HashMap<String, String> customDataMappings = continuation.getDataMap();
            for(String sourceDataName : customDataMappings.keySet()) {
                DataType sourceFlowDataType = sourceFlow.getDataTypeByEffectiveName(sourceDataName);
                // ofir - I'm not completely sure about this. this uses the "getPresentableString" to fill
                // the DataType as if the user filled it. this should work fine, if "getPresentableString"
                // returns a string that the user could've given to fill the same data type.
                targetFlow.setFreeInput(customDataMappings.get(sourceDataName), sourceFlowDataType.getPresentableString());
            }
        }
    }

    public HashMap<String, String> getFreeInputsCurrentValues(String flowName) {
        return getFlowByName(flowName).getFreeInputsCurrentValues();
    }

    public int getFlowTotalNumberOfSteps(String flowName) {
        return getFlowByName(flowName).getTotalNumberOfSteps();
    }

    public int getFlowNumberOfCompletedSteps(String flowName) {
        return getFlowByName(flowName).getCompletedStepsCounter();
    }

}
