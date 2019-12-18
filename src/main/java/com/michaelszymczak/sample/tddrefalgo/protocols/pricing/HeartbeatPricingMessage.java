package com.michaelszymczak.sample.tddrefalgo.protocols.pricing;

public interface HeartbeatPricingMessage extends PricingMessage {

    static ImmutableHeartbeatPricingMessage heartbeat(long nanoTIme) {
        return new ImmutableHeartbeatPricingMessage(nanoTIme);
    }

    static MutableHeartbeatPricingMessage heartbeat() {
        return new MutableHeartbeatPricingMessage();
    }

    @Override
    default Type type() {
        return Type.HEARTBEAT;
    }

    long nanoTime();
}
