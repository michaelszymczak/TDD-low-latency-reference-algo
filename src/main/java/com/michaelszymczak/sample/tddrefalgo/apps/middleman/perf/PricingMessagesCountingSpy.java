package com.michaelszymczak.sample.tddrefalgo.apps.middleman.perf;

import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.AckMessage;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.HeartbeatPricingMessage;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.PricingProtocolListener;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.QuotePricingMessage;

public class PricingMessagesCountingSpy implements PricingProtocolListener {
    private int receivedMessages = 0;

    @Override
    public void onMessage(HeartbeatPricingMessage message) {
        receivedMessages++;
    }

    @Override
    public void onMessage(QuotePricingMessage message) {
        receivedMessages++;
    }

    @Override
    public void onMessage(AckMessage message) {
        receivedMessages++;
    }

    public int receivedMessagesCount() {
        return receivedMessages;
    }
}
