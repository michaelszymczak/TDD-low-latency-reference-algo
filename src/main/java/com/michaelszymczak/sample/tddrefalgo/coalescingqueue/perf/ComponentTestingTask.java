package com.michaelszymczak.sample.tddrefalgo.coalescingqueue.perf;

import com.michaelszymczak.sample.tddrefalgo.coalescingqueue.CoalescingQueue;
import com.michaelszymczak.sample.tddrefalgo.coalescingqueue.LowLatencyCoalescingQueue;
import net.openhft.chronicle.core.jlbh.JLBH;
import net.openhft.chronicle.core.jlbh.JLBHOptions;
import net.openhft.chronicle.core.jlbh.JLBHResultConsumer;
import net.openhft.chronicle.core.jlbh.JLBHTask;

import static com.michaelszymczak.sample.tddrefalgo.coalescingqueue.CoalescingQueue.DROP_EVICTED_ELEMENT;

public class ComponentTestingTask implements JLBHTask {

    private final CoalescingQueue<Object> sut;
    private final StringBuilder key = new StringBuilder();
    private final Object element = new Object();
    private final ComputationEnforcer computationEnforcer = new ComputationEnforcer();

    private JLBH jlbh;
    private int iteration = 0;

    public static void sampleRun() {
        //Given
        new JLBH(
                new JLBHOptions()
                        .warmUpIterations(1_000_000)
                        .iterations(10_000_000)
                        .throughput(300_000)
                        .runs(5)
                        .recordOSJitter(true)
                        .accountForCoordinatedOmmission(true)
                        .jlbhTask(new ComponentTestingTask(new LowLatencyCoalescingQueue<>())),
                System.out,
                JLBHResultConsumer.newThreadSafeInstance()
        ).start();
    }

    public ComponentTestingTask(final CoalescingQueue<Object> queue) {
        this.sut = queue;
    }

    @Override
    public void init(JLBH jlbh) {
        this.jlbh = jlbh;
    }

    @Override
    public void run(long startTimeNS) {
        int fakeResult = runOnce(sut, iteration++);
        jlbh.sampleNanos(System.nanoTime() - startTimeNS);
        computationEnforcer.process(fakeResult);
    }

    @Override
    public void complete() {
        if (!computationEnforcer.hasProcessed()) {
            throw new IllegalStateException();
        }
    }


    private int runOnce(CoalescingQueue<Object> queue, final int iteration) {
        if (iteration % 128 == 0) {
            while (queue.poll() != null) {
            }
        } else if (iteration % 64 == 0) {
            queue.poll();
            queue.poll();
            queue.poll();
        } else if (iteration % 32 == 0) {
            queue.add(key("keyPrefix", 10_000_000 + iteration), element, DROP_EVICTED_ELEMENT);
        } else {
            queue.add(key("keyPrefix", iteration), element, DROP_EVICTED_ELEMENT);
        }
        return queue.size();
    }


    private CharSequence key(final String prefix, int i) {
        key.setLength(0);
        key.append(prefix).append(i);
        return key;
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
