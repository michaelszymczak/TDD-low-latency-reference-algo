package com.michaelszymczak.sample.tddrefalgo.protocols.pricing;

public class AckMessage implements PricingMessage {

    public static final AckMessage ACK_MESSAGE = new AckMessage();

    @Override
    public Type type() {
        return Type.ACK;
    }

    @Override
    public String toString() {
        return "ACK";
    }
}
