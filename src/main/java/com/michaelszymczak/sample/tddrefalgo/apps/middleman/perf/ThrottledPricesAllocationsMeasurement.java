package com.michaelszymczak.sample.tddrefalgo.apps.middleman.perf;

public class ThrottledPricesAllocationsMeasurement {

    private final ComputationEnforcer computationEnforcer = new ComputationEnforcer();
    private final MarketActivitySimulation marketActivitySimulation;

    private ThrottledPricesAllocationsMeasurement(final MarketActivitySimulation generated) {
        this.marketActivitySimulation = generated;
    }

    public static void run(final MarketActivitySimulation generated, final int iterations, final int progressEveryNth) {
        ThrottledPricesAllocationsMeasurement measurement = new ThrottledPricesAllocationsMeasurement(generated);
        for (int i = 0; i < iterations; i++) {
            measurement.doWork();
            if (i % progressEveryNth == 0) {
                printProgress(iterations, i);
            }
        }
        measurement.complete();

    }

    private static void printProgress(int iterations, int i) {
        System.out.println("Progress: " + i + "/" + iterations);
    }

    private void doWork() {
        int fakeResult = marketActivitySimulation.processSingleMessage();
        computationEnforcer.process(fakeResult);
    }

    private void complete() {
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
}
