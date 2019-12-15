package com.michaelszymczak.sample.tddrefalgo.protocols.pricing;

import com.michaelszymczak.sample.tddrefalgo.framework.encoding.EncodingPublisher;

public class SamplePricingApp implements PricingProtocolListener {

    private final EncodingPublisher<PricingMessage> publisher;

    public SamplePricingApp(EncodingPublisher<PricingMessage> publisher) {
        this.publisher = publisher;
    }

    @Override
    public void onHeartbeat(HeartbeatPricingMessage message) {
        publisher.publish(message);
    }

    @Override
    public void onQuote(QuotePricingMessage message) {

    }
}
