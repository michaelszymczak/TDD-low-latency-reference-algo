package com.michaelszymczak.sample.tddrefalgo.other;


import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class CoalescingQueueTest {


    static <T> Stream<CoalescingQueue<T>> stableImplementationsProvider() {
        return Stream.of(new ReferenceCoalescingQueue<>());
    }

    static <T> Stream<CoalescingQueue<T>> unstableImplementationsProvider() {
        return Stream.of(new LowLatencyCoalescingQueue<>());
    }

    static <T> Stream<CoalescingQueue<T>> allImplementationsProvider() {
        return Stream.concat(stableImplementationsProvider(), unstableImplementationsProvider());
    }

    @ParameterizedTest
    @MethodSource("allImplementationsProvider")
    void shouldBeEmptyUponConstruction(CoalescingQueue<String> queue) {
        assertThat(queue.size()).isEqualTo(0);
        assertThat(queue.poll()).isNull();
    }

    @ParameterizedTest
    @MethodSource("allImplementationsProvider")
    void shouldReturnOnlyElement(CoalescingQueue<String> queue) {
        queue.add("key", "element");

        assertThat(queue.size()).isEqualTo(1);
        assertThat(queue.poll()).isEqualTo("element");
    }

    @ParameterizedTest
    @MethodSource("allImplementationsProvider")
    void shouldRemoveElementWhenPolled(CoalescingQueue<String> queue) {
        queue.add("key", "element");
        queue.poll();

        assertThat(queue.size()).isEqualTo(0);
        assertThat(queue.poll()).isNull();
    }

    @ParameterizedTest
    @MethodSource("allImplementationsProvider")
    void shouldCountElementsIndividuallyIfKeysAreDifferent(CoalescingQueue<String> queue) {
        queue.add("key1", "element1");
        queue.add("key2", "element2");

        assertThat(queue.size()).isEqualTo(2);
    }

    @ParameterizedTest
    @MethodSource("allImplementationsProvider")
    void shouldPlaceNewElementAfterExistingOneIfKeysAreDifferent(CoalescingQueue<String> queue) {
        queue.add("key1", "element1");
        queue.add("key2", "element2");

        assertThat(queue.poll()).isEqualTo("element1");
    }

    @ParameterizedTest
    @MethodSource("allImplementationsProvider")
    void shouldReplaceElementWithTheSameKey(CoalescingQueue<String> queue) {
        queue.add("key1", "element1a");
        queue.add("key1", "element1b");

        assertThat(queue.size()).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("allImplementationsProvider")
    void shouldReturnReplacedElement(CoalescingQueue<String> queue) {
        queue.add("key1", "element1a");
        queue.add("key1", "element1b");

        assertThat(queue.poll()).isEqualTo("element1b");
        assertThat(queue.size()).isEqualTo(0);
    }

    @ParameterizedTest
    @MethodSource("allImplementationsProvider")
    void shouldRemovePreviousElementWithTheSameKey(CoalescingQueue<String> queue) {
        queue.add("key1", "element1a");
        queue.add("key1", "element1b");
        queue.add("key2", "element2");
        queue.poll();

        assertThat(queue.poll()).isEqualTo("element2");
        assertThat(queue.size()).isEqualTo(0);

    }

    @ParameterizedTest
    @MethodSource("allImplementationsProvider")
    void shouldPollElementsInFIFOOrderIfKeysAreDifferent(CoalescingQueue<String> queue) {
        queue.add("key1", "element1");
        queue.add("key2", "element2");
        queue.add("key3", "element3");

        assertThat(queue.poll()).isEqualTo("element1");
        assertThat(queue.poll()).isEqualTo("element2");
        assertThat(queue.poll()).isEqualTo("element3");
    }

    @ParameterizedTest
    @MethodSource("allImplementationsProvider")
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
    @MethodSource("allImplementationsProvider")
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
    @MethodSource("allImplementationsProvider")
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