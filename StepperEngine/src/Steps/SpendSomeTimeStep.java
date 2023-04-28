package Steps;

import DataTypes.DataType;
import DataTypes.ListType;
import DataTypes.NumberType;

import java.util.ArrayList;
import java.util.List;


public class SpendSomeTimeStep extends Step{
    private NumberType secondsToSpend;
    private static double stepAvgDuration=0;
    private static int stepStartUpCount=0;

    public SpendSomeTimeStep(){
        super("Spend Some Time", true);

        this.secondsToSpend = new NumberType(StepInputNameEnum.TIME_TO_SPEND.toString(), true);
        secondsToSpend.setMandatory(true);
    }

    public SpendSomeTimeStep(NumberType secondsToSpend) {
        this();
        this.secondsToSpend = secondsToSpend;
        this.secondsToSpend.setMandatory(true);
    }

    @Override
    protected void outerRunStepFlow(){
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
            if(input.getEffectiveName().equals(secondsToSpend.getEffectiveName())) {
                this.secondsToSpend.setData((Integer) input.getData());
                this.secondsToSpend.setMandatory(true);
            }
        }
    }

    @Override
    public void setInputByName(DataType input, String inputName) {
        if(inputName.equals(secondsToSpend.getEffectiveName()))
            this.secondsToSpend.setData((Integer) input.getData());
    }

    @Override
    public ArrayList<DataType> getOutputs(String... outputNames) {
        return null;
    }

    @Override
    public ArrayList<DataType> getAllData() {
        ArrayList<DataType> allData=new ArrayList<>();
        allData.add(this.secondsToSpend);
        return  allData;
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

    @Override
    protected void updateStaticTimers() {
        stepStartUpCount= startUpCounter;
        stepAvgDuration=durationAvgInMs;
    }

    public static int getStepStartUpCount() {
        return stepStartUpCount;
    }

    public static double getStepAvgDuration() {
        return stepAvgDuration;
    }
}
