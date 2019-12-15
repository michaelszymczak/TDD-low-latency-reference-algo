package com.michaelszymczak.sample.tddrefalgo.apps.pricing;

import java.util.Objects;

public class ImmutableHeartbeat implements Heartbeat {

    private final long nanoTime;

    public ImmutableHeartbeat(long nanoTime) {
        this.nanoTime = nanoTime;
    }

    public ImmutableHeartbeat(Heartbeat heartbeat) {
        this(heartbeat.nanoTime());
    }

    @Override
    public long nanoTime() {
        return nanoTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImmutableHeartbeat that = (ImmutableHeartbeat) o;
        return nanoTime == that.nanoTime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nanoTime);
    }

    @Override
    public String toString() {
        return "ImmutableHeartbeat{" +
                "nanoTime=" + nanoTime +
                '}';
    }
}
