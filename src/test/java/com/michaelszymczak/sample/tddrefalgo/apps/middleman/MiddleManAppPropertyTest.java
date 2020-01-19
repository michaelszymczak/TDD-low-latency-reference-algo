package com.michaelszymczak.sample.tddrefalgo.apps.middleman;

import com.michaelszymczak.sample.tddrefalgo.apps.marketmaker.MarketMakerApp;
import com.michaelszymczak.sample.tddrefalgo.apps.marketmaker.support.Probabilities;
import com.michaelszymczak.sample.tddrefalgo.support.OutputSpy;
import com.michaelszymczak.sample.tddrefalgo.support.PricingProtocolDecodedMessageSpy;
import com.michaelszymczak.sample.tddrefalgo.testsupport.PricingMessagesCountingSpy;
import com.michaelszymczak.sample.tddrefalgo.testsupport.RelativeNanoClockWithTimeFixedTo;
import org.agrona.ExpandableArrayBuffer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static com.michaelszymczak.sample.tddrefalgo.apps.marketmaker.support.Probabilities.QuoteProbability.quoteProbability;
import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;

class MiddleManAppPropertyTest {

    private final MiddleManApp middleManApp = new MiddleManApp(1024, 1, true);
    private final OutputSpy<PricingProtocolDecodedMessageSpy> outputSpy = OutputSpy.outputSpy();
    private final MarketMakerApp marketMakerApp = new MarketMakerApp(new RelativeNanoClockWithTimeFixedTo(12345L), 5 * 1024 * 1024);

    @Test
    void shouldNotProduceSideEffectsUnprompted() {
        // When
        middleManApp.onInput(new ExpandableArrayBuffer(), 0, 0);

        // Then
        outputSpy.onInput(middleManApp.output());
        assertThat(outputSpy.getSpy().receivedMessages()).isEmpty();
    }

    @Test
    void shouldProduceSideEffects() {
        // Given
        marketMakerApp.heartbeat();

        // When
        middleManApp.onInput(marketMakerApp.output());

        // Then
        outputSpy.onInput(middleManApp.output());
        assertThat(outputSpy.getSpy().receivedMessages()).isNotEmpty();
    }

    @Test
    @Disabled
    void shouldProduceCorrectOutputs() {
    }

    @Test
    @Timeout(5)
    void shouldHandleHighThroughput() {
        final int publisherCapacity = 5 * 1024 * 1024;
        final int rounds = 10;
        final int priceUpdatesPerRound = 50_000;
        final int ackPerMilProbability = 100;
        final int windowSize = (1000 / ackPerMilProbability);
        final MarketMakerApp app = generateMarketUpdates(publisherCapacity, rounds, priceUpdatesPerRound, ackPerMilProbability);

        MiddleManApp middleManApp = new MiddleManApp(publisherCapacity, windowSize, true);
        range(1, rounds + 1).forEach(round -> {
            middleManApp.onInput(app.output(round));
            middleManApp.output().reset();
        });
    }

    @Test
    @Timeout(5)
    @Disabled
    void shouldHandleHighThroughputWithInfrequentAcks() {
        final int publisherCapacity = 5 * 1024 * 1024;
        final int rounds = 10;
        final int priceUpdatesPerRound = 50_000;
        final int ackPerMilProbability = 2;
        final int windowSize = (1000 / ackPerMilProbability);
        final MarketMakerApp app = generateMarketUpdates(publisherCapacity, rounds, priceUpdatesPerRound, ackPerMilProbability);

        MiddleManApp middleManApp = new MiddleManApp(publisherCapacity, windowSize, true);
        range(1, rounds + 1).forEach(round -> {
            middleManApp.onInput(app.output(round));
            middleManApp.output().reset();
        });
    }

    private static MarketMakerApp generateMarketUpdates(int publisherCapacity, int rounds, final int samples, final int ackPerMilProbability) {
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
        assertThat(marketMakerOutputSpy.getSpy().receivedMessagesCount()).isBetween(
                totalExpectedMessages - (int) (totalExpectedMessages * 0.2),
                totalExpectedMessages + (int) (totalExpectedMessages * 0.2));
        return app;
    }

    @Test
    @Timeout(10)
    @Disabled
    void shouldHaveLowLatencyWhenDownstreamServiceKeepsThePace() {
        shouldHaveLowLatency(100);
    }

    @Test
    @Timeout(10)
    @Disabled
    void shouldHaveLowLatencyWhenDownstreamServiceHasBurstyNature() {
        shouldHaveLowLatency(2);
    }

    private void shouldHaveLowLatency(final int ackPerMilProbability) {
        final int publisherCapacity = 5 * 1024 * 1024;
        final int rounds = 10;
        final int priceUpdatesPerRound = 50_000;
        final int windowSize = (1000 / ackPerMilProbability);
        final MarketMakerApp app = generateMarketUpdates(publisherCapacity, rounds, priceUpdatesPerRound, ackPerMilProbability);

        MiddleManApp middleManApp = new MiddleManApp(publisherCapacity, windowSize, true);
        range(1, rounds + 1).forEach(round -> {
            middleManApp.onInput(app.output(round));
            middleManApp.output().reset();
        });
    }
}