package com.michaelszymczak.sample.tddrefalgo.apps.middleman;

import com.michaelszymczak.sample.tddrefalgo.framework.encoding.EncodingPublisher;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.MutableHeartbeatPricingMessage;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.PricingMessage;

class ThrottledPricesPublisher {

    private final EncodingPublisher<PricingMessage> publisher;
    private final MutableHeartbeatPricingMessage heartBeatMessage = new MutableHeartbeatPricingMessage();

    ThrottledPricesPublisher(EncodingPublisher<PricingMessage> publisher) {
        this.publisher = publisher;
    }

    void publishHeartbeat(long nanoTime) {
        publisher.publish(heartBeatMessage.set(nanoTime));
    }
}
