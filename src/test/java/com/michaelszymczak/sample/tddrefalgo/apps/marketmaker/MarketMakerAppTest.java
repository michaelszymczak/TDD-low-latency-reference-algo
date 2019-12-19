package com.michaelszymczak.sample.tddrefalgo.apps.marketmaker;

import com.michaelszymczak.sample.tddrefalgo.apps.marketmaker.support.Probabilities;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.AckMessage;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.ImmutableHeartbeatPricingMessage;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.ImmutableQuotePricingMessage;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.QuotePricingMessage;
import com.michaelszymczak.sample.tddrefalgo.testsupport.OutputSpy;
import com.michaelszymczak.sample.tddrefalgo.testsupport.RelativeNanoClockWithTimeFixedTo;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MarketMakerAppTest {

    private final OutputSpy outputSpy = new OutputSpy();

    @Test
    void shouldBeIdleIfUnprompted() {
        assertEquals(0, new MarketMakerApp().output().length());
    }

    @Test
    void shouldHeartbeat() {
        MarketMakerApp app = new MarketMakerApp(new RelativeNanoClockWithTimeFixedTo(12345L));

        outputSpy.onInput(app.heartbeat().output());

        assertEquals(1, outputSpy.receivedMessages().size());
        assertEquals(new ImmutableHeartbeatPricingMessage(12345L), outputSpy.receivedMessages().get(0));
    }

    @Test
    void shouldSendEventsWhenAskedTo() {
        MarketMakerApp app = new MarketMakerApp();

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
        ), outputSpy.receivedMessages());
    }

    @Test
    void shouldGenerateEventsForLater() {
        MarketMakerApp app = new MarketMakerApp();

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
        assertEquals(emptyList(), outputSpy.receivedMessages());
        outputSpy.clear();

        outputSpy.onInput(app.output(0));
        assertEquals(emptyList(), outputSpy.receivedMessages());
        outputSpy.clear();

        outputSpy.onInput(app.output(1));
        assertEquals(Arrays.asList(
                new ImmutableQuotePricingMessage("isin1       ", 1, 4455L, 4466L),
                AckMessage.ACK_MESSAGE
        ), outputSpy.receivedMessages());
        outputSpy.clear();

        outputSpy.onInput(app.output(2));
        assertEquals(Arrays.asList(
                new ImmutableQuotePricingMessage("isin2       ", 2, 5555L, 6666L),
                AckMessage.ACK_MESSAGE
        ), outputSpy.receivedMessages());
        outputSpy.clear();

        outputSpy.onInput(app.output(3));
        assertEquals(emptyList(), outputSpy.receivedMessages());
        outputSpy.clear();
    }

    @Test
    void shouldAlwaysGenerateMessageWith100PerCentProbability() {
        MarketMakerApp app = new MarketMakerApp();

        outputSpy.onInput(app.generateRandom(
                100, new Probabilities(new Probabilities.AckProbability(100))
        ).output());

        assertEquals(100, outputSpy.receivedMessages().size());
        assertEquals(AckMessage.ACK_MESSAGE, outputSpy.receivedMessages().get(0));
        assertEquals(AckMessage.ACK_MESSAGE, outputSpy.receivedMessages().get(99));
    }

    @Test
    void shouldNeverGenerateMessageWith0PerCentProbability() {
        MarketMakerApp app = new MarketMakerApp();

        outputSpy.onInput(app.generateRandom(
                100, new Probabilities(new Probabilities.AckProbability(0))
        ).output());

        assertEquals(0, outputSpy.receivedMessages().size());
    }

    @Test
    void shouldGenerateMessageAccordingToItsProbability() {
        MarketMakerApp app = new MarketMakerApp();

        outputSpy.onInput(app.generateRandom(
                1000, new Probabilities(new Probabilities.AckProbability(50))
        ).output());

        assertThat(outputSpy.receivedMessages()).hasSizeBetween(400, 600);
    }

    @Test
    void shouldTreatMessageProbabilitiesAsIndependent() {
        MarketMakerApp app = new MarketMakerApp();

        outputSpy.onInput(app.generateRandom(
                1000, new Probabilities(new Probabilities.AckProbability(50), new Probabilities.QuoteProbability(10, 50, 0, 0))
        ).output());

        assertThat(outputSpy.receivedMessages()).hasSizeBetween(900, 1100);
    }

    @Test
    void shouldDefineProbabilityOfQuoteWithNoPrice() {
        MarketMakerApp app = new MarketMakerApp();

        outputSpy.onInput(app.generateRandom(
                1000, new Probabilities(new Probabilities.QuoteProbability(10, 100, 50, 0))
        ).output());

        assertThat(outputSpy.receivedMessages(
                QuotePricingMessage.class,
                q -> q.askPrice() == 0 && q.bidPrice() == 0
        )).hasSizeBetween(400, 600);
    }

    @Test
    void shouldDefineProbabilityOfQuoteWithNoTier() {
        MarketMakerApp app = new MarketMakerApp();

        outputSpy.onInput(app.generateRandom(
                1000, new Probabilities(new Probabilities.QuoteProbability(10, 100, 0, 30))
        ).output());

        assertThat(outputSpy.receivedMessages(
                QuotePricingMessage.class,
                q -> q.priceTier() == 0
        )).hasSizeBetween(200, 400);
    }

    @Test
    void shouldDefineNumberOfDistinctInstruments() {
        MarketMakerApp app = new MarketMakerApp();

        outputSpy.onInput(app.generateRandom(
                1000, new Probabilities(new Probabilities.QuoteProbability(10, 100, 0, 0))
        ).output());

        Map<String, List<QuotePricingMessage>> quotesByIsin = outputSpy
                .receivedMessages(QuotePricingMessage.class, Objects::nonNull).stream()
                .collect(groupingBy(q -> q.isin().toString()));
        assertThat(quotesByIsin).hasSize(10);
        quotesByIsin.forEach((isin, quotes) -> assertThat(quotes).hasSizeBetween(50, 150));
    }
}