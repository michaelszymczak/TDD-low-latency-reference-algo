package com.michaelszymczak.sample.tddrefalgo.apps.middleman;

import com.michaelszymczak.sample.tddrefalgo.apps.marketmaker.MarketMakerApp;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.ImmutableHeartbeatPricingMessage;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.ImmutableQuotePricingMessage;
import com.michaelszymczak.sample.tddrefalgo.support.OutputSpy;
import com.michaelszymczak.sample.tddrefalgo.support.PricingProtocolDecodedMessageSpy;
import com.michaelszymczak.sample.tddrefalgo.testsupport.RelativeNanoClockWithTimeFixedTo;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MiddleManAppTracerBulletTest {

    private final MiddleManApp middleManApp = new MiddleManApp(1024, 3);
    private final OutputSpy<PricingProtocolDecodedMessageSpy> outputSpy = OutputSpy.outputSpy();
    private final MarketMakerApp marketMakerApp = new MarketMakerApp(new RelativeNanoClockWithTimeFixedTo(12345L), 5 * 1024 * 1024);

    @Test
    void shouldProcessMessages() {
        // Given
        marketMakerApp.heartbeat();
        marketMakerApp
                .events("Q/   isin1/  1/     4455/   4466\n" +
                        "Q/   isin2/  2/     7755/   8866\n" +
                        "Q/   isin3/  0/     0/         0\n" +
                        "A\n" +
                        "Q/   isin4/  0/     0/         0\n" +
                        "Q/   isin5/  5/     1234/   5678\n" +
                        "A\n");

        // When
        middleManApp.onInput(marketMakerApp.output());

        // Then
        outputSpy.onInput(middleManApp.output());
        outputSpy.getSpy().receivedMessages().forEach(System.out::println);
        assertEquals(Arrays.asList(
                new ImmutableHeartbeatPricingMessage(12345L),
                new ImmutableQuotePricingMessage("isin1       ", 1, 4455L, 4466L),
                new ImmutableQuotePricingMessage("isin2       ", 2, 7755L, 8866L),
                new ImmutableQuotePricingMessage("isin3       ", 0, 0L, 0L),
                new ImmutableQuotePricingMessage("isin4       ", 0, 0L, 0L),
                new ImmutableQuotePricingMessage("isin5       ", 5, 1234L, 5678L)
        ), outputSpy.getSpy().receivedMessages());
    }
}