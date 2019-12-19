package com.michaelszymczak.sample.tddrefalgo.apps.marketmaker;

import com.michaelszymczak.sample.tddrefalgo.framework.api.io.AppIO;
import com.michaelszymczak.sample.tddrefalgo.framework.api.io.Output;
import com.michaelszymczak.sample.tddrefalgo.framework.api.setup.AppFactory;
import com.michaelszymczak.sample.tddrefalgo.framework.api.setup.AppFactoryRegistry;
import com.michaelszymczak.sample.tddrefalgo.framework.api.setup.PayloadSchema;
import com.michaelszymczak.sample.tddrefalgo.framework.api.setup.RegisteredAppFactory;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.ImmutableQuotePricingMessage;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.PricingProtocolEncoding;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.QuotePricingMessage;
import com.michaelszymczak.sample.tddrefalgo.supportingdomain.RelativeNanoClock;
import org.agrona.DirectBuffer;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public class MarketMakerApp implements AppIO {

    private static final PayloadSchema PRICING_SCHEMA = new PayloadSchema.KnownPayloadSchema((short) 5);

    private final AppIO app;
    private final MarketMakingModule marketMakingModule;

    public MarketMakerApp() {
        this(System::nanoTime);
    }

    public MarketMakerApp(final RelativeNanoClock nanoClock) {
        this.marketMakingModule = new MarketMakingModule(nanoClock);
        this.app = AppFactory.createApp(new AppFactoryRegistry(1024 * 1024, Collections.singletonList(
                new RegisteredAppFactory<>(
                        PRICING_SCHEMA,
                        new PricingProtocolEncoding.Decoder(),
                        new PricingProtocolEncoding.Encoder(PRICING_SCHEMA),
                        marketMakingModule::registerPublisher
                )
        )));

    }

    @Override
    public int onInput(DirectBuffer input, int offset, int length) {
        return 0;
    }

    @Override
    public Output output() {
        return app.output();
    }

    public MarketMakerApp heartbeat() {
        marketMakingModule.heartbeat();
        return this;
    }

    public MarketMakerApp quote(String message) {
        List<String> split = stream(message.split("/", -1))
                .map(String::trim)
                .collect(Collectors.toList());
        if (split.size() != 5) {
            throw new IllegalArgumentException(message);
        }
        return quote(new ImmutableQuotePricingMessage(
                split.get(1),
                Integer.parseInt(split.get(2)),
                Long.parseLong(split.get(3)),
                Long.parseLong(split.get(4))
        ));
    }

    public MarketMakerApp quote(QuotePricingMessage message) {
        marketMakingModule.onMessage(message);
        return this;
    }
}
