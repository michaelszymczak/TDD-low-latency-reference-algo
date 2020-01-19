package com.michaelszymczak.sample.tddrefalgo.apps.middleman;

import com.michaelszymczak.sample.tddrefalgo.apps.marketmaker.MarketMakerApp;
import com.michaelszymczak.sample.tddrefalgo.apps.marketmaker.support.MarketUpdatesGenerator;
import com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain.ReferenceThrottledPrices;
import com.michaelszymczak.sample.tddrefalgo.apps.middleman.perf.ThrottledPricesLatencyMeasurement;
import com.michaelszymczak.sample.tddrefalgo.support.OutputSpy;
import com.michaelszymczak.sample.tddrefalgo.support.PricingProtocolDecodedMessageSpy;
import com.michaelszymczak.sample.tddrefalgo.testsupport.RelativeNanoClockWithTimeFixedTo;
import org.agrona.ExpandableArrayBuffer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;

class MiddleManAppPropertyTest {

    private final MiddleManApp middleManApp = new MiddleManApp(1024, 1);
    private final OutputSpy<PricingProtocolDecodedMessageSpy> outputSpy = OutputSpy.outputSpy();
    private final MarketMakerApp marketMakerApp = new MarketMakerApp(new RelativeNanoClockWithTimeFixedTo(12345L), 5 * 1024 * 1024);

    @Test
    void shouldNotProduceSideEffectsUnprompted() {
        // When
        middleManApp.onInput(new ExpandableArrayBuffer(), 0, 0, false);

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
        final MarketMakerApp app = MarketUpdatesGenerator.generateMarketUpdates(publisherCapacity, rounds, priceUpdatesPerRound, ackPerMilProbability);

        MiddleManApp middleManApp = new MiddleManApp(publisherCapacity, windowSize);
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
        final MarketMakerApp app = MarketUpdatesGenerator.generateMarketUpdates(publisherCapacity, rounds, priceUpdatesPerRound, ackPerMilProbability);

        MiddleManApp middleManApp = new MiddleManApp(publisherCapacity, windowSize);
        range(1, rounds + 1).forEach(round -> {
            middleManApp.onInput(app.output(round));
            middleManApp.output().reset();
        });
    }

    @Test
    void shouldHaveLowLatencyWhenDownstreamServiceKeepsThePace() {
        ThrottledPricesLatencyMeasurement.run(new ThrottledPricesLatencyMeasurement.MarketActivitySimulation()
                .withSut(publisher -> new ReferenceThrottledPrices(publisher, 10))
                .withGeneratedEvents(100));
    }

    @Test
    @Disabled
    void shouldHaveLowLatencyWhenDownstreamServiceHasBurstyNature() {
        ThrottledPricesLatencyMeasurement.run(new ThrottledPricesLatencyMeasurement.MarketActivitySimulation()
                .withSut(publisher -> new ReferenceThrottledPrices(publisher, 500))
                .withGeneratedEvents(2));
    }

}