package com.michaelszymczak.sample.tddrefalgo.apps.marketmaker;

import com.michaelszymczak.sample.tddrefalgo.apps.marketmaker.support.Probabilities;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.*;
import com.michaelszymczak.sample.tddrefalgo.testsupport.OutputSpy;
import com.michaelszymczak.sample.tddrefalgo.testsupport.PricingProtocolDecodedMessageSpy;
import com.michaelszymczak.sample.tddrefalgo.testsupport.RelativeNanoClockWithTimeFixedTo;
import org.junit.jupiter.api.Test;

import java.util.*;

import static com.michaelszymczak.sample.tddrefalgo.apps.marketmaker.support.Probabilities.QuoteProbability.quoteProbability;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MarketMakerAppTest {

    private final MarketMakerApp app = new MarketMakerApp();
    private final OutputSpy<PricingProtocolDecodedMessageSpy> outputSpy = OutputSpy.outputSpy();

    @Test
    void shouldBeIdleIfUnprompted() {
        assertEquals(0, new MarketMakerApp().output().length());
    }

    @Test
    void shouldHeartbeat() {
        MarketMakerApp app = new MarketMakerApp(new RelativeNanoClockWithTimeFixedTo(12345L), 5 * 1024 * 1024);

        outputSpy.onInput(app.heartbeat().output());

        assertEquals(1, outputSpy.getSpy().receivedMessages().size());
        assertEquals(new ImmutableHeartbeatPricingMessage(12345L), outputSpy.getSpy().receivedMessages().get(0));
    }

    @Test
    void shouldSendEventsWhenAskedTo() {
        outputSpy.onSingleReaderInput(app
                .events("Q/   isin1/  1/     4455/   4466\n" +
                        "Q/   isin2/  2/     7755/   8866\n" +
                        "Q/   isin3/  1/     0/         0\n" +
                        "A\n" +
                        "Q/   isin3/  0/     0/         0\n" +
                        "Q/   isin3/  0/     1234/   5678\n" +
                        "A\n"
                ).output());

        assertEquals(Arrays.asList(
                new ImmutableQuotePricingMessage("isin1       ", 1, 4455L, 4466L),
                new ImmutableQuotePricingMessage("isin2       ", 2, 7755L, 8866L),
                new ImmutableQuotePricingMessage("isin3       ", 1, 0L, 0L),
                AckMessage.ACK_MESSAGE,
                new ImmutableQuotePricingMessage("isin3       ", 0, 0L, 0L),
                new ImmutableQuotePricingMessage("isin3       ", 0, 1234L, 5678L),
                AckMessage.ACK_MESSAGE
        ), outputSpy.getSpy().receivedMessages());
    }

    @Test
    void shouldSendEventsReGeneratedFromCapturedEvents() {
        outputSpy.onSingleReaderInput(app.generateRandom(
                1000, new Probabilities(
                        new Probabilities.AckProbability(100),
                        quoteProbability()
                                .withPercentageProbability(700)
                                .withDistinctInstruments(10)
                                .withCancellationProbability(100).build())
        ).output());
        final List<PricingMessage> expectedReceivedMessages = new ArrayList<>(outputSpy.getSpy().receivedMessages());
        String reGeneratedInputs = outputSpy.getSpy().receivedMessagesPrettyPrint(", ");
        outputSpy.getSpy().clear();

        // When
        outputSpy.onSingleReaderInput(app.events(", ", reGeneratedInputs).output());

        // Then
        assertThat(outputSpy.getSpy().receivedMessages()).isEqualTo(expectedReceivedMessages);
    }

    @Test
    void shouldGenerateEventsForLater() {
        // When
        app.events("Q/   isin1/  1/     4455/   4466\n" +
                "A\n"
        );
        app.newOutput();
        app.events("Q/   isin2/  2/     5555/   6666\n" +
                "A\n"
        );
        app.newOutput();
        app.newOutput();

        // Then
        outputSpy.onInput(app.output());
        assertEquals(emptyList(), outputSpy.getSpy().receivedMessages());
        outputSpy.getSpy().clear();

        outputSpy.onInput(app.output(0));
        assertEquals(emptyList(), outputSpy.getSpy().receivedMessages());
        outputSpy.getSpy().clear();

        outputSpy.onInput(app.output(1));
        assertEquals(Arrays.asList(
                new ImmutableQuotePricingMessage("isin1       ", 1, 4455L, 4466L),
                AckMessage.ACK_MESSAGE
        ), outputSpy.getSpy().receivedMessages());
        outputSpy.getSpy().clear();

        outputSpy.onInput(app.output(2));
        assertEquals(Arrays.asList(
                new ImmutableQuotePricingMessage("isin2       ", 2, 5555L, 6666L),
                AckMessage.ACK_MESSAGE
        ), outputSpy.getSpy().receivedMessages());
        outputSpy.getSpy().clear();

        outputSpy.onInput(app.output(3));
        assertEquals(emptyList(), outputSpy.getSpy().receivedMessages());
        outputSpy.getSpy().clear();
    }

    @Test
    void shouldAlwaysGenerateMessageWith100PerCentProbability() {
        outputSpy.onInput(app.generateRandom(
                100, new Probabilities(new Probabilities.AckProbability(1000))
        ).output());

        assertEquals(100, outputSpy.getSpy().receivedMessages().size());
        assertEquals(AckMessage.ACK_MESSAGE, outputSpy.getSpy().receivedMessages().get(0));
        assertEquals(AckMessage.ACK_MESSAGE, outputSpy.getSpy().receivedMessages().get(99));
    }

    @Test
    void shouldNeverGenerateMessageWith0PerCentProbability() {
        outputSpy.onInput(app.generateRandom(
                100, new Probabilities(new Probabilities.AckProbability(0))
        ).output());

        assertEquals(0, outputSpy.getSpy().receivedMessages().size());
    }

    @Test
    void shouldGenerateMessageAccordingToItsProbability() {
        outputSpy.onInput(app.generateRandom(
                1000, new Probabilities(new Probabilities.AckProbability(500))
        ).output());

        assertThat(outputSpy.getSpy().receivedMessages()).hasSizeBetween(400, 600);
    }

    @Test
    void shouldTreatMessageProbabilitiesAsIndependent() {
        outputSpy.onInput(app.generateRandom(
                1000, new Probabilities(new Probabilities.AckProbability(500), quoteProbability()
                        .withPercentageProbability(500)
                        .withDistinctInstruments(100)
                        .withCancellationProbability(0).build())
        ).output());

        assertThat(outputSpy.getSpy().receivedMessages()).hasSizeBetween(900, 1100);
    }

    @Test
    void shouldDefineProbabilityOfQuoteWithNoPrice() {
        outputSpy.onInput(app.generateRandom(
                1000, new Probabilities(quoteProbability()
                        .withPercentageProbability(1000)
                        .withDistinctInstruments(100)
                        .withCancellationProbability(500).build())
        ).output());

        assertThat(outputSpy.getSpy().receivedMessages(QuotePricingMessage.class, q -> q.askPrice() == 0 && q.bidPrice() == 0)).hasSizeBetween(400, 600);
    }

    @Test
    void shouldDefineNumberOfDistinctInstruments() {
        outputSpy.onInput(app.generateRandom(
                1000, new Probabilities(quoteProbability()
                        .withPercentageProbability(1000)
                        .withDistinctInstruments(10)
                        .withCancellationProbability(0).build())
        ).output());

        Map<String, List<QuotePricingMessage>> quotesByIsin = outputSpy.getSpy().receivedMessages(QuotePricingMessage.class, Objects::nonNull).stream()
                .collect(groupingBy(q -> q.isin().toString()));
        assertThat(quotesByIsin).hasSize(10);
        quotesByIsin.forEach((isin, quotes) -> assertThat(quotes).hasSizeBetween(50, 150));
    }
}