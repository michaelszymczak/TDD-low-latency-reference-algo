package com.michaelszymczak.sample.tddrefalgo.other;

import net.openhft.chronicle.core.jlbh.*;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static java.time.Duration.ofMillis;
import static java.time.Duration.ofNanos;
import static org.assertj.core.api.Assertions.assertThat;

class LowLatencyCoalescingQueuePerformanceTest {

    @Test
    void shouldNotAllocateInSteadyState() {
        //Given
        final JLBHResultConsumer results = JLBHResultConsumer.newThreadSafeInstance();
        JLBHOptions jlbhOptions = parametersWhenTesting(new LowLatencyCoalescingQueue<>());
        final JLBH jlbh = new JLBH(jlbhOptions, System.out, results);

        //When
        jlbh.start();

        //Then
        JLBHResult.RunResult latency = results.get().endToEnd().summaryOfLastRun();
        assertThat(latency.get50thPercentile()).isLessThan(us(1));
        assertThat(latency.get999thPercentile()).isLessThan(us(100));
        assertThat(latency.getWorst()).isLessThan(ms(1));
    }

    private JLBHOptions parametersWhenTesting(final CoalescingQueue<Object> sut) {
        return new JLBHOptions()
                .warmUpIterations(50_000)
                .iterations(50_000)
                .throughput(10_000)
                .runs(3)
                .recordOSJitter(true)
                .accountForCoordinatedOmmission(true)
                .jlbhTask(new ComponentTestingTask(sut));
    }

    private Duration us(int us) {
        return ofNanos(us * 1000);
    }

    private Duration ms(int ms) {
        return ofMillis(ms);
    }

    class ComponentTestingTask implements JLBHTask {

        private final CoalescingQueue<Object> sut;
        private final StringBuilder key = new StringBuilder();
        private final Object element = new Object();
        private final ComputationEnforcer computationEnforcer = new ComputationEnforcer();

        private JLBH jlbh;
        private int iteration = 0;

        ComponentTestingTask(final CoalescingQueue<Object> queue) {
            this.sut = queue;
        }

        @Override
        public void init(JLBH jlbh) {
            this.jlbh = jlbh;
        }

        @Override
        public void run(long startTimeNS) {
            Object fakeResult = runOnce(sut, iteration++);
            jlbh.sampleNanos(System.nanoTime() - startTimeNS);
            computationEnforcer.process(fakeResult);
        }

        @Override
        public void complete() {
            assertThat(computationEnforcer.hasProcessed()).isTrue();
        }


        private Object runOnce(CoalescingQueue<Object> queue, final int iteration) {
            Object fakeResult = null;
            if (iteration % 128 == 0) {
                while (queue.poll() != null) {
                }
                fakeResult = queue.poll();
            } else if (iteration % 64 == 0) {
                queue.poll();
                queue.poll();
                queue.poll();
            } else if (iteration % 32 == 0) {
                queue.add(key("keyPrefix", 10_000_000 + iteration), element);
            } else {
                queue.add(key("keyPrefix", iteration), element);
            }
            return fakeResult;
        }


        private CharSequence key(final String prefix, int i) {
            key.setLength(0);
            key.append(prefix).append(i);
            return key;
        }

        private class ComputationEnforcer {
            private Object object = new Object();

            void process(Object object) {
                this.object = object;
            }

            boolean hasProcessed() {
                return object == null;
            }
        }
    }


}