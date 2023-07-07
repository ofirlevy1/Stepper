package Users;

import java.util.ArrayList;
import java.util.HashSet;

public class User {
    private String name; // Acts as the unique identifier
    private boolean isManager; // A manager can see histories of ALL flow runs from ALL the users.
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

    public HashSet<String> getAllPermittedFlowsNames() {
        HashSet<String> permittedFlows = new HashSet<>();

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

}
