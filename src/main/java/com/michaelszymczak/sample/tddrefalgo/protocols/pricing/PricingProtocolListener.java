package com.michaelszymczak.sample.tddrefalgo.protocols.pricing;

public interface PricingProtocolListener {

    void onMessage(HeartbeatPricingMessage message);

    void onMessage(QuotePricingMessage message);

    void onMessage(AckMessage message);
}
