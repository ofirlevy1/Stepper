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
import Users.Role;
import Users.User;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Stepper {

    private HashSet<Flow> flowsDefinitions; // These are used just for definition / data.
    private Vector<Flow> flows; // These are used at run time.
    private String exceptionString;
    private Vector<FlowRunHistory> flowsRunHistories;
    private ExecutorService threadPool;
    private STStepper stStepper;
    private List<STFlow> stFlows; // used to create/load new dynamic (runnable) flows.
    private HashSet<User> users;
    private HashSet<Role> roles;

    public Stepper(String xmlFilePath, String username) throws FileNotFoundException, JAXBException{
        if(!isUserAllowedToLoadNewStepperFile(username))
            throw new RuntimeException("Non-admin user '" + username + " has tried to load a new file into the system.");
        validatePathPointsToXMLFile(xmlFilePath);
        stStepper = deserializeFrom(new FileInputStream(new File(xmlFilePath)));
        validateFlowNames(stStepper);
        flowsDefinitions = new HashSet<>();
        flows = new Vector<Flow>();
        flowsRunHistories = new Vector<>();
        for(STFlow stFlow : stStepper.getSTFlows().getSTFlow())
            flowsDefinitions.add(new Flow(stFlow));
        validateContinuations();
        if(stStepper.getSTThreadPool() < 1)
            throw new RuntimeException("The Stepper XML file defined a thread pool of size lower than 1");
        threadPool = Executors.newFixedThreadPool(stStepper.getSTThreadPool());
        stFlows = stStepper.getSTFlows().getSTFlow();
        initiatePredefinedRolesAndUsers();
    }

    public FlowDescriptor getFlowDescriptor(String flowName) {
        return getFlowDefinitionByName(flowName).getFlowDescriptor();
    }

    private Flow getFlowDefinitionByName(String flowName) {
        for(Flow flow : flowsDefinitions) {
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
        for(Flow flow : flowsDefinitions)
            flowNames.add(flow.getName());
        return flowNames;
    }

    public ArrayList<FreeInputDescriptor> getFreeInputDescriptorsByFlow(String flowName) {
        return getFlowDefinitionByName(flowName).getFreeInputsDescriptors();
    }

    public void setFreeInput(String flowID, String freeInputEffectiveName, String dataStr) {
        getFlowByID(flowID).setFreeInput(freeInputEffectiveName, dataStr);
    }

    public ArrayList<FlowStatistics> getFlowStatistics(){
        HashMap<String, FlowStatistics> statistics = new HashMap<String, FlowStatistics>();
        for(Flow flow:flows) {
            if(statistics.containsKey(flow.getName()))
                statistics.get(flow.getName()).addRunDurations(flow.getRunDurations());
            else {
                statistics.put(flow.getName(), new FlowStatistics());
                statistics.get(flow.getName()).setFlowName(flow.getName());
                statistics.get(flow.getName()).addRunDurations(flow.getRunDurations());
            }
        }

        ArrayList<FlowStatistics> result = new ArrayList<>();
        for(String flowName : statistics.keySet()) {
            result.add(statistics.get(flowName));
        }

        return result;
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

    public HashMap<String, String> getFlowContinuationMap(String sourceFlowID, String targetFlowID){
        HashMap<String,String> dataMap=new HashMap<>();
        Flow sourcFlow=getFlowByID(sourceFlowID);
        Flow targetFlow=getFlowByID(targetFlowID);
        Continuation continuation=sourcFlow.getContinuation(targetFlowID);
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
                // ofir - I'm not completely sure about this. this uses the "getPresentableString" to fill
                // the DataType as if the user filled it. this should work fine, if "getPresentableString"
                // returns a string that the user could've given to fill the same data type.
                targetFlow.setFreeInput(customDataMappings.get(sourceDataName), sourceFlowDataType.getPresentableString());
            }
        }
    }

    public HashMap<String, String> getFreeInputsCurrentValues(String flowID) {
        return getFlowByID(flowID).getFreeInputsCurrentValues();
    }

    public int getFlowTotalNumberOfSteps(String flowID) {
        return getFlowByID(flowID).getTotalNumberOfSteps();
    }

    public int getFlowNumberOfCompletedSteps(String flowID) {
        return getFlowByID(flowID).getCompletedStepsCounter();
    }

    public boolean hasFlowFailed(String flowID) {
        return (!getFlowByID(flowID).isRunning()) && (getFlowByID(flowID).getStatus() != null && getFlowByID(flowID).getStatus() == Flow.Status.FAILURE);
    }

    private void validateContinuations() {
        for(Flow flow : flowsDefinitions) {
            if(flow.hasContinuations()) {
                for(String continuationTarget : flow.getContinuationTargets()) {
                    // Making sure the target continuation flow actually exists in the XML
                    if(!this.getFlowNames().contains(continuationTarget)) {
                        throw new RuntimeException("Flow '" + flow.getName() + "' has an undefined continuation target: '" + continuationTarget + "'");
                    }

                    if(flow.getContinuation(continuationTarget).hasCustomContinuationDataMappings()) {
                        HashMap<String, String> customMap = flow.getContinuation(continuationTarget).getDataMap();
                        for(String sourceDataType : customMap.keySet()) {
                            if(!flow.hasDataType(sourceDataType)) {
                                throw new RuntimeException("Flow '" + flow.getName() + "' defined a non existing data type as a continuation source: '" + sourceDataType + "'");
                            }
                            if(!getFlowDefinitionByName(continuationTarget).isFreeInput(customMap.get(sourceDataType))) {
                                throw new RuntimeException("Flow '" + flow.getName() + "' defined a continuation target that either doesn't exists, or is not a free input: " + customMap.get(sourceDataType) + "'");
                            }
                        }
                    }
                }

            }
        }
    }

    private Flow getFlowByID(String flowID) {
        for(Flow flow : flows) {
            if(flow.getID().equals(flowID))
                return flow;
        }
        throw new RuntimeException("No flow was found with flowID: " + flowID);
    }

    public String createNewFlow(String flowName) {
        for(STFlow stFlow : stFlows) {
            if(stFlow.getName().equals(flowName)) {
                Flow newFlow = new Flow(stFlow);
                flows.add(newFlow);
                return newFlow.getID();
            }
        }
        throw new RuntimeException("An attempt was made to create a new flow of non-existing flow name '" + flowName + "')");
    }

    public String getFlowName(String flowID) {
        return getFlowByID(flowID).getName();
    }

    public void addFlowDefinitionsFromANewStepper(Stepper newStepper) {
        // Adding the new flows to the flow definitions & STFlows - only if no flows of the same name already exist.
        // And add them to the readonly & all flows roles.
        for(Flow flow : newStepper.flowsDefinitions) {
            if(!doesFlowDefinitionExist(flow.getName())){
                flowsDefinitions.add(flow);
                stFlows.add(newStepper.getSTFlowObjectByName(flow.getName()));
                getRoleByName("All Flows").addPermittedFlowName(flow.getName());
                if(flow.isReadOnly())
                    getRoleByName("Read Only Flows").addPermittedFlowName(flow.getName());
            }
        }
    }

    public boolean doesFlowDefinitionExist(String flowName) {
        for(Flow flowDefinition : flowsDefinitions)
            if(flowDefinition.getName().equals(flowName))
                return true;
        return false;
    }

    private STFlow getSTFlowObjectByName(String flowName) {
        for(STFlow stFlow : stStepper.getSTFlows().getSTFlow()) {
            if(stFlow.getName().equals(flowName))
                return stFlow;
        }
        throw new RuntimeException("An attempt was made to get the STFlow object of not existing flow '" + flowName + "'");
    }

    private void initiatePredefinedRolesAndUsers() {
        roles = new HashSet<>();
        users = new HashSet<>();

        Role readOnlyRole = new Role("Read Only Flows");
        Role allFlowsRole = new Role("All Flows");

        for(Flow flowDefinition : flowsDefinitions) {
            allFlowsRole.addPermittedFlowName(flowDefinition.getName());
            if(flowDefinition.isReadOnly())
                readOnlyRole.addPermittedFlowName(flowDefinition.getName());
        }

        roles.add(readOnlyRole);
        roles.add(allFlowsRole);

        User adminUser = new User("admin");
        adminUser.setManager(true);
        adminUser.addRole(allFlowsRole);

        users.add(adminUser);

    }

    private Role getRoleByName(String roleName) {
        for(Role currentRole : roles) {
            if(currentRole.getName().equals(roleName))
                return currentRole;
        }
        throw new RuntimeException("An attempt was made to find a role that doesn't exist: '" + roleName + "'");
    }

    public HashSet<String> getAllRolesNames() {
        HashSet<String> rolesNames = new HashSet<>();
        for(Role role : roles)
            rolesNames.add(role.getName());
        return rolesNames;
    }

    public HashSet<String> getAllUsersWithGivenRole(String roleName) {
        HashSet<String> result = new HashSet<>();
        for(User user : users) {
            if(user.hasRole(roleName))
                result.add(user.getName());
        }
        return result;
    }

    public HashSet<User> getAllUsers() {
        return (HashSet<User>)users.clone();
    }

    // This is static so that it can be called even when the Stepper object is not loaded yet (the first time).
    public static boolean isUserAllowedToLoadNewStepperFile(String username) {
        return username.equals("admin");
    }

    public boolean isUserExists(String username) {
        for(User user : users) {
            if(user.getName().equals(username))
                return true;
        }
        return false;
    }

    public void addUser(String username) {
        if(isUserExists(username))
            throw new RuntimeException("An attempt was made to add a user that already exists in the system! ('" + username + "')");
        users.add(new User(username));
    }
}
