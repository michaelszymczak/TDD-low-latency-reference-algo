package com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol;

public interface Heartbeat extends PricingMessage {

    static ImmutableHeartbeat heartbeat(long nanoTIme) {
        return new ImmutableHeartbeat(nanoTIme);
    }

    static MutableHeartbeat heartbeat() {
        return new MutableHeartbeat();
    }

    @Override
    default PricingMessageType type() {
        return PricingMessageType.HEARTBEAT;
    }

    long nanoTime();
}
