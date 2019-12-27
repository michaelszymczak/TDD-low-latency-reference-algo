package com.michaelszymczak.sample.tddrefalgo.apps.middleman;

import com.michaelszymczak.sample.tddrefalgo.framework.encoding.EncodingPublisher;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.*;

class PriceUpdatesHandler implements PricingProtocolListener {

    private final EncodingPublisher<PricingMessage> publisher;

    PriceUpdatesHandler(EncodingPublisher<PricingMessage> publisher) {

        this.publisher = publisher;
    }

    @Override
    public void onMessage(HeartbeatPricingMessage message) {
        publisher.publish(message);
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
