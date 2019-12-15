package com.michaelszymczak.sample.tddrefalgo.framework.encoding.pricingprotocol;

import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.*;

import java.util.ArrayList;
import java.util.List;

public class PricingProtocolDecodedMessageSpy implements PricingProtocolListener {

    private List<PricingMessage> pricingMessages = new ArrayList<>();


    @Override
    public void onHeartbeat(HeartbeatPricingMessage message) {
        pricingMessages.add(new ImmutableHeartbeatPricingMessage(message));
    }

    @Override
    public void onQuote(QuotePricingMessage message) {
        pricingMessages.add(new ImmutableQuotePricingMessage(message));
    }

    public List<PricingMessage> messages() {
        return pricingMessages;
    }
}
