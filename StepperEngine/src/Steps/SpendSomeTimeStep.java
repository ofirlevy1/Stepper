package Steps;

import DataTypes.DataType;
import DataTypes.ListType;
import DataTypes.NumberType;

import java.util.ArrayList;
import java.util.List;


public class SpendSomeTimeStep extends Step{
    private NumberType secondsToSpend;

    public SpendSomeTimeStep(){
        super("Spend Some Time", true);
    }

    public SpendSomeTimeStep(NumberType secondsToSpend) {
        this();
        this.secondsToSpend = secondsToSpend;
        this.secondsToSpend.setMandatory(true);
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
        Integer sleepAmount= secondsToSpend.getData();
        this.addLog("About to sleep for " + sleepAmount + " seconds...");
        if(sleepAmount<=0) throw new NumberZeroOrBelowException("Entered a number that is zero or below") ;
        Thread.sleep(sleepAmount*1000);
        this.addLog("Done Sleeping...");
        this.setSummaryLine("Time spent: "+sleepAmount);
        this.setStatus(Status.Success);
    }

    @Override
    public void setInputs(DataType... inputs) {
        for(DataType input: inputs){
            if(input.getEffectiveName().equals(StepInputNameEnum.TIME_TO_SPEND.toString())) {
                this.secondsToSpend = (NumberType) input;
                this.secondsToSpend.setMandatory(true);
            }
        }
    }

    @Override
    public ArrayList<DataType> getOutputs(String... outputNames) {
        return null;
    }


    // Not sure about this. maybe it's better to return an empty List<DataType>,
    // or do something else. overriding this and returning NULL might smell a bit.
 //   @Override
 //   public List<DataType> getOutputs() {
 //       return null;
 //   }

    public class NumberZeroOrBelowException extends Exception{
        public NumberZeroOrBelowException(String str){
            super(str);
        }
    }
}
