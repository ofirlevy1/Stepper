package Users;

import java.util.ArrayList;
import java.util.HashSet;

public class Role {
    private String name; // Acts as the unique identifier
    private HashSet<String> permittedFlowsNames;

    public Role(String name) {
        this.name = name;
        permittedFlowsNames = new HashSet<>();
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

}
