package com.michaelszymczak.sample.tddrefalgo.encoding;

import com.michaelszymczak.sample.tddrefalgo.api.App;
import com.michaelszymczak.sample.tddrefalgo.api.Output;
import com.michaelszymczak.sample.tddrefalgo.apps.samplepricing.SamplePricingApp;
import com.michaelszymczak.sample.tddrefalgo.encoding.pricingprotocol.EncodingPricingProtocolPublisher;
import org.agrona.DirectBuffer;

public class EncodingApp implements App {

    private final MessageEncoding.Decoder decoder = new MessageEncoding.Decoder();
    private final AppPublisher appPublisher = new AppPublisher();
    private final MessageConsumer messageConsumer = new MessageConsumer(new SamplePricingApp(
            new EncodingPricingProtocolPublisher(appPublisher)));

    @Override
    public int onInput(DirectBuffer input, int offset, int length) {
        decoder.wrap(input, offset).decode(length, messageConsumer);
        return offset + length;
    }

    @Override
    public Output output() {
        return appPublisher;
    }

}
