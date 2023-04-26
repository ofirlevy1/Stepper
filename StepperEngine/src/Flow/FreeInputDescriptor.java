package Flow;

import DataTypes.DataType;

import java.util.ArrayList;

public class FreeInputDescriptor {
    private String inputEffectiveName;

    private String inputUserFriendlyName;
    private DataType.Type inputType;
    private ArrayList<String> associatedSteps; //These are the EFFECTIVE names of the steps
    private boolean isMandatory;

    public FreeInputDescriptor(String inputEffectiveName, String inputUserFriendlyName, DataType.Type inputType, boolean isMandatory) {
        this.inputUserFriendlyName = inputUserFriendlyName;
        this.inputEffectiveName = inputEffectiveName;
        this.inputType = inputType;
        this.associatedSteps = new ArrayList<>();
        this.isMandatory = isMandatory;
    }

    public String getInputEffectiveName() {
        return inputEffectiveName;
    }

    public void setInputEffectiveName(String inputEffectiveName) {
        this.inputEffectiveName = inputEffectiveName;
    }

    public DataType.Type getInputType() {
        return inputType;
    }

    public void setInputType(DataType.Type inputType) {
        this.inputType = inputType;
    }

    public ArrayList<String> getAssociatedSteps() {
        return associatedSteps;
    }

    public void setAssociatedSteps(ArrayList<String> associatedSteps) {
        this.associatedSteps = associatedSteps;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public void setMandatory(boolean mandatory) {
        isMandatory = mandatory;
    }
}
