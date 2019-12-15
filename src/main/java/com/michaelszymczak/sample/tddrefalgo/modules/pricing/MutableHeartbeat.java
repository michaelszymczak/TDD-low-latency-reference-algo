package com.michaelszymczak.sample.tddrefalgo.modules.pricing;

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
