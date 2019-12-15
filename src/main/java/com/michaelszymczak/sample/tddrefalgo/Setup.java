package com.michaelszymczak.sample.tddrefalgo;

import com.michaelszymczak.sample.tddrefalgo.api.io.AppIO;
import com.michaelszymczak.sample.tddrefalgo.api.setup.AppFactory;
import com.michaelszymczak.sample.tddrefalgo.api.setup.AppFactoryRegistry;
import com.michaelszymczak.sample.tddrefalgo.api.setup.PayloadSchema;
import com.michaelszymczak.sample.tddrefalgo.api.setup.RegisteredAppFactory;
import com.michaelszymczak.sample.tddrefalgo.apps.plaintext.EchoApp;
import com.michaelszymczak.sample.tddrefalgo.apps.plaintext.PlainTextEncoding;
import com.michaelszymczak.sample.tddrefalgo.apps.pricing.PricingProtocolEncoding;
import com.michaelszymczak.sample.tddrefalgo.apps.pricing.SamplePricingApp;

import static com.michaelszymczak.sample.tddrefalgo.Setup.SupportedPayloadSchemas.PLAIN_TEXT;
import static com.michaelszymczak.sample.tddrefalgo.Setup.SupportedPayloadSchemas.PRICING;
import static java.util.Arrays.asList;

class Setup {

    static AppIO createApp() {
        return AppFactory.createApp(new AppFactoryRegistry(asList(
                new RegisteredAppFactory<>(
                        PRICING,
                        new PricingProtocolEncoding.Decoder(),
                        new PricingProtocolEncoding.Encoder(PRICING),
                        SamplePricingApp::new
                ),
                new RegisteredAppFactory<>(
                        PLAIN_TEXT,
                        new PlainTextEncoding.Decoder(),
                        new PlainTextEncoding.Encoder(PLAIN_TEXT),
                        EchoApp::new
                )
        )));
    }

    enum SupportedPayloadSchemas implements PayloadSchema {

        PLAIN_TEXT((short) 1),
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
