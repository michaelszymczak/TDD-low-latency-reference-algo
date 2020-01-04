package com.michaelszymczak.sample.tddrefalgo.other;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CoalescingQueueTest {

    private final CoalescingQueue<String> queue = new ReferenceCoalescingQueue<>();

    @Test
    void shouldBeEmptyUponConstruction() {
        assertThat(queue.size()).isEqualTo(0);
        assertThat(queue.poll()).isNull();
    }

    @Test
    void shouldReturnOnlyElement() {
        queue.add("key", "element");

        assertThat(queue.size()).isEqualTo(1);
        assertThat(queue.poll()).isEqualTo("element");
    }

    @Test
    void shouldRemoveElementWhenPolled() {
        queue.add("key", "element");
        queue.poll();

        assertThat(queue.size()).isEqualTo(0);
        assertThat(queue.poll()).isNull();
    }

    @Test
    void shouldCountElementsIndividuallyIfKeysAreDifferent() {
        queue.add("key1", "element1");
        queue.add("key2", "element2");

        assertThat(queue.size()).isEqualTo(2);
    }

    @Test
    void shouldPlaceNewElementAfterExistingOneIfKeysAreDifferent() {
        queue.add("key1", "element1");
        queue.add("key2", "element2");

        assertThat(queue.poll()).isEqualTo("element1");
    }

    @Test
    void shouldReplaceElementWithTheSameKey() {
        queue.add("key1", "element1");
        queue.add("key1", "element2");

        assertThat(queue.poll()).isEqualTo("element2");
    }

    @Test
    void shouldPollElementsInFIFOOrderIfKeysAreDifferent() {
        queue.add("key1", "element1");
        queue.add("key2", "element2");
        queue.add("key3", "element3");

        assertThat(queue.poll()).isEqualTo("element1");
        assertThat(queue.poll()).isEqualTo("element2");
        assertThat(queue.poll()).isEqualTo("element3");
    }

    @Test
    void shouldKeepThePositionOfTheOriginalElementWithTheSameKey() {
        queue.add("key1", "element1");
        queue.add("key2", "element2");
        queue.add("key3", "element3");
        queue.add("key2", "element2a");

        assertThat(queue.poll()).isEqualTo("element1");
        assertThat(queue.poll()).isEqualTo("element2a");
        assertThat(queue.poll()).isEqualTo("element3");
    }

    @Test
    void shouldSupportArbitraryType() {
        final CoalescingQueue<Integer> numberQueue = new ReferenceCoalescingQueue<>();
        numberQueue.add("key1", 1);
        numberQueue.add("key2", 2);
        numberQueue.add("key3", 3);
        numberQueue.add("key2", 20);

        assertThat(numberQueue.poll()).isEqualTo(1);
        assertThat(numberQueue.poll()).isEqualTo(20);
        assertThat(numberQueue.poll()).isEqualTo(3);
    }

    @Test
    void shouldWorkEvenIfKeysMutatedAfterwards() {
        StringBuilder key = new StringBuilder();
        key.append("key1");
        queue.add(key, "element1");
        key.setLength(0);
        key.append("key2");
        queue.add(key, "element2");
        key.setLength(0);
        key.append("key3");
        queue.add(key, "element3");
        key.setLength(0);
        key.append("key2");
        queue.add(key, "element2a");
        key.setLength(0);

        assertThat(queue.poll()).isEqualTo("element1");
        assertThat(queue.poll()).isEqualTo("element2a");
        assertThat(queue.poll()).isEqualTo("element3");
    }
}