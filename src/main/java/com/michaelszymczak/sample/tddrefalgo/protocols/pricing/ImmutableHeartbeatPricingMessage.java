package com.michaelszymczak.sample.tddrefalgo.protocols.pricing;

import java.util.Objects;

public class ImmutableHeartbeatPricingMessage implements HeartbeatPricingMessage {

    private final long nanoTime;

    public ImmutableHeartbeatPricingMessage(long nanoTime) {
        this.nanoTime = nanoTime;
    }

    public ImmutableHeartbeatPricingMessage(HeartbeatPricingMessage heartbeatPricingMessage) {
        this(heartbeatPricingMessage.nanoTime());
    }

    @Override
    public long nanoTime() {
        return nanoTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImmutableHeartbeatPricingMessage that = (ImmutableHeartbeatPricingMessage) o;
        return nanoTime == that.nanoTime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nanoTime);
    }

    @Override
    public String toString() {
        return "ImmutableHeartbeatPricingMessage{" +
                "nanoTime=" + nanoTime +
                '}';
    }
}
