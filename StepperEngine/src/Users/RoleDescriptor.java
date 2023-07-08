package Users;

import java.util.HashSet;

public class RoleDescriptor {
    private String name;
    private String description;
    private HashSet<String> usersWithThisRole;

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
}
