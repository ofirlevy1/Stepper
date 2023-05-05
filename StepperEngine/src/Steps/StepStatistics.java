package Steps;

public class StepStatistics {
    private int startUpCount;
    private double avgDuration;
    private String stepName;

    public StepStatistics(int startUpCount, double avgDuration, String stepName){
        this.startUpCount=startUpCount;
        this.avgDuration=avgDuration;
        this.stepName =stepName;
    }

    public int getStartUpCount() {
        return startUpCount;
    }

    public void setStartUpCount(int startUpCount) {
        this.startUpCount = startUpCount;
    }

    public double getAvgDuration() {
        return avgDuration;
    }

    public void setAvgDuration(double avgDuration) {
        this.avgDuration = avgDuration;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public String getStepstatisticsAsString(){
        return "'" + stepName + "'" + "step has run " + startUpCount + " times, with an average run time of "+avgDuration + " ms";
    }
}
