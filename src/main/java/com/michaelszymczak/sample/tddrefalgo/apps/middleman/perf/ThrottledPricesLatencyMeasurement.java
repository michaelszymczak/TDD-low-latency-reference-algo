package com.michaelszymczak.sample.tddrefalgo.apps.middleman.perf;

import com.michaelszymczak.sample.tddrefalgo.apps.middleman.ThrottledPricesPublisher;
import com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain.ThrottledPrices;
import net.openhft.chronicle.core.jlbh.JLBH;
import net.openhft.chronicle.core.jlbh.JLBHOptions;
import net.openhft.chronicle.core.jlbh.JLBHResultConsumer;
import net.openhft.chronicle.core.jlbh.JLBHTask;

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
                        .warmUpIterations(3_000_000)
                        .iterations(1_000_000)
                        .throughput(100_000)
                        .runs(4)
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

}
