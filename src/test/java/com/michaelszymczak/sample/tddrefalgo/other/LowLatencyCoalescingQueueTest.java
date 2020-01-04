package com.michaelszymczak.sample.tddrefalgo.other;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LowLatencyCoalescingQueueTest {

    private final Object element = new Object();
    private final StringBuilder key = new StringBuilder();

    @Test
    void shouldNotAllocateInSteadyState() {
        int uniqueKeys = 10_000;
        // Given
        LowLatencyCoalescingQueue<Object> queue = warmedUpQueue("warmUpKey", uniqueKeys);
        long allocationsBeforeEnteredSteadyState = queue.allocations();

        // When
        for (int i = 0; i < uniqueKeys; i++) {
            key.setLength(0);
            key.append("steadyStateKey").append(i);
            queue.add(key, element);
            if (i % 10 == 0) {
                queue.poll();
            }
        }

        // Then
        assertThat(queue.allocations()).isEqualTo(allocationsBeforeEnteredSteadyState);
    }

    private LowLatencyCoalescingQueue<Object> warmedUpQueue(final String keyPrefix, int uniqueKeys) {
        LowLatencyCoalescingQueue<Object> queue = new LowLatencyCoalescingQueue<>();
        for (int i = 0; i < uniqueKeys; i++) {
            key.setLength(0);
            key.append(keyPrefix).append(i);
            queue.add(key, element);
        }
        for (int i = 0; i < uniqueKeys; i++) {
            queue.poll();
        }
        return queue;
    }
}