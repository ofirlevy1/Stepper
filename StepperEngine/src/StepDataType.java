
public class StepDataType<T> {

    // user friendly means it can be entered by the user.
    private boolean userFriendly;
    private String name;
    private String alias;
    private String userFriendlyName;
    private boolean hasAlias;
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getEffectiveName() {
        return hasAlias ? alias : name;
    }

    public boolean isUserFriendly() {
        return userFriendly;
    }

    public void setUserFriendly(boolean userFriendly) {
        this.userFriendly = userFriendly;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
        hasAlias = true;
    }

    public String getUserFriendlyName() {
        return userFriendlyName;
    }

    public void setUserFriendlyName(String userFriendlyName) {
        this.userFriendlyName = userFriendlyName;
    }

    public boolean isHasAlias() {
        return hasAlias;
    }

    public void setHasAlias(boolean hasAlias) {
        this.hasAlias = hasAlias;
    }
}
