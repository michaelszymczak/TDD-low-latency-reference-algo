package com.michaelszymczak.sample.tddrefalgo.apps.marketmaker.support;

import com.michaelszymczak.sample.tddrefalgo.apps.marketmaker.MarketMakerApp;
import com.michaelszymczak.sample.tddrefalgo.apps.middleman.perf.PricingMessagesCountingSpy;
import com.michaelszymczak.sample.tddrefalgo.support.OutputSpy;

import static com.michaelszymczak.sample.tddrefalgo.apps.marketmaker.support.Probabilities.QuoteProbability.quoteProbability;
import static java.util.stream.IntStream.range;

public class MarketUpdatesGenerator {

    public static MarketMakerApp generateMarketUpdates(int publisherCapacity, int rounds, final int samples, final int ackPerMilProbability) {
        final MarketMakerApp app = new MarketMakerApp(System::nanoTime, publisherCapacity);
        final OutputSpy<PricingMessagesCountingSpy> marketMakerOutputSpy = new OutputSpy<>(new PricingMessagesCountingSpy());
        final int totalExpectedMessages = samples * rounds;

        // When
        range(1, rounds + 1).forEach(round -> app.generateRandom(samples, new Probabilities(
                new Probabilities.AckProbability(ackPerMilProbability),
                quoteProbability()
                        .withPercentageProbability(990)
                        .withDistinctInstruments(100)
                        .withCancellationProbability(300)
                        .build()
        )).newOutput());
        range(1, rounds + 1).forEach(round -> marketMakerOutputSpy.onInput(app.output(round)));
        if (marketMakerOutputSpy.getSpy().receivedMessagesCount() < totalExpectedMessages - (int) (totalExpectedMessages * 0.2) ||
                marketMakerOutputSpy.getSpy().receivedMessagesCount() > totalExpectedMessages + (int) (totalExpectedMessages * 0.2)) {
            throw new IllegalStateException();
        }

        return app;
    }
}
