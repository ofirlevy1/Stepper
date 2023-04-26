package Steps;

public class StepDescriptor {
    private String stepName;
    private String stepAlias;
    private boolean hasAlias;
    private boolean isReadOnly;

    public StepDescriptor(String stepName, String stepAlias, boolean hasAlias, boolean isReadOnly) {
        this.stepName = stepName;
        this.stepAlias = stepAlias;
        this.hasAlias = hasAlias;
        this.isReadOnly = isReadOnly;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public String getStepAlias() {
        return stepAlias;
    }

    public void setStepAlias(String stepAlias) {
        this.stepAlias = stepAlias;
    }

    public boolean isHasAlias() {
        return hasAlias;
    }

    public void setHasAlias(boolean hasAlias) {
        this.hasAlias = hasAlias;
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    public void setReadOnly(boolean readOnly) {
        isReadOnly = readOnly;
    }
}
