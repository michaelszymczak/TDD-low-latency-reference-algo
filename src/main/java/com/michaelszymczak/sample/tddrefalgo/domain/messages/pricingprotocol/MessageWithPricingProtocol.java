package com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol;

import com.michaelszymczak.sample.tddrefalgo.domain.messages.Message;

public class MessageWithPricingProtocol implements Message<PricingMessage> {

    private PricingMessage payload;

    public MessageWithPricingProtocol(PricingMessage payload) {
        this.payload = payload;
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