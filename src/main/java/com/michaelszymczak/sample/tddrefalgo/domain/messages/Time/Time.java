package com.michaelszymczak.sample.tddrefalgo.domain.messages.Time;

public class Time {
    private long timeNanos;

    public Time(long timeNanos) {
        this.timeNanos = timeNanos;
    }

    public long timeNanos() {
        return timeNanos;
    }

    public void set(long timeNanos) {
        this.timeNanos = timeNanos;
    }
}
