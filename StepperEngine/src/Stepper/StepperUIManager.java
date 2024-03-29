package Stepper;

// This object acts as a Facade, offering the Stepper system UI functions
// and managing the stepper object itself

import Flow.*;
import RunHistory.FlowRunHistory;
import Steps.*;
import Users.RoleDescriptor;
import Users.User;
import Users.UserDescriptor;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

public class StepperUIManager {
    Stepper stepper;
    boolean isLoaded;
    boolean isFlowRan;

    public StepperUIManager() {
        isLoaded = false;
        isFlowRan=false;
    }

//    public synchronized void LoadStepperFromXmlFile(String xmlFilePath, String username) throws FileNotFoundException, JAXBException {
//        // First assigning it to a new Stepper object, to not override anything in case of failure.
//        Stepper newStepper = new Stepper(xmlFilePath, username);
//
//        // If we got here, no exceptions were thrown, thus the stepper was loaded successfully.
//
//        if(!isLoaded) {
//            stepper = newStepper;
//            isLoaded = true;
//        }
//        else
//            stepper.addFlowDefinitionsFromANewStepper(newStepper);
//
//    }

    public synchronized void LoadStepperFromXmlString(String xmlString, String username) throws FileNotFoundException, JAXBException {
        Stepper newStepper = null;

        if(!isLoaded) {
            stepper = new Stepper(xmlString, username, new HashSet<Flow>());
            isLoaded = true;
        }
        else {
            newStepper = new Stepper(xmlString, username, stepper.getFlowDefinitions());
            stepper.addFlowDefinitionsFromANewStepper(newStepper);
        }


    }

    public ArrayList<String> getFlowNames(){
        return stepper.getFlowNames();
    }

    public FlowDescriptor getFlowDescriptor(String flowName) {
        return stepper.getFlowDescriptor(flowName);
    }

    public ArrayList<FreeInputDescriptor> getFreeInputDescriptorsByFlow(String flowName) {
        return stepper.getFreeInputDescriptorsByFlow(flowName);
    }

    public void setFreeInput(String flowID, String freeInputEffectiveName, String dataStr) {
        stepper.setFreeInput(flowID, freeInputEffectiveName, dataStr);
    }

    public boolean areAllMandatoryFreeInputsSet(String flowID) {
        return stepper.areAllMandatoryFreeInputsSet(flowID);
    }

    public void runFlow(String flowID, String username) {
        stepper.runFlow(flowID, username);
        isFlowRan=true; //problem with this##################################################### is even needed?
    }

    public Vector<FlowRunHistory> getFlowsRunHistories(String username) {
        return stepper.getFlowsRunHistories(username);
    }

    public FlowLog getFlowLog(String flowID) {
        return stepper.getFlowLog(flowID);
    }

    public ArrayList<FlowStatistics> getFlowStatistics() {
        return stepper.getFlowStatistics();
    }

    public ArrayList<StepStatistics> getStepsStatistics() {
        return stepper.getStepsStatistics();
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public boolean isFlowRan(){return  isFlowRan;}
    public boolean doesFlowHasContinuations(String flowName) {return stepper.doesFlowHaveContinuations(flowName);}
    public ArrayList<String> getFlowContinuationOptions(String flowName) {return stepper.getFlowContinuationOptions(flowName);}
    public HashMap<String,String> getFlowContinuationMap(String flowID, String targetFlowName){return stepper.getFlowContinuationMap(flowID, targetFlowName);}
    public void activateContinuation(String sourceFlowName, String targetFlowName) {activateContinuation(sourceFlowName, targetFlowName);}
    public HashMap<String, String> getFreeInputsCurrentValues(String flowID) {return stepper.getFreeInputsCurrentValues(flowID);}
    public int getFlowTotalNumberOfSteps(String flowID) {
        return stepper.getFlowTotalNumberOfSteps(flowID);
    }
    public int getFlowNumberOfCompletedSteps(String flowID) {
        return stepper.getFlowNumberOfCompletedSteps(flowID);
    }
    public String createNewFlow(String flowName, String username) {return stepper.createNewFlow(flowName, username);}
    public String getFlowName(String flowID) {
        return stepper.getFlowName(flowID);
    }
    public boolean hasFlowFailed(String flowID) {
        return stepper.hasFlowFailed(flowID);
    }

    public HashSet<String> getAllRolesNames() {
        return stepper.getAllRolesNames();
    }

    public HashSet<String> getAllUsersWithGivenRole(String roleName) {
        return stepper.getAllUsersWithGivenRole(roleName);
    }

    public HashSet<String> getAllUsersNames() { return stepper.getAllUsersNames();}

    public boolean isUserAllowedToLoadNewStepperFile(String username) {
        return stepper.isUserAllowedToLoadNewStepperFile(username);
    }

    public boolean isUserExists(String username) {
        return stepper.isUserExists(username);
    }

    public void addUser(String username) {stepper.addUser(username);}

    public UserDescriptor getUserDescriptor(String userName) {return stepper.getUserDescriptor(userName);}

    public void assignRoleToUser(String username, String roleName) {
        stepper.assignRoleToUser(username, roleName);
    }
    public boolean isUserManager(String userName) {
        return stepper.isUserManager(userName);
    }
    public void setManager(String username, boolean value) {
        stepper.setManager(username, value);
    }

    public RoleDescriptor getRoleDescriptor(String roleName) {return stepper.getRoleDescriptor(roleName);}

    public void addNewRole(String roleName, String description) {stepper.addNewRole(roleName, description);}

    public void setPermittedFlowsForRole(String roleName, String[] flowNames) {
        stepper.setPermittedFlowsForRole(roleName, flowNames);
    }

    public void setUsersAssignedRoles(String username, String[] rolesToAssign) {
        stepper.setUsersAssignedRoles(username, rolesToAssign);
    }

    public void setFreeInputs(String flowID, HashMap<String, String> valuesMap) {
        stepper.setFreeInputs(flowID, valuesMap);
    }

    public void deleteRole(String roleName) { stepper.deleteRole(roleName); }

    public ArrayList<String> getFlowPermittedContinuationTargetForUser(String flowID, String username) {
        return stepper.getFlowPermittedContinuationTargetForUser(flowID, username);}

    public ArrayList<FlowDescriptor> getPermittedFlowsDescriptorsByUser(String username) {return stepper.getPermittedFlowsDescriptorsByUser(username);}
    public Flow.Status getFlowStatus(String flowID) { return stepper.getFlowStatus(flowID);}
    public FlowRunHistory getFlowRunHistory(String flowID) { return stepper.getFlowRunHistory(flowID);}

    public void removeUser(String username) { stepper.removeUser(username);}
}
