package Steps;

import DataTypes.StepDataType;

import java.util.ArrayList;


public class SpendSomeTimeStep extends Step{
    public SpendSomeTimeStep(ArrayList<StepDataType> inputs, ArrayList<StepDataType> outputs) {
        super("TIME_TO_SPEND", true, inputs, outputs);
    }
    @Override
    public void execute(){
        Integer sleepAmount= (Integer) inputs.get(0).getData();
        this.addLog("About to sleep for " + sleepAmount + " seconds...");
        try {
            if(sleepAmount<=0) throw new Exception() ;
            Thread.sleep(sleepAmount*1000);
            this.addLog("Done Sleeping...");
            this.setSummaryLine("Time spent: "+sleepAmount);
            this.setStatus(StepStatus.Success);
        } catch (Exception e) {
            this.setSummaryLine("A number below zero or zero have been entered as input");
            this.setStatus(StepStatus.Failure);
        }

    }
}
