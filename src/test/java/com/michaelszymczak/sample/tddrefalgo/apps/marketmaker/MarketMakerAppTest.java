package com.michaelszymczak.sample.tddrefalgo.apps.marketmaker;

import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.AckMessage;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.ImmutableHeartbeatPricingMessage;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.ImmutableQuotePricingMessage;
import com.michaelszymczak.sample.tddrefalgo.testsupport.OutputSpy;
import com.michaelszymczak.sample.tddrefalgo.testsupport.RelativeNanoClockWithTimeFixedTo;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

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

        outputSpy.onInput(app
                // TODO: parse from string
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

}