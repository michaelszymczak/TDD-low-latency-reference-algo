package com.michaelszymczak.sample.tddrefalgo.apps.middleman;

import com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain.ReferenceThrottledPrices;
import com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain.SimpleLowLatencyThrottledPrices;
import com.michaelszymczak.sample.tddrefalgo.apps.middleman.support.Exposure;
import com.michaelszymczak.sample.tddrefalgo.apps.middleman.support.MarketEventsGenerator;
import com.michaelszymczak.sample.tddrefalgo.testsupport.PricingProtocolDecodedMessageSpy;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MiddleManAppExploratoryTest {

    private static final String SAME_EXPOSURE_DIFFERENT_OUTPUT =
            "Q/isin/1/101/102, Q/isin/2/291/292, Q/isin2/0/0/0, Q/isin/1/101/102, A";
    private static final String SAME_EXPOSURE_SAME_OUTPUT =
            "Q/isin/1/101/102, Q/isin/2/291/292, Q/isin/1/101/102, Q/isin/2/291/292, A";

    private static final int WINDOW_SIZE = 100;
    private static final String MESSAGE_DELIMITER = ", ";
    private static final int PUBLISHER_CAPACITY = 5 * 1024 * 1024;
    private static final MarketEventsGenerator MARKET_EVENTS_GENERATOR = new MarketEventsGenerator(MESSAGE_DELIMITER, 4);
    private final Process process = new Process(PUBLISHER_CAPACITY, MESSAGE_DELIMITER, WINDOW_SIZE);
    private MiddleManApp referenceMiddleManApp = new MiddleManApp(PUBLISHER_CAPACITY, throttledPricesPublisher ->
            new ReferenceThrottledPrices(throttledPricesPublisher, WINDOW_SIZE));
    private MiddleManApp testedMiddleManApp = new MiddleManApp(PUBLISHER_CAPACITY, throttledPricesPublisher ->
            new SimpleLowLatencyThrottledPrices(throttledPricesPublisher, WINDOW_SIZE)); // use SimpleLowLatencyThrottledPrices instead

    static List<String> humanReadablePermutations() {
        return MARKET_EVENTS_GENERATOR.generateHumanReadablePermutations();
    }

    private static void assertIdenticalSideEffects(
            String humanReadableMarketEventsSequence,
            PricingProtocolDecodedMessageSpy referenceImplementationSideEffects,
            PricingProtocolDecodedMessageSpy testedImplementationSideEffects) {

        String referenceImplementationOutput = referenceImplementationSideEffects.receivedMessagesPrettyPrint(MESSAGE_DELIMITER);
        String testedImplementationOutput = testedImplementationSideEffects.receivedMessagesPrettyPrint(MESSAGE_DELIMITER);

        assertThat(humanReadableMarketEventsSequence).isNotEmpty();
        assertThat(referenceImplementationOutput).isNotEmpty();
        assertThat(testedImplementationOutput)
                .describedAs("For input: " + humanReadableMarketEventsSequence)
                .isEqualTo(referenceImplementationOutput);
    }

    private static void assertIdenticalExposure(
            String humanReadableMarketEventsSequence,
            PricingProtocolDecodedMessageSpy referenceImplementationSideEffects,
            PricingProtocolDecodedMessageSpy testedImplementationSideEffects) {

        Exposure exposureForReferenceImplementation = new Exposure();
        Exposure exposureForTestedImplementation = new Exposure();
        referenceImplementationSideEffects.receivedMessages()
                .forEach(exposureForReferenceImplementation::onPricingMessage);

        testedImplementationSideEffects.receivedMessages()
                .forEach(exposureForTestedImplementation::onPricingMessage);

        assertThat(exposureForTestedImplementation)
                .describedAs("For input: " + humanReadableMarketEventsSequence)
                .isEqualTo(exposureForReferenceImplementation);
    }

    @ParameterizedTest
    @MethodSource("humanReadablePermutations")
    @Disabled
    void shouldProduceSideEffectsIdenticalToTheReferenceImplementation(String humanReadablePermutations) {
        PricingProtocolDecodedMessageSpy referenceImplementationSideEffects = process
                .process(humanReadablePermutations, referenceMiddleManApp);

        // When
        PricingProtocolDecodedMessageSpy testedImplementationSideEffects = process
                .process(humanReadablePermutations, testedMiddleManApp);

        // Then
        assertIdenticalSideEffects(
                humanReadablePermutations,
                referenceImplementationSideEffects,
                testedImplementationSideEffects
        );
    }

    @ParameterizedTest
    @MethodSource("humanReadablePermutations")
    @Disabled
    void shouldResultInTheSameExposureAsTheReferenceImplementation(String humanReadablePermutations) {
        PricingProtocolDecodedMessageSpy referenceImplementationSideEffects = process
                .process(humanReadablePermutations, referenceMiddleManApp);

        // When
        PricingProtocolDecodedMessageSpy testedImplementationSideEffects = process
                .process(humanReadablePermutations, testedMiddleManApp);

        // Then
        assertIdenticalExposure(
                humanReadablePermutations,
                referenceImplementationSideEffects,
                testedImplementationSideEffects
        );
    }

    @Test
    void testIdenticalOutput1() {
        shouldProduceSideEffectsIdenticalToTheReferenceImplementation(SAME_EXPOSURE_SAME_OUTPUT);
    }

    @Test
    @Disabled
    void testIdenticalOutput2() {
        shouldProduceSideEffectsIdenticalToTheReferenceImplementation(SAME_EXPOSURE_DIFFERENT_OUTPUT);
    }

    @Test
    void testExposure() {
        shouldResultInTheSameExposureAsTheReferenceImplementation(SAME_EXPOSURE_SAME_OUTPUT);
        shouldResultInTheSameExposureAsTheReferenceImplementation(SAME_EXPOSURE_DIFFERENT_OUTPUT);
    }


}