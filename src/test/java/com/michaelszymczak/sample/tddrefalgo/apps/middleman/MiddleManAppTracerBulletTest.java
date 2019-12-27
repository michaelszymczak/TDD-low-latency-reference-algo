package com.michaelszymczak.sample.tddrefalgo.apps.middleman;

import com.michaelszymczak.sample.tddrefalgo.apps.marketmaker.MarketMakerApp;
import com.michaelszymczak.sample.tddrefalgo.testsupport.OutputSpy;
import com.michaelszymczak.sample.tddrefalgo.testsupport.PricingProtocolDecodedMessageSpy;
import com.michaelszymczak.sample.tddrefalgo.testsupport.RelativeNanoClockWithTimeFixedTo;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MiddleManAppTracerBulletTest {

    private final MiddleManApp middleManApp = new MiddleManApp(1024, 3);
    private final OutputSpy<PricingProtocolDecodedMessageSpy> outputSpy = OutputSpy.outputSpy();
    private final MarketMakerApp marketMakerApp = new MarketMakerApp(new RelativeNanoClockWithTimeFixedTo(12345L));

    @Test
    @Disabled
    void shouldProcessMessages() {
        // Given
        marketMakerApp.heartbeat();
        marketMakerApp
                .events("Q/   isin1/  1/     4455/   4466\n" +
                        "Q/   isin2/  2/     7755/   8866\n" +
                        "Q/   isin3/  3/     0/         0\n" +
                        "A\n" +
                        "Q/   isin4/  4/     0/         0\n" +
                        "Q/   isin5/  5/     1234/   5678\n" +
                        "A\n");

        // When
        middleManApp.onInput(marketMakerApp.output());

        // Then
        outputSpy.onInput(middleManApp.output());
        assertThat(outputSpy.getSpy().receivedMessages()).hasSize(6);
    }
}