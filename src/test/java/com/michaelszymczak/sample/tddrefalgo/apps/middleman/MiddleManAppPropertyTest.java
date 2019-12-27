package com.michaelszymczak.sample.tddrefalgo.apps.middleman;

import com.michaelszymczak.sample.tddrefalgo.testsupport.OutputSpy;
import com.michaelszymczak.sample.tddrefalgo.testsupport.PricingProtocolDecodedMessageSpy;
import org.agrona.ExpandableArrayBuffer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MiddleManAppPropertyTest {

    private final MiddleManApp app = new MiddleManApp();
    private final OutputSpy<PricingProtocolDecodedMessageSpy> outputSpy = OutputSpy.outputSpy();

    @Test
    void shouldNotProduceSideEffectsUnprompted() {
        app.onInput(new ExpandableArrayBuffer(), 0, 0);

        outputSpy.onInput(app.output());

        assertThat(outputSpy.getSpy().receivedMessages()).isEmpty();
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