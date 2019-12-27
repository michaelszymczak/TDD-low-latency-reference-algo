package com.michaelszymczak.sample.tddrefalgo.apps.middleman;

import com.michaelszymczak.sample.tddrefalgo.framework.encoding.EncodingPublisher;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.*;

class PriceUpdatesHandler implements PricingProtocolListener {

    private final ThrottledPrices throttledPrices;

    PriceUpdatesHandler(EncodingPublisher<PricingMessage> publisher, final int windowSize) {
        this.throttledPrices = new ThrottledPrices(new EncodingThrottledPricesPublisher(publisher), windowSize);
    }

    @Override
    public void onMessage(HeartbeatPricingMessage message) {
        throttledPrices.onHeartbeat(message.nanoTime());
    }

    @Override
    public void onMessage(QuotePricingMessage message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onMessage(AckMessage message) {
        throw new UnsupportedOperationException();
    }
}