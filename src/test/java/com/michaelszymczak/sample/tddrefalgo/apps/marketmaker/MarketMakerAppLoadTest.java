package com.michaelszymczak.sample.tddrefalgo.apps.marketmaker;

import com.michaelszymczak.sample.tddrefalgo.apps.marketmaker.support.Probabilities;
import com.michaelszymczak.sample.tddrefalgo.testsupport.OutputSpy;
import com.michaelszymczak.sample.tddrefalgo.testsupport.PricingMessagesCountingSpy;
import com.michaelszymczak.sample.tddrefalgo.testsupport.SameOutputProperty;
import org.junit.jupiter.api.Test;

import static com.michaelszymczak.sample.tddrefalgo.apps.marketmaker.support.Probabilities.QuoteProbability.quoteProbability;
import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MarketMakerAppLoadTest {

    @Test
    void shouldBeAbleToGenerateManyMessages() {
        final MarketMakerApp app = new MarketMakerApp();
        OutputSpy<PricingMessagesCountingSpy> outputSpy = new OutputSpy<>(new PricingMessagesCountingSpy());
        final int samples = 50_000;
        final int rounds = 10;
        final int reads = 10;
        final int totalExpectedMessages = samples * rounds * reads;

        // When
        range(1, rounds + 1).forEach(round -> app.generateRandom(samples, new Probabilities(
                new Probabilities.AckProbability(1),
                quoteProbability()
                        .withPercentageProbability(99)
                        .withDistinctInstruments(10)
                        .withCancellationProbability(30)
                        .build()
        )).newOutput());
        range(1, reads + 1).forEach(read -> range(1, rounds + 1).forEach(round -> outputSpy.onInput(app.output(round))));

        // Then
        assertThat(outputSpy.getSpy().receivedMessagesCount()).isBetween(
                totalExpectedMessages - (int) (totalExpectedMessages * 0.2),
                totalExpectedMessages + (int) (totalExpectedMessages * 0.2));
    }

    @Test
    void shouldHaveComparableOutputs() {
        MarketMakerApp app = new MarketMakerApp();
        Probabilities probabilities = new Probabilities(quoteProbability()
                .withPercentageProbability(99)
                .withDistinctInstruments(10)
                .withCancellationProbability(30)
                .build());
        SameOutputProperty sameOutputProperty = new SameOutputProperty();

        // When
        app.generateRandom(50_000, probabilities);
        app.newOutput();
        app.generateRandom(50_000, probabilities);
        app.newOutput();

        // Then
        assertDoesNotThrow(() -> sameOutputProperty.verifySameOutputs(
                app.output(1).buffer(), app.output(1).offset(), app.output(1).length(),
                app.output(1).buffer(), app.output(1).offset(), app.output(1).length()
        ));
        assertThrows(AssertionError.class, () -> sameOutputProperty.verifySameOutputs(
                app.output(1).buffer(), app.output(1).offset(), app.output(1).length(),
                app.output(2).buffer(), app.output(2).offset(), app.output(2).length()
        ));
    }
}