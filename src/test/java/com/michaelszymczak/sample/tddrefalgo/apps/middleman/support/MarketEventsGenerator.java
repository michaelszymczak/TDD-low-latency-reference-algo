package com.michaelszymczak.sample.tddrefalgo.apps.middleman.support;

import com.michaelszymczak.sample.tddrefalgo.apps.marketmaker.MarketMakerApp;
import com.michaelszymczak.sample.tddrefalgo.apps.support.InputPermutations;
import com.michaelszymczak.sample.tddrefalgo.testsupport.OutputSpy;
import com.michaelszymczak.sample.tddrefalgo.testsupport.PricingProtocolDecodedMessageSpy;
import com.michaelszymczak.sample.tddrefalgo.testsupport.RelativeNanoClockWithTimeFixedTo;

import java.util.List;
import java.util.stream.Collectors;

public class MarketEventsGenerator {
    private static final int PUBLISHER_CAPACITY = 5 * 1024 * 1024;

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
    private final String messageDelimiter;
    private final int slots;

    public MarketEventsGenerator(final String messageDelimiter, final int slots) {
        this.messageDelimiter = messageDelimiter;
        this.slots = slots;
    }

    public List<String> generateHumanReadablePermutations() {
        return inputPermutationsWithSlotCountOf(slots).stream().map(this::generateFrom).collect(Collectors.toList());
    }

    private List<String> inputPermutationsWithSlotCountOf(int slots) {
        return inputPermutations.generate(slots).stream()
                .map(permutation -> String.join("\n", permutation) + "\nA\n")
                .collect(Collectors.toList());
    }

    private String generateFrom(String humanReadableMarketEventSequence) {
        marketMakerApp.events(humanReadableMarketEventSequence);
        final OutputSpy<PricingProtocolDecodedMessageSpy> spyOfTestedInputToSUT = OutputSpy.outputSpy();
        spyOfTestedInputToSUT.onInput(marketMakerApp.output());
        String humanReadableTestedInputToSUT = spyOfTestedInputToSUT.getSpy().receivedMessagesPrettyPrint(messageDelimiter);
        spyOfTestedInputToSUT.getSpy().clear();
        marketMakerApp.output().reset();
        return humanReadableTestedInputToSUT;
    }
}
