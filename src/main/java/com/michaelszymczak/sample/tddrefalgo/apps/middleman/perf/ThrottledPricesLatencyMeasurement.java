package com.michaelszymczak.sample.tddrefalgo.apps.middleman.perf;

import com.michaelszymczak.sample.tddrefalgo.apps.marketmaker.MarketMakerApp;
import com.michaelszymczak.sample.tddrefalgo.apps.middleman.MiddleManApp;
import com.michaelszymczak.sample.tddrefalgo.apps.middleman.ThrottledPricesPublisher;
import com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain.ThrottledPrices;
import com.michaelszymczak.sample.tddrefalgo.framework.api.io.Output;
import net.openhft.chronicle.core.jlbh.JLBH;
import net.openhft.chronicle.core.jlbh.JLBHOptions;
import net.openhft.chronicle.core.jlbh.JLBHResultConsumer;
import net.openhft.chronicle.core.jlbh.JLBHTask;
import org.agrona.DirectBuffer;

import java.util.ArrayList;
import java.util.List;

import static com.michaelszymczak.sample.tddrefalgo.apps.marketmaker.support.MarketUpdatesGenerator.generateMarketUpdates;

public class ThrottledPricesLatencyMeasurement implements JLBHTask {

    private final ComputationEnforcer computationEnforcer = new ComputationEnforcer();
    private MarketActivitySimulation marketActivitySimulation;
    private JLBH jlbh;

    private ThrottledPricesLatencyMeasurement(final MarketActivitySimulation generated) {
        this.marketActivitySimulation = generated;
    }

    public static void run(final MarketActivitySimulation generated) {
        //Given
        new JLBH(
                new JLBHOptions()
                        .warmUpIterations(1_000_000)
                        .iterations(500_000)
                        .throughput(100_000)
                        .runs(3)
                        .recordOSJitter(true)
                        .accountForCoordinatedOmmission(true)
                        .jlbhTask(new ThrottledPricesLatencyMeasurement(generated)),
                System.out,
                JLBHResultConsumer.newThreadSafeInstance()
        ).start();
    }

    @Override
    public void init(JLBH jlbh) {
        this.jlbh = jlbh;
    }

    @Override
    public void run(long startTimeNS) {
        int fakeResult = marketActivitySimulation.processSingleMessage();
        jlbh.sampleNanos(System.nanoTime() - startTimeNS);
        computationEnforcer.process(fakeResult);
    }

    @Override
    public void complete() {
        if (!computationEnforcer.hasProcessed()) {
            throw new IllegalStateException();
        }
    }

    static class ComputationEnforcer {
        private int result = -1;

        void process(int result) {
            this.result = result;
        }

        boolean hasProcessed() {
            return result != -1;
        }
    }

    public interface SutFactory {
        ThrottledPrices create(ThrottledPricesPublisher publisher);
    }

    public static class MarketActivitySimulation {

        private final int totalNumberOfOutputs = 10;
        private MiddleManApp middleManApp;
        private List<Output> outputs;
        private int outputNumber;
        private int initialOffsetOfThisOutput;
        private int initialLengthOfThisOutput;
        private int finalPositionOfThisOutput;
        private int currentOffsetOfThisOutput;
        private DirectBuffer bufferOfThisOutput;
        private SutFactory throttledPricesFactory;

        public MarketActivitySimulation withSut(final SutFactory throttledPricesFactory) {
            this.throttledPricesFactory = throttledPricesFactory;
            return this;
        }

        public MarketActivitySimulation withGeneratedEvents(final int ackPerMilProbability) {
            final int priceUpdatesPerRound = 50_000;
            final int publisherCapacity = 5 * 1024 * 1024;
            final MarketMakerApp app = generateMarketUpdates(
                    publisherCapacity, totalNumberOfOutputs, priceUpdatesPerRound, ackPerMilProbability);
            outputs = new ArrayList<>(totalNumberOfOutputs);
            for (int i = 0; i < totalNumberOfOutputs; i++) {
                outputs.add(app.output(i + 1));
            }
            middleManApp = new MiddleManApp(publisherCapacity, throttledPricesFactory::create);
            outputNumber = -1;
            initialOffsetOfThisOutput = 0;
            initialLengthOfThisOutput = 0;
            finalPositionOfThisOutput = 0;
            currentOffsetOfThisOutput = Integer.MAX_VALUE;
            bufferOfThisOutput = null;
            return this;
        }

        int processSingleMessage() {
            if (currentOffsetOfThisOutput >= finalPositionOfThisOutput) {
                middleManApp.output().reset();
                outputNumber = (outputNumber + 1) % totalNumberOfOutputs;
                initialOffsetOfThisOutput = outputs.get(outputNumber).offset();
                initialLengthOfThisOutput = outputs.get(outputNumber).length();
                finalPositionOfThisOutput = outputs.get(outputNumber).offset() + initialLengthOfThisOutput;
                currentOffsetOfThisOutput = outputs.get(outputNumber).offset();
                bufferOfThisOutput = outputs.get(outputNumber).buffer();
            }
            int howMuchReadFromThisOutput = currentOffsetOfThisOutput - initialOffsetOfThisOutput;
            int remainingLengthOfThisOutput = initialLengthOfThisOutput - howMuchReadFromThisOutput;
            currentOffsetOfThisOutput = middleManApp.onInput(
                    bufferOfThisOutput,
                    currentOffsetOfThisOutput,
                    remainingLengthOfThisOutput,
                    true
            );
            return currentOffsetOfThisOutput;
        }
    }
}
