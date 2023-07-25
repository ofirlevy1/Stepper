package Users;

import java.util.ArrayList;
import java.util.HashSet;

public class UserDescriptor {
    private String name;
    private HashSet<String> rolesNames;
    private HashSet<String> permittedFlowsNames;
    private int numberOfExecutedFlows;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashSet<String> getRoles() {
        return rolesNames;
    }

    public void setRoles(HashSet<String> roles) {
        this.rolesNames = roles;
    }

    public int getNumberOfExecutedFlows() {
        return numberOfExecutedFlows;
    }

    public void setNumberOfExecutedFlows(int numberOfExecutedFlows) {
        this.numberOfExecutedFlows = numberOfExecutedFlows;
    }

    public HashSet<String> getPermittedFlowsNames() {
        return permittedFlowsNames;
    }

    public void setPermittedFlowsNames(HashSet<String> permittedFlowsNames) {
        this.permittedFlowsNames = permittedFlowsNames;
    }
}
