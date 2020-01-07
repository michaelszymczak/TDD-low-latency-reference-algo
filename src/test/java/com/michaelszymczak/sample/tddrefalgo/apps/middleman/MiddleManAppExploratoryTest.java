package com.michaelszymczak.sample.tddrefalgo.apps.middleman;

import com.michaelszymczak.sample.tddrefalgo.apps.marketmaker.MarketMakerApp;
import com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain.ReferenceThrottledPrices;
import com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain.SimpleLowLatencyThrottledPrices;
import com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain.ThrottledPrices;
import com.michaelszymczak.sample.tddrefalgo.apps.support.InputPermutations;
import com.michaelszymczak.sample.tddrefalgo.testsupport.OutputSpy;
import com.michaelszymczak.sample.tddrefalgo.testsupport.PricingProtocolDecodedMessageSpy;
import com.michaelszymczak.sample.tddrefalgo.testsupport.RelativeNanoClockWithTimeFixedTo;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;

class MiddleManAppExploratoryTest {

    private static final int WINDOW_SIZE = 100;
    private static final Function<ThrottledPricesPublisher, ThrottledPrices> REFERENCE_IMPLEMENTATION = throttledPricesPublisher ->
            new ReferenceThrottledPrices(throttledPricesPublisher, WINDOW_SIZE);
    private static final Function<ThrottledPricesPublisher, ThrottledPrices> TESTED_IMPLEMENTATION = throttledPricesPublisher ->
            new SimpleLowLatencyThrottledPrices(throttledPricesPublisher, WINDOW_SIZE);
    private static final String MESSAGE_DELIMITER = ", ";
    private static final int PUBLISHER_CAPACITY = 5 * 1024 * 1024;
    private final OutputSpy<PricingProtocolDecodedMessageSpy> inputSpy = OutputSpy.outputSpy();
    private final OutputSpy<PricingProtocolDecodedMessageSpy> outputSpy = OutputSpy.outputSpy();
    private final MarketMakerApp marketMakerApp = new MarketMakerApp(
            new RelativeNanoClockWithTimeFixedTo(12345L), PUBLISHER_CAPACITY);
    private final InputPermutations<String> inputPermutations = new InputPermutations<>(
            "Q/   isin/  1/     101/   102",
            "Q/   isin/  1/     191/   192",
            "Q/   isin/  2/     201/   202",
            "Q/   isin/  2/     291/   292",
            "Q/   isin/  3/     301/   302",
            "Q/   isin/  3/     391/   392",
            "Q/   isin/  0/       0/     0"
    );

    @Test
    void shouldProduceSideEffects() {
        List<String> eventsPermutationBeforeAck = inputPermutationsWithSlotCountOf(4);

        for (String permutation : eventsPermutationBeforeAck) {

            // Given
            marketMakerApp.events(permutation);
            inputSpy.onInput(marketMakerApp.output());
            String inputEvents = inputSpy.getSpy().receivedMessagesPrettyPrint(MESSAGE_DELIMITER);
            inputSpy.getSpy().clear();
            marketMakerApp.output().reset();

            // When
            PricingProtocolDecodedMessageSpy referenceImplementationSideEffects = process(REFERENCE_IMPLEMENTATION, inputEvents);
            PricingProtocolDecodedMessageSpy testedImplementationSideEffects = process(REFERENCE_IMPLEMENTATION, inputEvents);

            // Then
            String referenceImplementationOutput = referenceImplementationSideEffects.receivedMessagesPrettyPrint(MESSAGE_DELIMITER);
            String testedImplementationOutput = testedImplementationSideEffects.receivedMessagesPrettyPrint(MESSAGE_DELIMITER);

            assertThat(testedImplementationOutput)
                    .describedAs(inputEvents + " -> " + referenceImplementationOutput)
                    .isEqualTo(testedImplementationOutput); // reference implementation produces different output

            // Cleanup
            marketMakerApp.output().reset();
            inputSpy.getSpy().clear();
            outputSpy.getSpy().clear();
        }
    }

    @Test
    @Disabled
    void manuallyExamineOutput() {
        String inputEvents = "Q/isin/1/101/102, Q/isin/2/291/292, Q/isin/0/0/0, Q/isin/1/101/102, A";
        String output = process(REFERENCE_IMPLEMENTATION, inputEvents).receivedMessagesPrettyPrint(MESSAGE_DELIMITER);
//        System.out.println(inputEvents + " -> " + output);
        assertThat(output).isNotEmpty();
    }

    private PricingProtocolDecodedMessageSpy process(
            final Function<ThrottledPricesPublisher, ThrottledPrices> throttledPricesFactory,
            String inputEvents) {
        marketMakerApp.output().reset();
        MiddleManApp middleManApp = createMiddleManAppWithWindowFull(throttledPricesFactory);
        marketMakerApp.events(MESSAGE_DELIMITER, inputEvents);
        middleManApp.onInput(marketMakerApp.output());
        outputSpy.getSpy().clear();
        outputSpy.onInput(middleManApp.output());
        return outputSpy.getSpy();
    }

    private List<String> inputPermutationsWithSlotCountOf(int slots) {
        return inputPermutations.generate(slots).stream()
                .map(permutation -> String.join("\n", permutation) + "\nA\n")
                .collect(Collectors.toList());
    }

    private MiddleManApp createMiddleManAppWithWindowFull(final Function<ThrottledPricesPublisher, ThrottledPrices> throttledPricesFactory) {
        MiddleManApp middleManApp = new MiddleManApp(PUBLISHER_CAPACITY, throttledPricesFactory);
        range(0, WINDOW_SIZE).forEach(i -> marketMakerApp.events(
                format("Q/   otherisin%d/  1/     1/   1", i)
        ));
        outputSpy.getSpy().clear();
        middleManApp.onSingleReaderInput(marketMakerApp.output());
        outputSpy.onSingleReaderInput(middleManApp.output());
        assertThat(outputSpy.getSpy().receivedMessages()).hasSize(WINDOW_SIZE);
        outputSpy.getSpy().clear();
        return middleManApp;
    }

}