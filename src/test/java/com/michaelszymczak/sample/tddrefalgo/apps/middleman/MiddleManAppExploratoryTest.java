package com.michaelszymczak.sample.tddrefalgo.apps.middleman;

import com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain.ReferenceThrottledPrices;
import com.michaelszymczak.sample.tddrefalgo.apps.middleman.support.MarketEventsGenerator;
import com.michaelszymczak.sample.tddrefalgo.testsupport.PricingProtocolDecodedMessageSpy;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MiddleManAppExploratoryTest {

    private static final int WINDOW_SIZE = 100;
    private static final String MESSAGE_DELIMITER = ", ";
    private static final int PUBLISHER_CAPACITY = 5 * 1024 * 1024;
    private final Process process = new Process(PUBLISHER_CAPACITY, MESSAGE_DELIMITER, WINDOW_SIZE);
    private static final MarketEventsGenerator MARKET_EVENTS_GENERATOR = new MarketEventsGenerator(MESSAGE_DELIMITER, 4);
    private MiddleManApp referenceMiddleManApp = new MiddleManApp(PUBLISHER_CAPACITY, throttledPricesPublisher ->
            new ReferenceThrottledPrices(throttledPricesPublisher, WINDOW_SIZE));
    private MiddleManApp testedMiddleManApp = new MiddleManApp(PUBLISHER_CAPACITY, throttledPricesPublisher ->
            new ReferenceThrottledPrices(throttledPricesPublisher, WINDOW_SIZE)); // use SimpleLowLatencyThrottledPrices instead

    @ParameterizedTest
    @MethodSource("humanReadablePermutations")
    void shouldProduceSideEffects(String humanReadablePermutations) {
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

    @Test
    @Disabled
    void manuallyExamineOutput() {
        shouldProduceSideEffects("Q/isin/1/101/102, Q/isin/2/291/292, Q/isin/0/0/0, Q/isin/1/101/102, A");
    }

    static List<String> humanReadablePermutations() {
        return MARKET_EVENTS_GENERATOR.generateHumanReadablePermutations();
    }

    private void assertIdenticalSideEffects(
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


}