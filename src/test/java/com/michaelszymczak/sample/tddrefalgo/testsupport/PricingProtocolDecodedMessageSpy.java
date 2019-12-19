package com.michaelszymczak.sample.tddrefalgo.testsupport;

import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.*;

import java.util.ArrayList;
import java.util.List;

public class PricingProtocolDecodedMessageSpy implements PricingProtocolListener {

    private List<PricingMessage> pricingMessages = new ArrayList<>();


    @Override
    public void onMessage(HeartbeatPricingMessage message) {
        pricingMessages.add(new ImmutableHeartbeatPricingMessage(message));
    }

    @Override
    public void onMessage(QuotePricingMessage message) {
        pricingMessages.add(new ImmutableQuotePricingMessage(message));
    }

    @Override
    public void onMessage(AckMessage message) {
        pricingMessages.add(message);
    }

    public List<PricingMessage> messages() {
        return pricingMessages;
    }

    void clear() {
        pricingMessages.clear();
    }
}
