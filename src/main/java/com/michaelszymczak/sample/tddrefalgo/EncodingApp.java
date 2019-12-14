package com.michaelszymczak.sample.tddrefalgo;

import com.michaelszymczak.sample.tddrefalgo.api.App;
import com.michaelszymczak.sample.tddrefalgo.api.Output;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.plaintext.PlainTextListener;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.plaintext.PlainTextPublisher;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol.PricingProtocolListener;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol.PricingProtocolPublisher;
import com.michaelszymczak.sample.tddrefalgo.encoding.AppPublisher;
import com.michaelszymczak.sample.tddrefalgo.encoding.DecodedAppMessageConsumer;
import com.michaelszymczak.sample.tddrefalgo.encoding.lengthbased.LengthBasedMessageEncoding;
import com.michaelszymczak.sample.tddrefalgo.encoding.plaintext.EncodingPlainTextPublisher;
import com.michaelszymczak.sample.tddrefalgo.encoding.plaintext.PlainTextEncoding;
import com.michaelszymczak.sample.tddrefalgo.encoding.pricingprotocol.EncodingPricingProtocolPublisher;
import com.michaelszymczak.sample.tddrefalgo.encoding.pricingprotocol.PricingProtocolEncoding;
import org.agrona.DirectBuffer;

import java.util.function.Function;

public class EncodingApp implements App {

    private final LengthBasedMessageEncoding.Decoder decoder;
    private final AppMessageConsumer consumer = new AppMessageConsumer();
    private final AppPublisher appPublisher = new AppPublisher();


    private final PricingProtocolListener pricingApp;
    private final PlainTextListener plainTextApp;


    EncodingApp(
            Function<PricingProtocolPublisher, PricingProtocolListener> pricingAppFactory,
            Function<PlainTextPublisher, PlainTextListener> plainTextAppFactory,
            LengthBasedMessageEncoding.Encoder encoder,
            LengthBasedMessageEncoding.Decoder decoder
    ) {
        pricingApp = pricingAppFactory.apply(new EncodingPricingProtocolPublisher(appPublisher, encoder));
        plainTextApp = plainTextAppFactory.apply(new EncodingPlainTextPublisher(appPublisher, encoder));
        this.decoder = decoder;
    }

    @Override
    public int onInput(DirectBuffer input, int offset, int length) {
        decoder.wrap(input, offset).decode(length, consumer);
        return offset + length;
    }

    @Override
    public Output output() {
        return appPublisher;
    }

    private class AppMessageConsumer implements DecodedAppMessageConsumer {

        private final PricingProtocolEncoding.Decoder pricingDecoder = new PricingProtocolEncoding.Decoder();
        private final PlainTextEncoding.Decoder textDecoder = new PlainTextEncoding.Decoder();

        @Override
        public void onMessage(short payloadSchemaId, DirectBuffer buffer, int offset, int length) {
            if (payloadSchemaId == Setup.SupportedPayloadSchemas.PRICING.id()) {
                pricingDecoder.wrap(buffer, offset).decode(pricingApp);
            } else if (payloadSchemaId == Setup.SupportedPayloadSchemas.PLAIN_TEXT.id()) {
                textDecoder.wrap(buffer, offset, length).decode(plainTextApp);
            }
        }
    }
}
