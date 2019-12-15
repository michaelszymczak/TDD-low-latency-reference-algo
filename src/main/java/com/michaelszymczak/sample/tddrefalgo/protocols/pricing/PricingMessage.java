package com.michaelszymczak.sample.tddrefalgo.protocols.pricing;

public interface PricingMessage {

    PricingMessageType type();

    int length();
}
