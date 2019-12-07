package com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol;

public interface PricingMessage {

    PricingMessageType type();

    int length();
}
