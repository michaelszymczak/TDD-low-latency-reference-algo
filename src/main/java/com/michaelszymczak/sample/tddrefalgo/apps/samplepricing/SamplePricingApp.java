package com.michaelszymczak.sample.tddrefalgo.apps.samplepricing;

import com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol.Heartbeat;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol.PricingProtocolListener;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol.PricingProtocolPublisher;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol.Quote;

public class SamplePricingApp implements PricingProtocolListener {

    private PricingProtocolPublisher output;

    public SamplePricingApp(PricingProtocolPublisher output) {
        this.output = output;
    }

    @Override
    public void onHeartbeat(Heartbeat message) {
        output.publish(message);
    }

    @Override
    public void onQuote(Quote message) {

    }
}
