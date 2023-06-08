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

public class Stepper {

    HashSet<Flow> flows;
    String exceptionString;
    ArrayList<FlowRunHistory> flowsRunHistories;


    // attempting to load from an invalid file should NOT override any data.
    public Stepper(String xmlFilePath) throws FileNotFoundException, JAXBException{
        validatePathPointsToXMLFile(xmlFilePath);
        STStepper stStepper = deserializeFrom(new FileInputStream(new File(xmlFilePath)));
        validateFlowNames(stStepper);
        flows = new HashSet<>();
        flowsRunHistories = new ArrayList<>();
        for(STFlow stFlow : stStepper.getSTFlows().getSTFlow())
            flows.add(new Flow(stFlow));
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
        getFlowByName(flowName).execute();
        flowsRunHistories.add(getFlowByName(flowName).getFlowRunHistory());
    }

    public ArrayList<FlowRunHistory> getFlowsRunHistories() {
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
}
