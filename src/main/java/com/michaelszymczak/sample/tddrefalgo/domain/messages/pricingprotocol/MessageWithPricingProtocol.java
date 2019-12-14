package com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol;

import com.michaelszymczak.sample.tddrefalgo.domain.messages.Message;

public class MessageWithPricingProtocol implements Message<PricingMessage> {

    private PricingMessage payload;

    public MessageWithPricingProtocol withPayload(PricingMessage payload) {
        this.payload = payload;
        return this;
    }

    @Override
    public PricingMessage payload() {
        return payload;
    }

    @Override
    public Class<PricingMessage> payloadType() {
        return PricingMessage.class;
    }

}