package com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol;

public enum PricingMessageType {
    HEARTBEAT('H'),
    QUOTE('Q');

    private final char type;

    PricingMessageType(char type) {
        this.type = type;
    }

    public char charType() {
        return type;
    }
}
