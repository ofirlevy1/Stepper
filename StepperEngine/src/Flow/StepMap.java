package Flow;

import DataTypes.DataType;
import Steps.Step;

public class StepMap {
    private String sourceStepName;
    private String sourceDataName;
    private String TargetStepName;
    private String targetDataName;

    public StepMap(String sourceStepName, String sourceDataName, String targetStepName, String targetDataName) {
        this.sourceStepName = sourceStepName;
        this.sourceDataName = sourceDataName;
        TargetStepName = targetStepName;
        this.targetDataName = targetDataName;
    }

    public String getSourceStepName() {
        return sourceStepName;
    }

    public String getSourceDataName() {
        return sourceDataName;
    }

    public String getTargetStepName() {
        return TargetStepName;
    }

    public String getTargetDataName() {
        return targetDataName;
    }
}
