package DataTypes;

public abstract class DataType<T> {

    // user friendly means it can be given by the user.
    private boolean userFriendly;
    private String name;
    private String alias;
    private String userFriendlyName;
    private boolean hasAlias;
    boolean isMandatory;
    protected T data;


    public DataType(String name, String userFriendlyName, boolean userFriendly) {
        this.userFriendly = userFriendly;
        this.userFriendlyName = userFriendlyName;
        this.name = name;
        hasAlias = false;
    }

    public DataType(String name, String userFriendlyName, boolean userFriendly, T data) {
        this(name, userFriendlyName, userFriendly);
        this.data = data;
    }

    public void setMandatory(boolean mandatory) {
        isMandatory = mandatory;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public abstract String getPresentableString();

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

    public boolean hasAlias() {
        return hasAlias;
    }

    public void setHasAlias(boolean hasAlias) {
        this.hasAlias = hasAlias;
    }
}
