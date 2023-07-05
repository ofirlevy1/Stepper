package Flow;

import java.util.ArrayList;

public class FlowStatistics {
    private ArrayList<Long> runDurations;
    private String flowName;

    public FlowStatistics() {
        runDurations = new ArrayList<>();
    }
    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public int getStartUpCount() {
        return runDurations.size();
    }

    public long getAvgDuration() {
        long sum = 0;
        for(long duration : runDurations)
            sum += duration;
        return sum / (long)runDurations.size();
    }

    public String getFlowStatisticsAsString(){
        return "'" + flowName + "' flow has run "+ getStartUpCount() +" times, with an average run time of " + getAvgDuration() + " ms";
    }

    public void addRunDurations(ArrayList<Long> runDurationsToAdd) {
        runDurations.addAll(runDurationsToAdd);
    }
}
