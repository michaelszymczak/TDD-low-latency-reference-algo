package com.michaelszymczak.sample.tddrefalgo.apps.middleman;

import com.michaelszymczak.sample.tddrefalgo.apps.marketmaker.MarketMakerApp;
import com.michaelszymczak.sample.tddrefalgo.support.OutputSpy;
import com.michaelszymczak.sample.tddrefalgo.support.PricingProtocolDecodedMessageSpy;
import com.michaelszymczak.sample.tddrefalgo.testsupport.RelativeNanoClockWithTimeFixedTo;

import static java.lang.String.format;
import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;

class Process {

    private final String messageDelimiter;
    private final int windowSize;
    private final MarketMakerApp marketMakerApp;

    Process(final int publisherCapacity, final String messageDelimiter, final int windowSize) {
        this.messageDelimiter = messageDelimiter;
        this.windowSize = windowSize;
        this.marketMakerApp = new MarketMakerApp(
                new RelativeNanoClockWithTimeFixedTo(12345L), publisherCapacity);
    }

    PricingProtocolDecodedMessageSpy process(
            String inputEvents, final MiddleManApp middleManApp) {
        marketMakerApp.output().reset();
        final OutputSpy<PricingProtocolDecodedMessageSpy> outputSpy = OutputSpy.outputSpy();
        MiddleManApp middleManAppWithFullWindow = fillTheWindow(outputSpy, middleManApp);
        marketMakerApp.events(messageDelimiter, inputEvents);
        middleManAppWithFullWindow.onInput(marketMakerApp.output());
        marketMakerApp.output().reset();
        outputSpy.getSpy().clear();
        outputSpy.onInput(middleManAppWithFullWindow.output());
        return outputSpy.getSpy();
    }

    private MiddleManApp fillTheWindow(final OutputSpy<PricingProtocolDecodedMessageSpy> outputSpy, final MiddleManApp middleManApp) {
        range(0, windowSize).forEach(i -> marketMakerApp.events(
                format("Q/   otherisin%d/  1/     1/   1", i)
        ));
        outputSpy.getSpy().clear();
        middleManApp.onSingleReaderInput(marketMakerApp.output());
        outputSpy.onSingleReaderInput(middleManApp.output());
        assertThat(outputSpy.getSpy().receivedMessages()).hasSize(windowSize);
        outputSpy.getSpy().clear();
        return middleManApp;
    }
}
