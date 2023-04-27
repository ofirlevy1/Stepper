package Flow;

import Steps.StepStatistics;

import java.util.ArrayList;

public class FlowStatistics {
    private int startupCount;
    private double avgDuration;
    private String flowName;

    public FlowStatistics(int startupCount, double avgDuration, String flowName) {
        this.startupCount = startupCount;
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


    public int getStartupCount() {
        return startupCount;
    }

    public void setStartupCount(int startupCount) {
        this.startupCount = startupCount;
    }

    public double getAvgDuration() {
        return avgDuration;
    }

    public void setAvgDuration(double avgDuration) {
        this.avgDuration = avgDuration;
    }

    public String getFlowStatisticsAsString(){
        return flowName+" has run: "+startupCount+" times. For the average run time: "+avgDuration;
    }
}
