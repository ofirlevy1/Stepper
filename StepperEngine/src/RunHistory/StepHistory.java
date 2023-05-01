package RunHistory;

import Steps.Step;

public class StepHistory {
    private String name;
    private double runTimeInMs;
    private Step.Status status;
    private String summery;
    private String logs;

    public StepHistory(String name, double runTimeInMs, Step.Status status, String summery, String logs) {
        this.name = name;
        this.runTimeInMs = runTimeInMs;
        this.status = status;
        this.summery = summery;
        this.logs = logs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRunTimeInMs() {
        return runTimeInMs;
    }

    public void setRunTimeInMs(double runTimeInMs) {
        this.runTimeInMs = runTimeInMs;
    }

    public Step.Status getStatus() {
        return status;
    }

    public void setStatus(Step.Status status) {
        this.status = status;
    }

    public String getSummery() {
        return summery;
    }

    public void setSummery(String summery) {
        this.summery = summery;
    }

    public String getLogs() {
        return logs;
    }

    public void setLogs(String logs) {
        this.logs = logs;
    }
}
