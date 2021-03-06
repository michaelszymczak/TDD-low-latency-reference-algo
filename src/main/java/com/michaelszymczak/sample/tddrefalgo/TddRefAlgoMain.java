package com.michaelszymczak.sample.tddrefalgo;

import com.michaelszymczak.sample.tddrefalgo.apps.marketmaker.MarketMakerApp;
import com.michaelszymczak.sample.tddrefalgo.apps.middleman.MiddleManApp;
import com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain.ReferenceThrottledPrices;
import com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain.SimpleLowLatencyThrottledPrices;
import com.michaelszymczak.sample.tddrefalgo.apps.middleman.perf.MarketActivitySimulation;
import com.michaelszymczak.sample.tddrefalgo.apps.middleman.perf.ThrottledPricesAllocationsMeasurement;
import com.michaelszymczak.sample.tddrefalgo.apps.middleman.perf.ThrottledPricesLatencyMeasurement;
import com.michaelszymczak.sample.tddrefalgo.coalescingqueue.perf.ComponentTestingTask;
import com.michaelszymczak.sample.tddrefalgo.support.OutputSpy;
import com.michaelszymczak.sample.tddrefalgo.support.PricingProtocolDecodedMessageSpy;

import java.util.Arrays;

import static com.michaelszymczak.sample.tddrefalgo.support.OutputSpy.outputSpy;

public class TddRefAlgoMain {

    private static final int PUBLISHER_CAPACITY = 5 * 1024 * 1024;
    private final MarketMakerApp marketMakerApp = new MarketMakerApp(System::nanoTime, PUBLISHER_CAPACITY);
    private final int windowSize;
    private final MiddleManApp middleManApp;

    public static void main(String[] args) {
        if (args.length > 0 && "perfQueue".equals(args[0])) {
            ComponentTestingTask.sampleRun();
        } else if (args.length > 0 && "perfRefPricer".equals(args[0])) {
            System.out.println("Reference implementation latency measurement");
            ThrottledPricesLatencyMeasurement.run(new MarketActivitySimulation()
                    .withSut(publisher -> new ReferenceThrottledPrices(publisher, 500))
                    .withGeneratedEvents(2));
        } else if (args.length > 0 && "perfPricer".equals(args[0])) {
            System.out.println("Implementation latency measurement");
            ThrottledPricesLatencyMeasurement.run(new MarketActivitySimulation()
                    .withSut(publisher -> new SimpleLowLatencyThrottledPrices(publisher, 500))
                    .withGeneratedEvents(2));
        } else if (args.length > 0 && "perfRefPricerAlloc".equals(args[0])) {
            System.out.println("Reference implementation allocations measurement");
            ThrottledPricesAllocationsMeasurement.run(new MarketActivitySimulation()
                    .withSut(publisher -> new ReferenceThrottledPrices(publisher, 500))
                    .withGeneratedEvents(2), 1_000_000, 10_000);
        } else if (args.length > 0 && "perfPricerAlloc".equals(args[0])) {
            System.out.println("Reference implementation allocations measurement");
            ThrottledPricesAllocationsMeasurement.run(new MarketActivitySimulation()
                    .withSut(publisher -> new SimpleLowLatencyThrottledPrices(publisher, 500))
                    .withGeneratedEvents(2), 1_000_000_000, 1_000_000);
        } else {
            System.out.println(Arrays.toString(args));
            System.out.println(new TddRefAlgoMain(2).process(
                    "" +
                            "Q/   isin1/  1/     4455/   4466\n" +
                            "Q/   isin2/  2/     7755/   8866\n" +
                            "Q/   isin3/  0/     0/         0\n" +
                            "A\n" +
                            "Q/   isin4/  0/     0/         0\n" +
                            "Q/   isin5/  5/     1234/   5678\n" +
                            "A\n"));
        }
    }

    public TddRefAlgoMain(final int windowSize) {
        this.windowSize = windowSize;
        middleManApp = new MiddleManApp(PUBLISHER_CAPACITY, this.windowSize);
    }

    public String process(final String messages) {

        marketMakerApp.events(messages);
        OutputSpy<PricingProtocolDecodedMessageSpy> spy = outputSpy();
        middleManApp.onInput(marketMakerApp.output());
        spy.onInput(middleManApp.output());
        return spy.getSpy().receivedMessages().toString();
    }

}
