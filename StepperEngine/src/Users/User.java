package Users;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class User {
    private String name; // Acts as the unique identifier
    private boolean isManager; // A manager can see histories of ALL flow runs from ALL the users, and has run access to all flows.
    private HashSet<Role> roles;
    private ArrayList<String> executedFlowsIDs;

    public User(String name) {
        this.name = name;
        isManager = false;
        roles = new HashSet<>();
        executedFlowsIDs = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isManager() {
        return isManager;
    }

    public void setManager(boolean manager) {
        isManager = manager;
    }

    public HashSet<Role> getRoles() {
        return roles;
    }

    public void addRole(Role roleToAdd) {
        roles.add(roleToAdd);
    }

    public HashSet<String> getAllPermittedFlowsNames(ArrayList<String> allFlows) {
        HashSet<String> permittedFlows = new HashSet<>();

        if(isManager == true) {
            permittedFlows.addAll(allFlows);
            return permittedFlows;
        }

        for(Role role : roles)
            permittedFlows.addAll(role.getPermittedFlowsNames());

        return permittedFlows;
    }

    public void addExecutedFlowID(String flowID) {
        executedFlowsIDs.add(flowID);
    }

    public ArrayList<String> getExecutedFlowsIDs() {
        return (ArrayList<String>)executedFlowsIDs.clone();
    }

    public boolean hasRole(String roleName) {
        for(Role role : roles) {
            if(role.getName().equals(roleName))
                return true;
        }
        return false;
    }

    public UserDescriptor getUserDescriptor(ArrayList<String> allFlows) {
        HashSet<String> permittedFlowsNames = this.getAllPermittedFlowsNames(allFlows);
        UserDescriptor userDescriptor = new UserDescriptor();
        userDescriptor.setName(this.name);
        userDescriptor.setNumberOfExecutedFlows(executedFlowsIDs.size());
        HashSet<String> rolesNames = new HashSet<>();
        for(Role role : roles) {
            rolesNames.add(role.getName());
        }
        userDescriptor.setPermittedFlowsNames(permittedFlowsNames);
        userDescriptor.setRoles(rolesNames);
        return userDescriptor;
    }

    public void setRoles(HashSet<Role> roles) {
        this.roles = (HashSet<Role>) roles.clone();
    }

    public boolean isAuthorizedToRunFlow(String flowName) {
        if(isManager == true)
            return true;

        for(Role role : roles)
            if(role.getPermittedFlowsNames().contains(flowName))
                return true;

        return false;
    }
}
