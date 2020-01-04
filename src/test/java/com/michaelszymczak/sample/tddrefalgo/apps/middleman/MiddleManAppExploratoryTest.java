package com.michaelszymczak.sample.tddrefalgo.apps.middleman;

import com.michaelszymczak.sample.tddrefalgo.apps.marketmaker.MarketMakerApp;
import com.michaelszymczak.sample.tddrefalgo.apps.support.InputPermutations;
import com.michaelszymczak.sample.tddrefalgo.testsupport.OutputSpy;
import com.michaelszymczak.sample.tddrefalgo.testsupport.PricingProtocolDecodedMessageSpy;
import com.michaelszymczak.sample.tddrefalgo.testsupport.RelativeNanoClockWithTimeFixedTo;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;

class MiddleManAppExploratoryTest {

    private static final String MESSAGE_DELIMITER = ", ";
    private final OutputSpy<PricingProtocolDecodedMessageSpy> inputSpy = OutputSpy.outputSpy();
    private final OutputSpy<PricingProtocolDecodedMessageSpy> outputSpy = OutputSpy.outputSpy();
    private final MarketMakerApp marketMakerApp = new MarketMakerApp(new RelativeNanoClockWithTimeFixedTo(12345L), 5 * 1024 * 1024);
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
            marketMakerApp.output().reset();
            String output = process(10, inputEvents);

            // Then
//            System.out.println(inputEvents + " -> " + output);
            assertThat(output).isNotEmpty();

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
        String output = process(10, inputEvents);
//        System.out.println(inputEvents + " -> " + output);
        assertThat(output).isNotEmpty();
    }

    private String process(final int windowSize, String inputEvents) {
        MiddleManApp middleManApp = createAppWithFullWindowOfSize(windowSize);
        marketMakerApp.events(MESSAGE_DELIMITER, inputEvents);
        middleManApp.onInput(marketMakerApp.output());
        outputSpy.onInput(middleManApp.output());
        return outputSpy.getSpy().receivedMessagesPrettyPrint(MESSAGE_DELIMITER);
    }

    private List<String> inputPermutationsWithSlotCountOf(int slots) {
        return inputPermutations.generate(slots).stream()
                .map(permutation -> String.join("\n", permutation) + "\nA\n")
                .collect(Collectors.toList());
    }

    private MiddleManApp createAppWithFullWindowOfSize(int windowSize) {
        MiddleManApp middleManApp = new MiddleManApp(1024, windowSize);
        range(0, windowSize).forEach(i -> marketMakerApp.events(
                format("Q/   otherisin%d/  0/     0/   0", i)
        ));
        middleManApp.onSingleReaderInput(marketMakerApp.output());
        outputSpy.onSingleReaderInput(middleManApp.output());
        assertThat(outputSpy.getSpy().receivedMessages()).hasSize(windowSize);
        outputSpy.getSpy().clear();
        return middleManApp;
    }

}