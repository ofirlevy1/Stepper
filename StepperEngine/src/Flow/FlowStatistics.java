package Flow;

public class FlowStatistics {
    private int startUpCount;
    private double avgDuration;
    private String flowName;

    public FlowStatistics(int startupCount, double avgDuration, String flowName) {
        this.startUpCount = startupCount;
        this.avgDuration = avgDuration;
        this.flowName = flowName;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public FlowStatistics(){}


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

    public String getFlowStatisticsAsString(){
        return "'" + flowName + "' flow has run "+ startUpCount +" times, with an average run time of " + avgDuration + " ms";
    }
}
