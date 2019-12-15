package com.michaelszymczak.sample.tddrefalgo.encoding.pricingprotocol;

import com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol.MessageWithPricingProtocol;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol.PricingMessage;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol.PricingProtocolPublisher;
import com.michaelszymczak.sample.tddrefalgo.encoding.AppPublisher;
import com.michaelszymczak.sample.tddrefalgo.encoding.lengthbased.LengthBasedMessageEncoding;

public class EncodingPricingProtocolPublisher implements PricingProtocolPublisher {

    private final MessageWithPricingProtocol messageWithPricingProtocol = new MessageWithPricingProtocol();
    private final LengthBasedMessageEncoding.Encoder encoder;
    private final PricingProtocolEncoding.Encoder protocolEncoder;
    private final AppPublisher appPublisher;

    public EncodingPricingProtocolPublisher(PricingProtocolEncoding.Encoder protocolEncoder, AppPublisher appPublisher, LengthBasedMessageEncoding.Encoder encoder) {
        this.protocolEncoder = protocolEncoder;
        this.appPublisher = appPublisher;
        this.encoder = encoder;
    }


    @Override
    public void publish(PricingMessage message) {
        appPublisher.setWrittenPosition(encoder
                .wrap(appPublisher.buffer(), appPublisher.writtenPosition())
                .encode(protocolEncoder, messageWithPricingProtocol.withPayload(message)));
    }

}
