package Flow;

import DataTypes.DataType;

import java.util.ArrayList;

public class StepOutputDescriptor {
    private String outputEffectiveName;
    private DataType.Type outputType;
    private String sourceStepName; // these are the EFFECTIVE names.

    public StepOutputDescriptor(String outputEffectiveName, DataType.Type outputType, String sourceStepName) {
        this.outputEffectiveName = outputEffectiveName;
        this.outputType = outputType;
        this.sourceStepName = sourceStepName;
    }

    public String getOutputEffectiveName() {
        return outputEffectiveName;
    }

    public void setOutputEffectiveName(String outputEffectiveName) {
        this.outputEffectiveName = outputEffectiveName;
    }

    public DataType.Type getOutputType() {
        return outputType;
    }

    public void setOutputType(DataType.Type outputType) {
        this.outputType = outputType;
    }

    public String getSourceStepName() {
        return sourceStepName;
    }

    public void setSourceStepName(String sourceStepsNames) {
        this.sourceStepName = sourceStepsNames;
    }
}

