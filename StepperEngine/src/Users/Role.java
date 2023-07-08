package Users;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class Role {
    private String name; // Acts as the unique identifier

    private String description;
    private HashSet<String> permittedFlowsNames;

    public Role(String name, String description) {
        this.name = name;
        permittedFlowsNames = new HashSet<>();
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashSet<String> getPermittedFlowsNames() {
        return permittedFlowsNames;
    }
    public void addPermittedFlowName(String permittedFlowName) {
        permittedFlowsNames.add(permittedFlowName);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RoleDescriptor getRoleDescriptor(HashSet<String> allUsersWithRole) {
        RoleDescriptor roleDescriptor = new RoleDescriptor();
        roleDescriptor.setName(this.name);
        roleDescriptor.setDescription(this.description);
        roleDescriptor.setUsersWithThisRole(allUsersWithRole);
        return roleDescriptor;
    }

    public void setPermittedFlows(String[] flowNames) {
        permittedFlowsNames = new HashSet<>(Arrays.asList(flowNames));
    }
}
