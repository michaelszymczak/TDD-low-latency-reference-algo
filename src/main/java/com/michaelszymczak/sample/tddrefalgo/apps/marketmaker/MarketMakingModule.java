package com.michaelszymczak.sample.tddrefalgo.apps.marketmaker;

import com.michaelszymczak.sample.tddrefalgo.framework.encoding.EncodingPublisher;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.*;
import com.michaelszymczak.sample.tddrefalgo.supportingdomain.RelativeNanoClock;

public class MarketMakingModule implements PricingProtocolListener {

    private final RelativeNanoClock nanoClock;
    private EncodingPublisher<PricingMessage> publisher;

    MarketMakingModule(final RelativeNanoClock nanoClock) {
        this.nanoClock = nanoClock;
    }

    MarketMakingModule registerPublisher(EncodingPublisher<PricingMessage> publisher) {
        this.publisher = publisher;
        return this;
    }

    @Override
    public void onMessage(HeartbeatPricingMessage message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onMessage(QuotePricingMessage message) {
        publish(message);
    }

    @Override
    public void onMessage(AckMessage message) {
        publish(message);
    }

    void heartbeat() {
        publish(new ImmutableHeartbeatPricingMessage(nanoClock.timestampNs()));
    }

    private void publish(PricingMessage message) {
        publisher.publish(message);
    }
}
