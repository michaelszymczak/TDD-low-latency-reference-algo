package com.michaelszymczak.sample.tddrefalgo.apps.middleman;

import com.michaelszymczak.sample.tddrefalgo.framework.encoding.EncodingPublisher;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.MutableHeartbeatPricingMessage;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.PricingMessage;

public class EncodingThrottledPricesPublisher implements ThrottledPricesPublisher {

    private final EncodingPublisher<PricingMessage> publisher;
    private final MutableHeartbeatPricingMessage heartBeatMessage = new MutableHeartbeatPricingMessage();

    EncodingThrottledPricesPublisher(EncodingPublisher<PricingMessage> publisher) {
        this.publisher = publisher;
    }

    @Override
    public void publishHeartbeat(long nanoTime) {
        publisher.publish(heartBeatMessage.set(nanoTime));
    }

    @Override
    public void publishQuote(CharSequence isin, int tier, long bidPrice, long askPrice) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void publishCancel(CharSequence isin, int tier) {
        throw new UnsupportedOperationException();
    }
}