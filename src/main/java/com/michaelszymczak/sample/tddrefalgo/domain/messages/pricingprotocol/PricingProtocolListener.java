package com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol;

public interface PricingProtocolListener {

    void onHeartbeat(Heartbeat message);

    void onQuote(Quote message);
}
