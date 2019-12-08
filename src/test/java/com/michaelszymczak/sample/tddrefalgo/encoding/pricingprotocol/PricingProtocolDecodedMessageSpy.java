package com.michaelszymczak.sample.tddrefalgo.encoding.pricingprotocol;

import com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol.*;

import java.util.ArrayList;
import java.util.List;

public class PricingProtocolDecodedMessageSpy implements PricingProtocolListener {

    private List<PricingMessage> pricingMessages = new ArrayList<>();


    @Override
    public void onHeartbeat(Heartbeat message) {
        pricingMessages.add(new ImmutableHeartbeat(message));
    }

    @Override
    public void onQuote(Quote message) {
        pricingMessages.add(new ImmutableQuote(message));
    }

    public List<PricingMessage> messages() {
        return pricingMessages;
    }
}
