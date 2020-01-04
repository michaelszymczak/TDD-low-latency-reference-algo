package com.michaelszymczak.sample.tddrefalgo.apps.marketmaker;

import com.michaelszymczak.sample.tddrefalgo.apps.support.InputPermutations;
import com.michaelszymczak.sample.tddrefalgo.testsupport.OutputSpy;
import com.michaelszymczak.sample.tddrefalgo.testsupport.PricingProtocolDecodedMessageSpy;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class MarketMakerPropertyTest {

    private final MarketMakerApp app = new MarketMakerApp();
    private final List<String> possibleEvents = Arrays.asList(
            "Q/   isin/  1/     101/   102",
            "Q/   isin/  1/     191/   192",
            "Q/   isin/  2/     201/   202",
            "Q/   isin/  2/     291/   292",
            "Q/   isin/  3/     301/   302",
            "Q/   isin/  3/     391/   392",
            "Q/   isin/  0/       0/     0"
    );
    private final OutputSpy<PricingProtocolDecodedMessageSpy> spy = OutputSpy.outputSpy();

    @Test
    void shouldGeneratePrescribedNumberOfEventsRegardlessOfTheirPermutation() {
        InputPermutations<String> inputPermutations = new InputPermutations<>(possibleEvents);
        List<String> eventsPermutationBeforeAck = inputPermutations.generate(2).stream()
                .map(permutation -> String.join("\n", permutation) + "\nA\n")
                .collect(Collectors.toList());

        for (String s : eventsPermutationBeforeAck) {
            app.events(s);
            spy.getSpy().clear();
            spy.onSingleReaderInput(app.output());
            Assertions.assertThat(spy.getSpy().receivedMessages()).hasSize(3);
        }
    }
}
