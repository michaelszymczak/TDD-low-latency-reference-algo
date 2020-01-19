package com.michaelszymczak.sample.tddrefalgo.apps.middleman;

import com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain.ReferenceThrottledPrices;
import com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain.SimpleLowLatencyThrottledPrices;
import com.michaelszymczak.sample.tddrefalgo.apps.middleman.support.Exposure;
import com.michaelszymczak.sample.tddrefalgo.apps.middleman.support.MarketEventsGenerator;
import com.michaelszymczak.sample.tddrefalgo.support.PricingProtocolDecodedMessageSpy;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static com.michaelszymczak.sample.tddrefalgo.testsupport.PricingMessagePrettyPrint.prettyPrint;
import static org.assertj.core.api.Assertions.assertThat;

class MiddleManAppExploratoryTest {

    private static final String QUOTES_ONLY =
            "Q/isin/1/101/102, Q/isin/2/291/292, Q/isin/1/101/102, Q/isin/2/291/292, A";
    private static final String CANCELS_AND_UNRELATED_QUOTES =
            "Q/isin/1/101/102, Q/isin/2/291/292, Q/isin2/0/0/0, Q/isin/1/101/102, A";
    private static final String CANCELS_AND_RELATED_QUOTES =
            "Q/isin/1/101/102, Q/isin/2/291/292, Q/isin/0/0/0, Q/isin/1/101/102, A";

    private static final int WINDOW_SIZE = 100;
    private static final String MESSAGE_DELIMITER = ", ";
    private static final int PUBLISHER_CAPACITY = 5 * 1024 * 1024;
    private static final MarketEventsGenerator MARKET_EVENTS_GENERATOR = new MarketEventsGenerator(MESSAGE_DELIMITER, 4);
    private final Process process = new Process(PUBLISHER_CAPACITY, MESSAGE_DELIMITER, WINDOW_SIZE);
    private MiddleManApp referenceMiddleManApp = new MiddleManApp(PUBLISHER_CAPACITY, true, throttledPricesPublisher ->
            new ReferenceThrottledPrices(throttledPricesPublisher, WINDOW_SIZE));
    private MiddleManApp testedMiddleManApp = new MiddleManApp(PUBLISHER_CAPACITY, true, throttledPricesPublisher ->
            new SimpleLowLatencyThrottledPrices(throttledPricesPublisher, WINDOW_SIZE)); // use SimpleLowLatencyThrottledPrices instead

    static List<String> humanReadablePermutations() {
        return MARKET_EVENTS_GENERATOR.generateHumanReadablePermutations();
    }

    static List<String> humanReadableSimulation() {
        return MARKET_EVENTS_GENERATOR.generateHumanReadableSimulation();
    }

    @ParameterizedTest
    @MethodSource("humanReadablePermutations")
    @Disabled
    void shouldProduceIdenticalSideEffectsForPermutations(String humanReadablePermutations) {
        shouldProduceIdenticalSideEffects(humanReadablePermutations);
    }

    @ParameterizedTest
    @MethodSource("humanReadablePermutations")
    @Disabled
    void shouldResultInTheSameExposureForPermutations(String humanReadablePermutations) {
        shouldResultInTheSameExposure(humanReadablePermutations);
    }

    @ParameterizedTest(name = "simulation")
    @MethodSource("humanReadableSimulation")
    @Disabled
    void shouldProduceIdenticalSideEffectsDuringSimulation(String humanReadableSimulation) {
        shouldProduceIdenticalSideEffects(humanReadableSimulation);
    }

    @ParameterizedTest(name = "simulation")
    @MethodSource("humanReadableSimulation")
    @Disabled
    void shouldResultInTheSameExposureDuringSimulation(String humanReadableSimulation) {
        shouldResultInTheSameExposure(humanReadableSimulation);
    }

    @Test
    void shouldProduceCorrectResults() {
        // change to reproduce some other input
        String input = "Q/isin/1/1/1, A";
        shouldResultInTheSameExposure(input);
        shouldProduceIdenticalSideEffects(input);
    }

    @Test
    void testIdenticalOutput1() {
        shouldProduceIdenticalSideEffects(QUOTES_ONLY);
    }

    @Test
    void testExposure1() {
        shouldResultInTheSameExposure(QUOTES_ONLY);
        shouldResultInTheSameExposure(CANCELS_AND_UNRELATED_QUOTES);
    }

    @Test
    @Disabled
    void testIdenticalOutput2() {
        shouldProduceIdenticalSideEffects(CANCELS_AND_UNRELATED_QUOTES);
    }

    @Test
    @Disabled
    void testExposure2() {
        shouldResultInTheSameExposure(CANCELS_AND_RELATED_QUOTES);
    }

    private void shouldProduceIdenticalSideEffects(String humanReadablePermutations) {
        PricingProtocolDecodedMessageSpy referenceImplementationSideEffects = process
                .process(humanReadablePermutations, referenceMiddleManApp);

        // When
        PricingProtocolDecodedMessageSpy testedImplementationSideEffects = process
                .process(humanReadablePermutations, testedMiddleManApp);

        // Then
        String referenceImplementationOutput = prettyPrint(MESSAGE_DELIMITER, referenceImplementationSideEffects.receivedMessages());
        String testedImplementationOutput = prettyPrint(MESSAGE_DELIMITER, testedImplementationSideEffects.receivedMessages());

        assertThat(humanReadablePermutations).isNotEmpty();
        assertThat(referenceImplementationOutput).isNotEmpty();
        assertThat(testedImplementationOutput)
                .describedAs("For input: " + humanReadablePermutations)
                .isEqualTo(referenceImplementationOutput);
    }

    private void shouldResultInTheSameExposure(String humanReadablePermutations) {
        PricingProtocolDecodedMessageSpy referenceImplementationSideEffects = process
                .process(humanReadablePermutations, referenceMiddleManApp);

        // When
        PricingProtocolDecodedMessageSpy testedImplementationSideEffects = process
                .process(humanReadablePermutations, testedMiddleManApp);

        // Then
        Exposure exposureForReferenceImplementation = new Exposure();
        Exposure exposureForTestedImplementation = new Exposure();
        referenceImplementationSideEffects.receivedMessages()
                .forEach(exposureForReferenceImplementation::onPricingMessage);

        testedImplementationSideEffects.receivedMessages()
                .forEach(exposureForTestedImplementation::onPricingMessage);

        assertThat(exposureForTestedImplementation)
                .describedAs("For input: " + humanReadablePermutations)
                .isEqualTo(exposureForReferenceImplementation);
    }

}