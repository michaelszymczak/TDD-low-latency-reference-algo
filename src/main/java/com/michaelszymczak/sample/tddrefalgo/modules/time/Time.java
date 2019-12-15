package com.michaelszymczak.sample.tddrefalgo.modules.time;

public class Time {
    private long timeNanos;

    public Time(long timeNanos) {
        this.timeNanos = timeNanos;
    }

    long timeNanos() {
        return timeNanos;
    }

    void set(long timeNanos) {
        this.timeNanos = timeNanos;
    }
}
