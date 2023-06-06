package RunHistory;

public class OutputHistory {
    private String name;
    private String alias;
    private String type;
    private String presentableString;

    public OutputHistory(String name, String alias, String type, String presentableString) {
        this.name = name;
        this.alias=alias;
        this.type = type;
        this.presentableString = presentableString;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPresentableString() {
        return presentableString;
    }

    public void setPresentableString(String presentableString) {
        this.presentableString = presentableString;
    }
}
