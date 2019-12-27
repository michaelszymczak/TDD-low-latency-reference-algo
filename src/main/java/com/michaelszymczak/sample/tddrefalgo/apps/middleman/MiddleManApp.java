package com.michaelszymczak.sample.tddrefalgo.apps.middleman;

import com.michaelszymczak.sample.tddrefalgo.framework.api.io.AppIO;
import com.michaelszymczak.sample.tddrefalgo.framework.api.io.Output;
import com.michaelszymczak.sample.tddrefalgo.framework.api.setup.AppFactory;
import com.michaelszymczak.sample.tddrefalgo.framework.api.setup.AppFactoryRegistry;
import com.michaelszymczak.sample.tddrefalgo.framework.api.setup.PayloadSchema;
import com.michaelszymczak.sample.tddrefalgo.framework.api.setup.RegisteredAppFactory;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.PricingProtocolEncoding;
import org.agrona.DirectBuffer;

import java.util.Collections;

public class MiddleManApp implements AppIO {

    private static final PayloadSchema PRICING_SCHEMA = new PayloadSchema.KnownPayloadSchema((short) 5);

    private final AppIO app;

    public MiddleManApp() {
        app = AppFactory.createApp(new AppFactoryRegistry(1024, Collections.singletonList(
                new RegisteredAppFactory<>(
                        PRICING_SCHEMA,
                        new PricingProtocolEncoding.Decoder(),
                        new PricingProtocolEncoding.Encoder(PRICING_SCHEMA),
                        PriceUpdatesHandler::new
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
