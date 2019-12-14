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
    private final AppMessageConsumer consumer;
    private final AppPublisher appPublisher = new AppPublisher();


    EncodingApp(
            Function<PricingProtocolPublisher, PricingProtocolListener> pricingAppFactory,
            Function<PlainTextPublisher, PlainTextListener> plainTextAppFactory,
            LengthBasedMessageEncoding.Encoder encoder
    ) {
        this.decoder = new LengthBasedMessageEncoding.Decoder();
        consumer = new AppMessageConsumer(
                new RegisteredApp<>(Setup.SupportedPayloadSchemas.PRICING.id(), new PricingProtocolEncoding.Decoder(), pricingAppFactory.apply(new EncodingPricingProtocolPublisher(appPublisher, encoder))),
                new RegisteredApp<>(Setup.SupportedPayloadSchemas.PLAIN_TEXT.id(), new PlainTextEncoding.Decoder(), plainTextAppFactory.apply(new EncodingPlainTextPublisher(appPublisher, encoder)))
        );
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

    private static class AppMessageConsumer implements DecodedAppMessageConsumer {

        private final RegisteredApp<PricingProtocolEncoding.Decoder, PricingProtocolListener> registeredPricingApp;
        private final RegisteredApp<PlainTextEncoding.Decoder, PlainTextListener> registeredPlainTextApp;

        private AppMessageConsumer(RegisteredApp<PricingProtocolEncoding.Decoder, PricingProtocolListener> registeredPricingApp, RegisteredApp<PlainTextEncoding.Decoder, PlainTextListener> registeredPlainTextApp) {
            this.registeredPricingApp = registeredPricingApp;
            this.registeredPlainTextApp = registeredPlainTextApp;
        }

        @Override
        public void onMessage(short payloadSchemaId, DirectBuffer buffer, int offset, int length) {
            if (payloadSchemaId == registeredPricingApp.getProtocolSchemaId()) {
                registeredPricingApp.getProtocolDecoder().wrap(buffer, offset, length).decode(registeredPricingApp.getDecodedMessageListener());
            } else if (payloadSchemaId == registeredPlainTextApp.getProtocolSchemaId()) {
                registeredPlainTextApp.getProtocolDecoder().wrap(buffer, offset, length).decode(registeredPlainTextApp.getDecodedMessageListener());
            }
        }
    }

}
