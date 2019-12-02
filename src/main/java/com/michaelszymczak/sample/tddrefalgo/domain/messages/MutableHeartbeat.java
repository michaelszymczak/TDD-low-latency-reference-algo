package com.michaelszymczak.sample.tddrefalgo.domain.messages;

public class MutableHeartbeat implements Heartbeat {

    private long nanoTime;

    public MutableHeartbeat set(long nanoTime) {
        this.nanoTime = nanoTime;
        return this;
    }

    @Override
    public long nanoTime() {
        return nanoTime;
    }
}
