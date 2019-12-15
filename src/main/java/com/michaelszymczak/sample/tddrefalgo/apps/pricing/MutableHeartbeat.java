package com.michaelszymczak.sample.tddrefalgo.apps.pricing;

public class MutableHeartbeat implements Heartbeat {

    private long nanoTime;

    MutableHeartbeat set(long nanoTime) {
        this.nanoTime = nanoTime;
        return this;
    }

    @Override
    public long nanoTime() {
        return nanoTime;
    }
}
