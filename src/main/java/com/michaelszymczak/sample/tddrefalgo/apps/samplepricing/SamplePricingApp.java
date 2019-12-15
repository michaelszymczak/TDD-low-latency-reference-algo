package com.michaelszymczak.sample.tddrefalgo.apps.samplepricing;

import com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol.Heartbeat;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol.PricingMessage;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol.PricingProtocolListener;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol.Quote;
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
