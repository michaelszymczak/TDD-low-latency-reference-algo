package com.michaelszymczak.sample.tddrefalgo.apps.fooapp;

import com.michaelszymczak.sample.tddrefalgo.framework.api.io.AppIO;
import com.michaelszymczak.sample.tddrefalgo.framework.api.io.Output;
import com.michaelszymczak.sample.tddrefalgo.framework.api.setup.AppFactory;
import com.michaelszymczak.sample.tddrefalgo.framework.api.setup.AppFactoryRegistry;
import com.michaelszymczak.sample.tddrefalgo.framework.api.setup.PayloadSchema;
import com.michaelszymczak.sample.tddrefalgo.framework.api.setup.RegisteredAppFactory;
import com.michaelszymczak.sample.tddrefalgo.protocols.plaintext.PlainTextEncoding;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.PricingProtocolEncoding;
import org.agrona.DirectBuffer;

import static java.util.Arrays.asList;

class FooApp implements AppIO {

    static final PayloadSchema PLAIN_TEXT = new PayloadSchema.KnownPayloadSchema((short) 1);
    static final PayloadSchema PRICING = new PayloadSchema.KnownPayloadSchema((short) 3);

    private final AppIO app;

    public FooApp() {
        app = AppFactory.createApp(new AppFactoryRegistry(1024 * 1024, asList(
                new RegisteredAppFactory<>(
                        PRICING,
                        new PricingProtocolEncoding.Decoder(),
                        new PricingProtocolEncoding.Encoder(PRICING),
                        SamplePricingModule::new
                ),
                new RegisteredAppFactory<>(
                        PLAIN_TEXT,
                        new PlainTextEncoding.Decoder(),
                        new PlainTextEncoding.Encoder(PLAIN_TEXT),
                        EchoModule::new
                )
        )));
    }

    @Override
    public int onInput(DirectBuffer input, int offset, int length) {
        return app.onInput(input, offset, length);
    }

    @Override
    public Output output() {
        return app.output();
    }
}
