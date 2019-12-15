package com.michaelszymczak.sample.tddrefalgo.apps.pricing;

import static org.agrona.BitUtil.SIZE_OF_LONG;

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

    @Override
    default int length() {
        return SIZE_OF_LONG;
    }

    long nanoTime();
}
