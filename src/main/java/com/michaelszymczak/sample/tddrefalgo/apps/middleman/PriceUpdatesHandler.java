package com.michaelszymczak.sample.tddrefalgo.apps.middleman;

import com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain.ThrottledPrices;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.AckMessage;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.HeartbeatPricingMessage;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.PricingProtocolListener;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.QuotePricingMessage;

class PriceUpdatesHandler implements PricingProtocolListener {

    private final ThrottledPrices throttledPrices;

    PriceUpdatesHandler(final ThrottledPrices throttledPrices) {
        this.throttledPrices = throttledPrices;
    }

    @Override
    public void onMessage(HeartbeatPricingMessage message) {
        throttledPrices.onHeartbeat(message.nanoTime());
    }

    @Override
    public void onMessage(QuotePricingMessage message) {
        if (message.priceTier() == 0 && message.bidPrice() == 0 && message.askPrice() == 0) {
            throttledPrices.onCancel(message.isin());
        } else {

            throttledPrices.onQuoteUpdate(message.isin(), message.priceTier(), message.bidPrice(), message.askPrice());
        }
    }

    @Override
    public void onMessage(AckMessage message) {
        throttledPrices.onAck();
    }
}
