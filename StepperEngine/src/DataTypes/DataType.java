package DataTypes;

public abstract class DataType<T> {
    private String name;
    private String alias;
    private String userFriendlyName;
    private boolean hasAlias;
    boolean isMandatory;

    boolean isInput;

    private T data;

    // important for input datatype since only 1 output can be assigned to them.
    boolean isAssigned;

    boolean isDataSet;    // true if the "Data" member is set (not null...);

    public enum Type{
        DOUBLE, FILE, LIST, MAPPING, NUMBER, RELATION, STRING
    }

    private Type type;


    public DataType(String name, String userFriendlyName, Type type, boolean isInput) {
        this.userFriendlyName = userFriendlyName;
        this.name = name;
        hasAlias = false;
        isAssigned = false;
        this.isInput = isInput;
        this.type=type;
    }

    public DataType(String name, String userFriendlyName, T data, Type type, boolean isInput) {
        this(name, userFriendlyName, type, isInput);
        setData(data);
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

    public boolean isDataSet() {
        return isDataSet;
    }

    public void setData(T data) {
        this.data = data;
        isDataSet = true;
    }

    public void eraseData(){
        this.data=null;
        isDataSet=false;
    }

    public String getEffectiveName() {
        return hasAlias ? alias : name;
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

    public boolean isInput() {
        return isInput;
    }

}
