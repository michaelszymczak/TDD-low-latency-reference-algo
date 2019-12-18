package com.michaelszymczak.sample.tddrefalgo.apps.fooapp;

import com.michaelszymczak.sample.tddrefalgo.framework.encoding.EncodingPublisher;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.*;

public class SamplePricingModule implements PricingProtocolListener {

    private final EncodingPublisher<PricingMessage> publisher;

    public SamplePricingModule(EncodingPublisher<PricingMessage> publisher) {
        this.publisher = publisher;
    }

    @Override
    public void onMessage(HeartbeatPricingMessage message) {
        publisher.publish(message);
    }

    @Override
    public void onMessage(QuotePricingMessage message) {

    }

    @Override
    public void onMessage(AckMessage message) {

    }
}
