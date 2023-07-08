package Users;

import java.util.HashSet;

public class UserDescriptor {
    private String name;
    private HashSet<String> rolesNames;
    private int numberOfPermittedFlows;
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

    public int getNumberOfPermittedFlows() {
        return numberOfPermittedFlows;
    }

    public void setNumberOfPermittedFlows(int numberOfPermittedFlows) {
        this.numberOfPermittedFlows = numberOfPermittedFlows;
    }

    public int getNumberOfExecutedFlows() {
        return numberOfExecutedFlows;
    }

    public void setNumberOfExecutedFlows(int numberOfExecutedFlows) {
        this.numberOfExecutedFlows = numberOfExecutedFlows;
    }
}
