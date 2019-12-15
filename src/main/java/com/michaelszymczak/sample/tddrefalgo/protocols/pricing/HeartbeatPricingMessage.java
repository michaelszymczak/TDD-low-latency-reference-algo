package com.michaelszymczak.sample.tddrefalgo.protocols.pricing;

import static org.agrona.BitUtil.SIZE_OF_LONG;

public interface HeartbeatPricingMessage extends PricingMessage {

    static ImmutableHeartbeatPricingMessage heartbeat(long nanoTIme) {
        return new ImmutableHeartbeatPricingMessage(nanoTIme);
    }

    static MutableHeartbeatPricingMessage heartbeat() {
        return new MutableHeartbeatPricingMessage();
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
