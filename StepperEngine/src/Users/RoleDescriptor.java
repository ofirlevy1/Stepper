package Users;

import java.util.HashSet;

public class RoleDescriptor {
    private String name;
    private String description;
    private HashSet<String> usersWithThisRole;
    private HashSet<String> permittedFlowsNames;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HashSet<String> getUsersWithThisRole() {
        return usersWithThisRole;
    }

    public void setUsersWithThisRole(HashSet<String> usersWithThisRole) {
        this.usersWithThisRole = usersWithThisRole;
    }

    public HashSet<String> getPermittedFlowsNames() {
        return permittedFlowsNames;
    }

    public void setPermittedFlowsNames(HashSet<String> permittedFlowsNames) {
        this.permittedFlowsNames = permittedFlowsNames;
    }
}
