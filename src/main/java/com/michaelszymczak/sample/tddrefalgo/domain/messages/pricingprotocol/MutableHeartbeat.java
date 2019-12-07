package com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol;

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
