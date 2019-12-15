package com.michaelszymczak.sample.tddrefalgo.protocols.time;

public class Time {
    private long timeNanos;

    public Time(long timeNanos) {
        this.timeNanos = timeNanos;
    }

    long timeNanos() {
        return timeNanos;
    }

    public Time set(long timeNanos) {
        this.timeNanos = timeNanos;
        return this;
    }
}
