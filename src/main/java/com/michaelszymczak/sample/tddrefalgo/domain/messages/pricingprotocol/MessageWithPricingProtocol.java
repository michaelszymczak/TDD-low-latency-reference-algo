package com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol;

import com.michaelszymczak.sample.tddrefalgo.domain.messages.Message;

public class MessageWithPricingProtocol implements Message<PricingMessage> {

    private PricingMessage payload;

    public MessageWithPricingProtocol withPayload(PricingMessage payload) {
        this.payload = payload;
        return this;
    }

    @Override
    public int payloadLength() {
        return payload.length();
    }

    @Override
    public PricingMessage payload() {
        return payload;
    }

}