package com.michaelszymczak.sample.tddrefalgo.protocols.pricing;

public interface PricingMessage {

    Type type();

    enum Type {
        HEARTBEAT,
        QUOTE,
        ACK,

    }
}
