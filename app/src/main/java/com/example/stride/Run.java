package com.example.stride;

import java.util.HashMap;

public class Run {
    public enum MetricsName {
        HUNDREDTH_SECS, // In hundredth of seconds
        DISTANCE, // In meters
        HEIGHT, // In meters
        PACE, // In hundredth of seconds per meters
        CALORIES
    }

    protected long hundredthSecs = 0;
    protected long distance = 0;
    protected long height = 0;
    protected long pace = 0;
    protected long calories = 0;
    protected String date;


    public long getHundredthSecs() {
        return hundredthSecs;
    }

    public long getDistance() {
        return distance;
    }

    public long getHeight() {
        return height;
    }

    public long getPace() {
        return pace;
    }

    public long getCalories() {
        return calories;
    }

    public String getDate() {
        return date;
    }

    public void setHundredthSecs(long hundredthSecs) {
        this.hundredthSecs = hundredthSecs;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public void setPace(long pace) {
        this.pace = pace;
    }

    public void setCalories(long calories) {
        this.calories = calories;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Run() {}

    public Run(String date) {
        this.date = date;
    }
}
