package com.michaelszymczak.sample.tddrefalgo;

import com.michaelszymczak.sample.tddrefalgo.apps.plaintext.EchoApp;
import com.michaelszymczak.sample.tddrefalgo.apps.samplepricing.SamplePricingApp;
import com.michaelszymczak.sample.tddrefalgo.encoding.PayloadSchema;
import com.michaelszymczak.sample.tddrefalgo.encoding.lengthbased.LengthBasedMessageEncoding;
import com.michaelszymczak.sample.tddrefalgo.encoding.plaintext.EncodingPlainTextPublisher;
import com.michaelszymczak.sample.tddrefalgo.encoding.plaintext.PlainTextEncoding;
import com.michaelszymczak.sample.tddrefalgo.encoding.pricingprotocol.EncodingPricingProtocolPublisher;
import com.michaelszymczak.sample.tddrefalgo.encoding.pricingprotocol.PricingProtocolEncoding;

import static com.michaelszymczak.sample.tddrefalgo.Setup.SupportedPayloadSchemas.PRICING;
import static java.util.Arrays.asList;

class Setup {

    static EncodingApp createApp() {
        return new EncodingApp(
                new LengthBasedMessageEncoding.Decoder(),
                asList(
                        appPublisher -> new RegisteredApp<>(
                                PRICING,
                                new PricingProtocolEncoding.Decoder(),
                                new SamplePricingApp(
                                        new EncodingPricingProtocolPublisher(
                                                new PricingProtocolEncoding.Encoder(PRICING),
                                                appPublisher,
                                                new LengthBasedMessageEncoding.Encoder()))),
                        appPublisher -> new RegisteredApp<>(
                                SupportedPayloadSchemas.PLAIN_TEXT,
                                new PlainTextEncoding.Decoder(),
                                new EchoApp(
                                        new EncodingPlainTextPublisher(
                                                new PlainTextEncoding.Encoder(SupportedPayloadSchemas.PLAIN_TEXT),
                                                appPublisher,
                                                new LengthBasedMessageEncoding.Encoder()))))
        );
    }

    enum SupportedPayloadSchemas implements PayloadSchema {

        PLAIN_TEXT((short) 1),
        TIME((short) 2),
        PRICING((short) 3);

        private final short id;

        SupportedPayloadSchemas(short id) {
            this.id = id;
        }

        @Override
        public short id() {
            return id;
        }
    }
}
