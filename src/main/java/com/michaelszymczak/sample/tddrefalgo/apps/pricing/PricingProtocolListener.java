package com.michaelszymczak.sample.tddrefalgo.apps.pricing;

public interface PricingProtocolListener {

    void onHeartbeat(Heartbeat message);

    void onQuote(Quote message);
}
