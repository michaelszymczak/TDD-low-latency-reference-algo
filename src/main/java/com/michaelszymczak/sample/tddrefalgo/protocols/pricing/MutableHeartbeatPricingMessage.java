package com.michaelszymczak.sample.tddrefalgo.protocols.pricing;

public class MutableHeartbeatPricingMessage implements HeartbeatPricingMessage {

    private long nanoTime;

    public MutableHeartbeatPricingMessage set(long nanoTime) {
        this.nanoTime = nanoTime;
        return this;
    }

    @Override
    public long nanoTime() {
        return nanoTime;
    }
}
