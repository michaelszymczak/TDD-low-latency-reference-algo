package com.michaelszymczak.sample.tddrefalgo.apps.pricing;

import com.michaelszymczak.sample.tddrefalgo.encoding.EncodingPublisher;

public class SamplePricingApp implements PricingProtocolListener {

    private final EncodingPublisher<PricingMessage> publisher;

    public SamplePricingApp(EncodingPublisher<PricingMessage> publisher) {
        this.publisher = publisher;
    }

    @Override
    public void onHeartbeat(Heartbeat message) {
        publisher.publish(message);
    }

    @Override
    public void onQuote(Quote message) {

    }
}
