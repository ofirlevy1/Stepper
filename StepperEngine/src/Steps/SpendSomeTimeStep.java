package Steps;

import DataTypes.DataType;

import java.util.ArrayList;


public class SpendSomeTimeStep extends Step{

    public class NumberZeroOrBelowException extends Exception{

        public NumberZeroOrBelowException(String str){
            super(str);
        }
    }

    public SpendSomeTimeStep(ArrayList<DataType> inputs, ArrayList<DataType> outputs) {
        super("TIME_TO_SPEND", true, inputs, outputs);
    }
    @Override
    public void execute(){

        try {
            this.runStepFlow();
        } catch (Exception e) {
            this.setSummaryLine("Exception: " + e.getMessage());
            this.setStatus(Status.Failure);
        }

    }

    @Override
    protected void runStepFlow() throws Exception {
        Integer sleepAmount= (Integer) inputs.get(0).getData();
        this.addLog("About to sleep for " + sleepAmount + " seconds...");
        if(sleepAmount<=0) throw new NumberZeroOrBelowException("Entered a number that is zero or below") ;
        Thread.sleep(sleepAmount*1000);
        this.addLog("Done Sleeping...");
        this.setSummaryLine("Time spent: "+sleepAmount);
        this.setStatus(Status.Success);
    }


}
