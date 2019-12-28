package com.michaelszymczak.sample.tddrefalgo.apps.middleman;

import com.michaelszymczak.sample.tddrefalgo.apps.marketmaker.MarketMakerApp;
import com.michaelszymczak.sample.tddrefalgo.testsupport.OutputSpy;
import com.michaelszymczak.sample.tddrefalgo.testsupport.PricingProtocolDecodedMessageSpy;
import com.michaelszymczak.sample.tddrefalgo.testsupport.RelativeNanoClockWithTimeFixedTo;
import org.agrona.ExpandableArrayBuffer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MiddleManAppPropertyTest {

    private final MiddleManApp middleManApp = new MiddleManApp(1024, 1);
    private final OutputSpy<PricingProtocolDecodedMessageSpy> outputSpy = OutputSpy.outputSpy();
    private final MarketMakerApp marketMakerApp = new MarketMakerApp(new RelativeNanoClockWithTimeFixedTo(12345L), 5 * 1024 * 1024);

    @Test
    void shouldNotProduceSideEffectsUnprompted() {
        // When
        middleManApp.onInput(new ExpandableArrayBuffer(), 0, 0);

        // Then
        outputSpy.onInput(middleManApp.output());
        assertThat(outputSpy.getSpy().receivedMessages()).isEmpty();
    }

    @Test
    void shouldProduceSideEffects() {
        // Given
        marketMakerApp.heartbeat();

        // When
        middleManApp.onInput(marketMakerApp.output());

        // Then
        outputSpy.onInput(middleManApp.output());
        assertThat(outputSpy.getSpy().receivedMessages()).isNotEmpty();
    }

    @Test
    @Disabled
    void shouldProduceCorrectOutputs() {
    }

    @Test
    @Disabled
    void shouldHandleHighThroughput() {
    }

    @Test
    @Disabled
    void shouldHaveLowLatency() {

    }
}