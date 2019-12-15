package com.michaelszymczak.sample.tddrefalgo.protocols.pricing;

public interface PricingProtocolListener {

    void onHeartbeat(HeartbeatPricingMessage message);

    void onQuote(QuotePricingMessage message);
}
