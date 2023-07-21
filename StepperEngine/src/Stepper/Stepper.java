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
import Users.RoleDescriptor;
import Users.User;
import Users.UserDescriptor;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
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
    private Object rolesLock;

    public Stepper(String xmlString, String username) throws FileNotFoundException, JAXBException{
        if(!isUserAllowedToLoadNewStepperFile(username))
            throw new RuntimeException("Non-admin user '" + username + " has tried to load a new file into the system.");
        //validatePathPointsToXMLFile(xmlFilePath);
        stStepper = deserializeFromInputStream(new ByteArrayInputStream(xmlString.getBytes()));
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
        this.rolesLock = new Object();
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

    private STStepper deserializeFromInputStream(InputStream in) throws JAXBException {
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

    public void setFreeInputs(String flowID, HashMap<String, String> valuesMap) {
        for(String freeInputEffectiveName : valuesMap.keySet()) {
            setFreeInput(flowID, freeInputEffectiveName, valuesMap.get(freeInputEffectiveName));
        }
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

    public void runFlow(String flowID, String username) {
        ValidateThatFlowExist(flowID);
        validateThatUserExists(username);
        Flow flow = getFlowByID(flowID);
        User user = getUserByName(username);

        if(!flow.getOwner().equals(username))
            throw new RuntimeException("Flow '" + flow.getName() +"' owner is '" + flow.getOwner() + "' but user '" + username + "' has attempted to run it!");

        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                flowsRunHistories.add(flow.execute());
                user.addExecutedFlowID(flowID);
            }
        });
    }

    public Vector<FlowRunHistory> getFlowsRunHistories(String username) {
        validateThatUserExists(username);

        if(getUserByName(username).isManager())
            return (Vector<FlowRunHistory>) flowsRunHistories.clone();

        // This will hold only the histories of flows that the user has run.
        Vector<FlowRunHistory> userFlowsHistories = new Vector<>();

        for(FlowRunHistory flowRunHistory : flowsRunHistories) {
            if(flowRunHistory.getOwner().equals(username))
                userFlowsHistories.add(flowRunHistory);
        }

        return userFlowsHistories;
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

    public HashMap<String, String> getFlowContinuationMap(String sourceFlowID, String targetFlowName){
        HashMap<String,String> dataMap=new HashMap<>();
        Flow sourceFlow=getFlowByID(sourceFlowID);
        Continuation continuation=sourceFlow.getContinuation(targetFlowName);
        if(!continuation.hasCustomContinuationDataMappings())
            return dataMap;
        for(String sourceDataName:continuation.getDataMap().keySet()){
            DataType sourceData=sourceFlow.getDataTypeByEffectiveName(sourceDataName);
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
        return (!getFlowByID(flowID).isRunning()) && (getFlowByID(flowID).getStatus() != null && getFlowByID(flowID).getStatus() != Flow.Status.NOT_RUN_YET && getFlowByID(flowID).getStatus() == Flow.Status.FAILURE);
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

    public String createNewFlow(String flowName, String username) {
        validateThatUserExists(username);
        ValidateThatFlowDefinitionExists(flowName);

        if(!getUserByName(username).isAuthorizedToRunFlow(flowName))
            throw new RuntimeException("User '" + username + "' is not authorized to create the flow '" + flowName +"'");

        for(STFlow stFlow : stFlows) {
            if(stFlow.getName().equals(flowName)) {
                Flow newFlow = new Flow(stFlow);
                newFlow.setOwner(username);
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

        Role readOnlyRole = new Role("Read Only Flows", "This role have access to all the Read-Only flows (flows that doesn't make changes in the system).");
        Role allFlowsRole = new Role("All Flows", "This role has access to all of the flows in the system.");

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
        synchronized(rolesLock) {
            for(Role currentRole : roles) {
                if(currentRole.getName().equals(roleName))
                    return currentRole;
            }
            throw new RuntimeException("An attempt was made to find a role that doesn't exist: '" + roleName + "'");
        }
    }

    public HashSet<String> getAllRolesNames() {
        synchronized(rolesLock) {
            HashSet<String> rolesNames = new HashSet<>();
            for(Role role : roles)
                rolesNames.add(role.getName());
            return rolesNames;
        }
    }

    public HashSet<String> getAllUsersWithGivenRole(String roleName) {
        synchronized(rolesLock) {
            HashSet<String> result = new HashSet<>();
            for(User user : users) {
                if(user.hasRole(roleName))
                    result.add(user.getName());
            }
            return result;
        }
    }

    public HashSet<String> getAllUsersNames() {

        HashSet<String> userNames = new HashSet<>();
        for(User user : users) {
            if(!user.getName().equals("admin"))
                userNames.add(user.getName());
        }
        return userNames;
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

    public UserDescriptor getUserDescriptor(String userName) {
        validateThatUserExists(userName);
        return getUserByName(userName).getUserDescriptor();
    }

    private User getUserByName(String userName) {
        for(User user : users) {
            if(user.getName().equals(userName))
                return user;
        }
        throw new RuntimeException("'getUserByName' was called on a user that doesn't exist - " + userName + "'");
    }

    public void assignRoleToUser(String username, String roleName) {
        synchronized(rolesLock) {
            validateThatUserExists(username);
            if(!isRoleExists(roleName))
                throw new RuntimeException("An attempt was made to assign a user to a role that doesn't exist - '" + roleName + "'");

            getUserByName(username).addRole(getRoleByName(roleName));
        }
    }

    public boolean isRoleExists(String roleName) {
        synchronized(rolesLock) {
            for(Role role : roles) {
                if(role.getName().equals(roleName))
                    return true;
            }
            return false;
        }
    }

    public boolean isFlowDefinitionExists(String flowName) {
        for(Flow flow : flowsDefinitions) {
            if(flow.getName().equals(flowName))
                return true;
        }
        return false;
    }

    public boolean isUserManager(String userName) {
        validateThatUserExists(userName);
        return getUserByName(userName).isManager();
    }

    private void validateThatUserExists(String userName) {
        if(!isUserExists(userName))
            throw new RuntimeException("User '" + userName + "' does not exist!");
    }

    private void validateThatRoleExists(String roleName) {
        synchronized(rolesLock) {
            if(!isRoleExists(roleName))
                throw new RuntimeException("Role '" + roleName + "' does not exist!");
        }
    }

    private void ValidateThatFlowDefinitionExists(String flowName) {
        if(!isFlowDefinitionExists(flowName))
            throw new RuntimeException("Flow '" + flowName + "' does not exist!");
    }

    private void ValidateThatFlowExist(String flowID) {
        if(!isFlowExist(flowID))
            throw new RuntimeException("Flow '" + flowID + "' does not exist!");
    }

    private boolean isFlowExist(String flowID) {
        for(Flow flow : flows) {
            if(flow.getID().equals(flowID))
                return true;
        }
        return false;
    }

    public void setManager(String username, boolean value) {
        validateThatUserExists(username);
        getUserByName(username).setManager(value);
    }

    public RoleDescriptor getRoleDescriptor(String roleName) {
        synchronized(rolesLock) {
            validateThatRoleExists(roleName);
            return getRoleByName(roleName).getRoleDescriptor(getAllUsersWithGivenRole(roleName));
        }
    }

    public void addNewRole(String roleName, String description) {
        synchronized(rolesLock) {
            if(isRoleExists(roleName))
                throw new RuntimeException("An attempt was made to add a role that already exist! please choose a different role name");

            roles.add(new Role(roleName, description));
        }
    }

    public void setPermittedFlowsForRole(String roleName, String[] flowNames) {
        synchronized(rolesLock) {
            validateThatRoleExists(roleName);
            for(String flowName : flowNames) {
                ValidateThatFlowDefinitionExists(flowName);
            }
            getRoleByName(roleName).setPermittedFlows(flowNames);
        }
    }

    public void setUsersAssignedRoles(String username, String[] rolesNames) {
        synchronized (rolesLock) {
            validateThatUserExists(username);
            HashSet<Role> roles = new HashSet<>();
            for(String roleName : rolesNames) {
                validateThatRoleExists(roleName);
                roles.add(getRoleByName(roleName));
            }
            getUserByName(username).setRoles(roles);
        }
    }

    public void deleteRole(String roleName) {
        validateThatRoleExists(roleName);
        Role roleToDelete = getRoleByName(roleName);

        synchronized(rolesLock) {
            for(User user : users) {
                if(user.hasRole(roleName))
                    throw new RuntimeException("Role '" + roleName + "' cannot be deleted because it's currently associated with one or more users");
            }
            roles.remove(roleToDelete);
        }
    }

    public ArrayList<String> getFlowPermittedContinuationTargetForUser(String flowID, String username) {
        validateThatUserExists(username);
        ValidateThatFlowExist(flowID);

        Flow flow = getFlowByID(flowID);
        User user = getUserByName(username);

        ArrayList<String> continuationOptions = getFlowContinuationOptions(flow.getName());
        ArrayList<String> unpermittedContinuationOptions = new ArrayList<>();

        for(String continuationOption : continuationOptions) {
            if(!user.isAuthorizedToRunFlow(continuationOption))
                unpermittedContinuationOptions.add(continuationOption);
        }

        continuationOptions.removeAll(unpermittedContinuationOptions);
        return continuationOptions;
    }

    public ArrayList<FlowDescriptor> getPermittedFlowsDescriptorsByUser(String username) {
        UserDescriptor userDescriptor = getUserDescriptor(username);
        ArrayList<FlowDescriptor> permittedFlowsDescriptors = new ArrayList<>();

        for(String flowName : userDescriptor.getPermittedFlowsNames()) {
            permittedFlowsDescriptors.add(getFlowDescriptor(flowName));
        }

        return permittedFlowsDescriptors;
    }

    public Flow.Status getFlowStatus(String flowID) {
        ValidateThatFlowExist(flowID);
        return getFlowByID(flowID).getStatus();
    }
}
