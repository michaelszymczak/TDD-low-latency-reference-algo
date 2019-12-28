package com.michaelszymczak.sample.tddrefalgo.apps.marketmaker;

import com.michaelszymczak.sample.tddrefalgo.apps.marketmaker.support.CommandLines;
import com.michaelszymczak.sample.tddrefalgo.apps.marketmaker.support.OutputCopy;
import com.michaelszymczak.sample.tddrefalgo.apps.marketmaker.support.Probabilities;
import com.michaelszymczak.sample.tddrefalgo.framework.api.io.AppIO;
import com.michaelszymczak.sample.tddrefalgo.framework.api.io.Output;
import com.michaelszymczak.sample.tddrefalgo.framework.api.setup.AppFactory;
import com.michaelszymczak.sample.tddrefalgo.framework.api.setup.AppFactoryRegistry;
import com.michaelszymczak.sample.tddrefalgo.framework.api.setup.PayloadSchema;
import com.michaelszymczak.sample.tddrefalgo.framework.api.setup.RegisteredAppFactory;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.AckMessage;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.ImmutableQuotePricingMessage;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.PricingProtocolEncoding;
import com.michaelszymczak.sample.tddrefalgo.supportingdomain.RelativeNanoClock;
import org.agrona.DirectBuffer;
import org.agrona.collections.Int2ObjectHashMap;

import java.util.Collections;
import java.util.Random;
import java.util.stream.IntStream;

public class MarketMakerApp implements AppIO {

    private static final PayloadSchema PRICING_SCHEMA = new PayloadSchema.KnownPayloadSchema((short) 5);

    private final AppIO app;
    private final MarketMakingModule marketMakingModule;
    private final Int2ObjectHashMap<Output> outputByNumber = new Int2ObjectHashMap<>();
    private final Random random = new Random();
    private int nextOutputNumber = 1;

    public MarketMakerApp() {
        this(System::nanoTime, 5 * 1024 * 1024);
    }

    public MarketMakerApp(final RelativeNanoClock nanoClock, final int publisherCapacity) {
        this.marketMakingModule = new MarketMakingModule(nanoClock);
        this.app = AppFactory.createApp(new AppFactoryRegistry(publisherCapacity, Collections.singletonList(
                new RegisteredAppFactory<>(
                        PRICING_SCHEMA,
                        new PricingProtocolEncoding.Decoder(),
                        new PricingProtocolEncoding.Encoder(PRICING_SCHEMA),
                        marketMakingModule::registerPublisher
                )
        )));
        outputByNumber.put(0, app.output());
    }

    @Override
    public int onInput(DirectBuffer input, int offset, int length) {
        return 0;
    }

    @Override
    public Output output() {
        return output(0);
    }

    public Output output(int outputNumber) {
        return outputByNumber.get(outputNumber);
    }

    public MarketMakerApp heartbeat() {
        marketMakingModule.heartbeat();
        return this;
    }

    public MarketMakerApp events(String messages) {
        CommandLines.parseAll(messages).executeAgainst(marketMakingModule);
        return this;
    }

    public MarketMakerApp newOutput() {
        outputByNumber.put(nextOutputNumber++, new OutputCopy(app.output()));
        app.output().reset();
        return this;
    }

    public MarketMakerApp generateRandom(int attempts, Probabilities probabilities) {
        IntStream.range(0, attempts).forEach(attempt -> {
            if (random.nextInt(100) < probabilities.ackProbability.percentageProbability) {
                marketMakingModule.onMessage(AckMessage.ACK_MESSAGE);
            }
            if (random.nextInt(100) < probabilities.quoteProbability.percentageProbability) {
                String instrument = "isin" + random.nextInt(probabilities.quoteProbability.distinctInstruments);
                if (random.nextInt(100) < probabilities.quoteProbability.cancellationProbability) {
                    marketMakingModule.onMessage(new ImmutableQuotePricingMessage(instrument, 0, 0, 0));
                } else {
                    int priceTier = random.nextInt(5) + 1;
                    marketMakingModule.onMessage(new ImmutableQuotePricingMessage(
                            instrument, priceTier, random.nextInt(1000), random.nextInt(1000)));
                }
            }
        });
        return this;
    }
}
