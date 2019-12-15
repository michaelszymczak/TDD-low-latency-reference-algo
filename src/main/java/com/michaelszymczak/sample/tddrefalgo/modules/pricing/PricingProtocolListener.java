package com.michaelszymczak.sample.tddrefalgo.modules.pricing;

public interface PricingProtocolListener {

    void onHeartbeat(Heartbeat message);

    void onQuote(Quote message);
}
