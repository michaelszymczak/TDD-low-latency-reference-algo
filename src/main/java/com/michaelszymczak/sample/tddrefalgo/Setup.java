package com.michaelszymczak.sample.tddrefalgo;

import com.michaelszymczak.sample.tddrefalgo.apps.plaintext.EchoApp;
import com.michaelszymczak.sample.tddrefalgo.apps.samplepricing.SamplePricingApp;
import com.michaelszymczak.sample.tddrefalgo.encoding.PayloadSchema;
import com.michaelszymczak.sample.tddrefalgo.encoding.lengthbased.LengthBasedMessageEncoding;
import com.michaelszymczak.sample.tddrefalgo.encoding.plaintext.PlainTextEncoding;
import com.michaelszymczak.sample.tddrefalgo.encoding.pricingprotocol.PricingProtocolEncoding;
import com.michaelszymczak.sample.tddrefalgo.encoding.time.TimeEncoding;

import java.util.Arrays;

class Setup {

    static LengthBasedMessageEncoding.Encoder encoder() {
        return new LengthBasedMessageEncoding.Encoder(Arrays.asList(
                new PlainTextEncoding.Encoder(SupportedPayloadSchemas.PLAIN_TEXT),
                new PricingProtocolEncoding.Encoder(SupportedPayloadSchemas.PRICING),
                new TimeEncoding.Encoder(SupportedPayloadSchemas.TIME)
        ));
    }

    static LengthBasedMessageEncoding.Decoder decoder() {
        return new LengthBasedMessageEncoding.Decoder();
    }

    static EncodingApp createApp() {
        return new EncodingApp(
                SamplePricingApp::new,
                EchoApp::new,
                encoder()
        );
    }

    enum SupportedPayloadSchemas implements PayloadSchema {

        UNDEFINED((short) 0),
        PLAIN_TEXT((short) 1),
        TIME((short) 2),
        PRICING((short) 3);

        private final short id;

        private static final SupportedPayloadSchemas[] VALUES = SupportedPayloadSchemas.values();

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
