package com.michaelszymczak.sample.tddrefalgo;

import com.michaelszymczak.sample.tddrefalgo.apps.plaintext.EchoApp;
import com.michaelszymczak.sample.tddrefalgo.apps.samplepricing.SamplePricingApp;
import com.michaelszymczak.sample.tddrefalgo.encoding.PayloadSchema;
import com.michaelszymczak.sample.tddrefalgo.encoding.lengthbased.LengthBasedMessageEncoding;
import com.michaelszymczak.sample.tddrefalgo.encoding.plaintext.EncodingPlainTextPublisher;
import com.michaelszymczak.sample.tddrefalgo.encoding.plaintext.PlainTextEncoding;
import com.michaelszymczak.sample.tddrefalgo.encoding.pricingprotocol.EncodingPricingProtocolPublisher;
import com.michaelszymczak.sample.tddrefalgo.encoding.pricingprotocol.PricingProtocolEncoding;
import com.michaelszymczak.sample.tddrefalgo.encoding.time.TimeEncoding;

import static com.michaelszymczak.sample.tddrefalgo.Setup.SupportedPayloadSchemas.PRICING;
import static java.util.Arrays.asList;

class Setup {

    static LengthBasedMessageEncoding.Encoder encoder() {
        return new LengthBasedMessageEncoding.Encoder(asList(
                new PlainTextEncoding.Encoder(SupportedPayloadSchemas.PLAIN_TEXT),
                new PricingProtocolEncoding.Encoder(PRICING),
                new TimeEncoding.Encoder(SupportedPayloadSchemas.TIME)
        ));
    }

    static LengthBasedMessageEncoding.Decoder decoder() {
        return new LengthBasedMessageEncoding.Decoder();
    }

    static EncodingApp createApp() {
        return new EncodingApp(
                asList(
                        appPublisher -> new RegisteredApp<>(
                                PRICING,
                                new PricingProtocolEncoding.Decoder(),
                                new SamplePricingApp(
                                        new EncodingPricingProtocolPublisher(
                                                appPublisher,
                                                new LengthBasedMessageEncoding.Encoder(new PricingProtocolEncoding.Encoder(PRICING))))),
                        appPublisher -> new RegisteredApp<>(
                                SupportedPayloadSchemas.PLAIN_TEXT,
                                new PlainTextEncoding.Decoder(),
                                new EchoApp(
                                        new EncodingPlainTextPublisher(
                                                appPublisher,
                                                new LengthBasedMessageEncoding.Encoder(new PlainTextEncoding.Encoder(SupportedPayloadSchemas.PLAIN_TEXT))))))
        );
    }

    enum SupportedPayloadSchemas implements PayloadSchema {

        UNDEFINED((short) 0),
        PLAIN_TEXT((short) 1),
        TIME((short) 2),
        PRICING((short) 3);

        private static final SupportedPayloadSchemas[] VALUES = SupportedPayloadSchemas.values();
        private final short id;

        SupportedPayloadSchemas(short id) {
            this.id = id;
        }

        public static PayloadSchema of(int schemaCode) {
            for (SupportedPayloadSchemas payloadSchema : VALUES) {
                if (payloadSchema.id() == schemaCode) {
                    return payloadSchema;
                }
            }
            return UNDEFINED;
        }

        @Override
        public short id() {
            return id;
        }
    }
}
