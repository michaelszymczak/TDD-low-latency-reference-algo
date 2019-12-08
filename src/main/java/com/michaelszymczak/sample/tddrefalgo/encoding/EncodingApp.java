package com.michaelszymczak.sample.tddrefalgo.encoding;

import com.michaelszymczak.sample.tddrefalgo.api.App;
import com.michaelszymczak.sample.tddrefalgo.api.Output;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.PayloadSchema;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.plaintext.PlainTextListener;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.plaintext.PlainTextPublisher;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol.PricingProtocolListener;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol.PricingProtocolPublisher;
import com.michaelszymczak.sample.tddrefalgo.encoding.plaintext.EncodingPlainTextPublisher;
import com.michaelszymczak.sample.tddrefalgo.encoding.plaintext.PlainTextEncoding;
import com.michaelszymczak.sample.tddrefalgo.encoding.pricingprotocol.EncodingPricingProtocolPublisher;
import com.michaelszymczak.sample.tddrefalgo.encoding.pricingprotocol.PricingProtocolEncoding;
import org.agrona.DirectBuffer;

import java.util.function.Function;

public class EncodingApp implements App {

    private final MessageEncoding.Decoder appMessageDecoder = new MessageEncoding.Decoder();
    private final AppMessageConsumer consumer = new AppMessageConsumer();
    private final AppPublisher appPublisher = new AppPublisher();

    private final PricingProtocolEncoding.Decoder pricingDecoder = new PricingProtocolEncoding.Decoder();
    private final PricingProtocolListener pricingApp;
    private final PlainTextListener plainTextApp;
    private final PlainTextEncoding.Decoder textDecoder = new PlainTextEncoding.Decoder();

    public EncodingApp(
            Function<PricingProtocolPublisher, PricingProtocolListener> pricingAppFactory,
            Function<PlainTextPublisher, PlainTextListener> plainTextAppFactory
    ) {
        pricingApp = pricingAppFactory.apply(new EncodingPricingProtocolPublisher(appPublisher));
        plainTextApp = plainTextAppFactory.apply(new EncodingPlainTextPublisher(appPublisher));
    }

    @Override
    public int onInput(DirectBuffer input, int offset, int length) {
        appMessageDecoder.wrap(input, offset).decode(length, consumer);
        return offset + length;
    }

    @Override
    public Output output() {
        return appPublisher;
    }

    private class AppMessageConsumer implements DecodedAppMessageConsumer {
        @Override
        public void onMessage(PayloadSchema payloadSchema, DirectBuffer buffer, int offset, int length) {
            if (payloadSchema == PayloadSchema.PRICING) {
                pricingDecoder.wrap(buffer, offset).decode(pricingApp);
            } else if (payloadSchema == PayloadSchema.PLAIN_TEXT) {
                textDecoder.wrap(buffer, offset, length).decode(plainTextApp);
            }
        }
    }
}
