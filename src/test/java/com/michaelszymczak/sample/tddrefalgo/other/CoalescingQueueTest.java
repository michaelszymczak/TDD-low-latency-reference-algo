package com.michaelszymczak.sample.tddrefalgo.other;


import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class CoalescingQueueTest {


    static <T> List<CoalescingQueue<T>> stableImplementationsProvider() {
        return singletonList(new ReferenceCoalescingQueue<>());
    }

    static <T> List<CoalescingQueue<T>> allImplementationsProvider() {
        return Arrays.asList(new ReferenceCoalescingQueue<>(), new LowLatencyCoalescingQueue<>());
    }

    @ParameterizedTest
    @MethodSource("allImplementationsProvider")
    void shouldBeEmptyUponConstruction(CoalescingQueue<String> queue) {
        assertThat(queue.size()).isEqualTo(0);
        assertThat(queue.poll()).isNull();
    }

    @ParameterizedTest
    @MethodSource("stableImplementationsProvider")
    void shouldReturnOnlyElement(CoalescingQueue<String> queue) {
        queue.add("key", "element");

        assertThat(queue.size()).isEqualTo(1);
        assertThat(queue.poll()).isEqualTo("element");
    }

    @ParameterizedTest
    @MethodSource("stableImplementationsProvider")
    void shouldRemoveElementWhenPolled(CoalescingQueue<String> queue) {
        queue.add("key", "element");
        queue.poll();

        assertThat(queue.size()).isEqualTo(0);
        assertThat(queue.poll()).isNull();
    }

    @ParameterizedTest
    @MethodSource("stableImplementationsProvider")
    void shouldCountElementsIndividuallyIfKeysAreDifferent(CoalescingQueue<String> queue) {
        queue.add("key1", "element1");
        queue.add("key2", "element2");

        assertThat(queue.size()).isEqualTo(2);
    }

    @ParameterizedTest
    @MethodSource("stableImplementationsProvider")
    void shouldPlaceNewElementAfterExistingOneIfKeysAreDifferent(CoalescingQueue<String> queue) {
        queue.add("key1", "element1");
        queue.add("key2", "element2");

        assertThat(queue.poll()).isEqualTo("element1");
    }

    @ParameterizedTest
    @MethodSource("stableImplementationsProvider")
    void shouldReplaceElementWithTheSameKey(CoalescingQueue<String> queue) {
        queue.add("key1", "element1");
        queue.add("key1", "element2");

        assertThat(queue.poll()).isEqualTo("element2");
    }

    @ParameterizedTest
    @MethodSource("stableImplementationsProvider")
    void shouldPollElementsInFIFOOrderIfKeysAreDifferent(CoalescingQueue<String> queue) {
        queue.add("key1", "element1");
        queue.add("key2", "element2");
        queue.add("key3", "element3");

        assertThat(queue.poll()).isEqualTo("element1");
        assertThat(queue.poll()).isEqualTo("element2");
        assertThat(queue.poll()).isEqualTo("element3");
    }

    @ParameterizedTest
    @MethodSource("stableImplementationsProvider")
    void shouldKeepThePositionOfTheOriginalElementWithTheSameKey(CoalescingQueue<String> queue) {
        queue.add("key1", "element1");
        queue.add("key2", "element2");
        queue.add("key3", "element3");
        queue.add("key2", "element2a");

        assertThat(queue.poll()).isEqualTo("element1");
        assertThat(queue.poll()).isEqualTo("element2a");
        assertThat(queue.poll()).isEqualTo("element3");
    }

    @ParameterizedTest
    @MethodSource("stableImplementationsProvider")
    void shouldSupportArbitraryType(CoalescingQueue<Integer> queue) {
        queue.add("key1", 1);
        queue.add("key2", 2);
        queue.add("key3", 3);
        queue.add("key2", 20);

        assertThat(queue.poll()).isEqualTo(1);
        assertThat(queue.poll()).isEqualTo(20);
        assertThat(queue.poll()).isEqualTo(3);
    }

    @ParameterizedTest
    @MethodSource("stableImplementationsProvider")
    void shouldWorkEvenIfKeysMutatedAfterwards(CoalescingQueue<String> queue) {
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