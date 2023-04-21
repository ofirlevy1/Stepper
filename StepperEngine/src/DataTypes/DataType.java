package DataTypes;

public abstract class DataType<T> {

    // user-friendly means it can be given by the user.
    private boolean userFriendly;
    private String name;
    private String alias;
    private String userFriendlyName;
    private boolean hasAlias;
    boolean isMandatory;

    boolean isInput;

    protected T data;

    // important for input datatype since only 1 output can be assigned to them.
    boolean isAssigned;

    public enum Type{
        DOUBLE, FILE, LIST, MAPPING, NUMBER, RELATION, STRING
    }

    private Type type;


    public DataType(String name, String userFriendlyName, boolean userFriendly,Type type, boolean isInput) {
        this.userFriendly = userFriendly;
        this.userFriendlyName = userFriendlyName;
        this.name = name;
        hasAlias = false;
        isAssigned = false;
        this.isInput = isInput;
    }

    public DataType(String name, String userFriendlyName, boolean userFriendly, T data, Type type, boolean isInput) {
        this(name, userFriendlyName, userFriendly, type, isInput);
        this.data = data;
    }

    public void setMandatory(boolean mandatory) {
        isMandatory = mandatory;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
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

    public boolean isAssigned() {
        return isAssigned;
    }

    public void setAssigned(boolean assigned) {
        isAssigned = assigned;
    }
}
