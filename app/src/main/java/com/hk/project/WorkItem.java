package com.hk.project;

public class WorkItem {
    private String description;
    private String job;
    private String day;
    private String startTime;
    private String endTime;
    private String workingTime;

    private Boolean isExpanded;
    public WorkItem(String description, String job, String day, String startTime, String endTime, String workingTime) {
        this.description = description;
        this.job = job;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.workingTime = workingTime;
        this.isExpanded = false;
    }
    public String getDescription() {
        return description;
    }

    public String getJob() {
        return job;
    }

    public String getDay() {
        return day;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getWorkingTime() {
        return workingTime;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}
