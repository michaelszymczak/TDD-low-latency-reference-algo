package com.michaelszymczak.sample.tddrefalgo.encoding.pricingprotocol;

import com.michaelszymczak.sample.tddrefalgo.encoding.AppPublisher;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol.*;
import com.michaelszymczak.sample.tddrefalgo.encoding.MessageEncoding;

public class EncodingPricingProtocolPublisher implements PricingProtocolPublisher {

    private final MessageWithPricingProtocol messageWithPricingProtocol = new MessageWithPricingProtocol();
    private final MessageEncoding.Encoder encoder = new MessageEncoding.Encoder();
    private final AppPublisher appPublisher;

    public EncodingPricingProtocolPublisher(AppPublisher appPublisher) {

        this.appPublisher = appPublisher;
    }


    @Override
    public void publish(PricingMessage message) {
        appPublisher.setWrittenPosition(encoder.wrap(appPublisher.buffer(), appPublisher.writtenPosition())
                        .encode(messageWithPricingProtocol.withPayload(message)));
    }
}
