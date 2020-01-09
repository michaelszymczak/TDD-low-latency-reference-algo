package com.michaelszymczak.sample.tddrefalgo.coalescingqueue;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.michaelszymczak.sample.tddrefalgo.coalescingqueue.CoalescingQueue.DROP_EVICTED_ELEMENT;
import static org.assertj.core.api.Assertions.assertThat;

class LowLatencyCoalescingQueueTest {

    private final Object element = new Object();
    private final StringBuilder key = new StringBuilder();

    @Test
    void shouldNotAllocateInSteadyState() {
        // Given
        LowLatencyCoalescingQueue.AllocationCounter allocationsCounter = new LowLatencyCoalescingQueue.AllocationCounter();
        LowLatencyCoalescingQueue<Object> queue = new LowLatencyCoalescingQueue<>(allocationsCounter);
        run("someKey", queue, 1_000_000);
        long totalAllocationsBeforeEnteredSteadyState = allocationsCounter.totalCount();
        Map<String, Integer> allocationsBeforeEnteredSteadyState = new HashMap<>();
        allocationsBeforeEnteredSteadyState.put("CONSTRUCTOR", 4);
        allocationsBeforeEnteredSteadyState.put("NEW_KEY", 123);
        allocationsBeforeEnteredSteadyState.put("NEW_STRING_BUILDER_FOR_KEY", 124);
        allocationsBeforeEnteredSteadyState.put("NEW_WRAPPED_ELEMENT", 123);
        assertThat(allocationsCounter.countByLabel()).isEqualTo(allocationsBeforeEnteredSteadyState);
        assertThat(totalAllocationsBeforeEnteredSteadyState).isEqualTo(374);

        // When
        Map<String, Long> result = run("steadyStateKey", queue, 10_000_000);

        // Then
        assertThat(allocationsCounter.countByLabel()).isEqualTo(allocationsBeforeEnteredSteadyState);
        assertThat(queue.allocations()).isEqualTo(totalAllocationsBeforeEnteredSteadyState);
        assertThat(result.get("msgPerSecond")).isGreaterThan(3_000_000);
        assertThat(result.get("worstLatencyMicros")).isLessThan(500);
    }

    private Map<String, Long> run(final String keyPrefix, LowLatencyCoalescingQueue<Object> queue, final int iterations) {
        long worstLatency = 0;
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            long before = System.nanoTime();
            if (i % 128 == 0) {
                while (queue.poll() != null) {
                }
            } else if (i % 64 == 0) {
                queue.poll();
                queue.poll();
                queue.poll();
            } else if (i % 32 == 0) {
                queue.add(key(keyPrefix, iterations + i), element, DROP_EVICTED_ELEMENT);
            } else {
                queue.add(key(keyPrefix, i), element, DROP_EVICTED_ELEMENT);
            }
            long after = System.nanoTime();
            if (worstLatency < after - before) {
                worstLatency = after - before;
            }
        }
        long end = System.nanoTime();
        HashMap<String, Long> result = new HashMap<>();
        result.put("worstLatencyMicros", TimeUnit.NANOSECONDS.toMicros(worstLatency));
        result.put("msgPerSecond", TimeUnit.SECONDS.toNanos(iterations) / (end - start));
        return result;
    }

    private CharSequence key(final String prefix, int i) {
        key.setLength(0);
        key.append(prefix).append(i);
        return key;
    }
}