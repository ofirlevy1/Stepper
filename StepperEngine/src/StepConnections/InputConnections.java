package StepConnections;

public class InputConnections {
    private String inputAliasName;
    private String inputName;
    private boolean hasAlias;
    private String connectedOutputName;
    private String connectedStepName;

    public InputConnections(String inputAliasNameName, boolean hasAlias, String inputName, String connectedOutputName, String connectedStepName){
        this.inputAliasName =inputAliasNameName;
        this.hasAlias=hasAlias;
        this.inputName=inputName;
        this.connectedOutputName=connectedOutputName;
        this.connectedStepName=connectedStepName;
    }

    public String getInputAliasName() {
        return inputAliasName;
    }

    public void setInputAliasName(String inputAliasName) {
        this.inputAliasName = inputAliasName;
    }

    public String getConnectedOutputName() {
        return connectedOutputName;
    }

    public void setConnectedOutputName(String connectedOutputName) {
        this.connectedOutputName = connectedOutputName;
    }

    public String getConnectedStepName() {
        return connectedStepName;
    }

    public void setConnectedStepName(String connectedStepName) {
        this.connectedStepName = connectedStepName;
    }

    public String getInputName() {
        return inputName;
    }

    public void setInputName(String inputName) {
        this.inputName = inputName;
    }

    public boolean isHasAlias() {
        return hasAlias;
    }

    public void setHasAlias(boolean hasAlias) {
        this.hasAlias = hasAlias;
    }
}
