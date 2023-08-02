package com.hk.project;

public class JobDetails {
    private int id;
    private String job;
    private String description;
    private String hourlyRate;
    private String holidayRate;
    private boolean isDefaultTask;

    public JobDetails(int id, String job, String description, String hourlyRate, String holidayRate, boolean isDefaultTask) {
        this.id = id;
        this.job = job;
        this.description = description;
        this.hourlyRate = hourlyRate;
        this.holidayRate = holidayRate;
        this.isDefaultTask = isDefaultTask;
    }

    public int getId() {
        return id;
    }

    public String getJob() {
        return job;
    }

    public String getDescription() {
        return description;
    }

    public String getHourlyRate() {
        return hourlyRate;
    }

    public String getHolidayRate() {
        return holidayRate;
    }

    public boolean isDefaultTask() {
        return isDefaultTask;
    }
}
