package com.michaelszymczak.sample.tddrefalgo.apps.marketmaker;

import com.michaelszymczak.sample.tddrefalgo.apps.marketmaker.support.Probabilities;
import com.michaelszymczak.sample.tddrefalgo.testsupport.OutputSpy;
import com.michaelszymczak.sample.tddrefalgo.testsupport.PricingMessagesCountingSpy;
import org.junit.jupiter.api.Test;

import static com.michaelszymczak.sample.tddrefalgo.apps.marketmaker.support.Probabilities.QuoteProbability.quoteProbability;
import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;

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
                        .withNoPriceProbability(30)
                        .withNoTierProbability(30)
                        .build()
        )).newOutput());
        range(1, reads + 1).forEach(read -> range(1, rounds + 1).forEach(round -> outputSpy.onInput(app.output(round))));

        // Then
        assertThat(outputSpy.getSpy().receivedMessagesCount()).isBetween(
                totalExpectedMessages - (int) (totalExpectedMessages * 0.2),
                totalExpectedMessages + (int) (totalExpectedMessages * 0.2));
    }

}