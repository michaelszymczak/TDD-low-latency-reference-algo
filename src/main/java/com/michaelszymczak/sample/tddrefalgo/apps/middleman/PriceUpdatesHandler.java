package com.michaelszymczak.sample.tddrefalgo.apps.middleman;

import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.AckMessage;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.HeartbeatPricingMessage;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.PricingProtocolListener;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.QuotePricingMessage;

class PriceUpdatesHandler implements PricingProtocolListener {
    @Override
    public void onMessage(HeartbeatPricingMessage message) {
        throw new UnsupportedOperationException();
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
