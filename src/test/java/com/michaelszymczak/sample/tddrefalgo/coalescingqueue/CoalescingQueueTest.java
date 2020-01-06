package com.michaelszymczak.sample.tddrefalgo.coalescingqueue;


import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.michaelszymczak.sample.tddrefalgo.coalescingqueue.CoalescingQueue.DROP_EVICTED_ELEMENT;
import static org.assertj.core.api.Assertions.assertThat;

class CoalescingQueueTest {


    static <T> EvictedElementSpy<T> evictedElementSpy() {
        return new EvictedElementSpy<>();
    }

    static <T> Stream<CoalescingQueue<T>> referenceImplementationsProvider() {
        return Stream.of(new ReferenceCoalescingQueue<>());
    }

    static <T> Stream<CoalescingQueue<T>> lowLatencyImplementationsProvider() {
        return Stream.of(new LowLatencyCoalescingQueue<>());
    }

    static <T> Stream<CoalescingQueue<T>> allImplementationsProvider() {
        return Stream.concat(referenceImplementationsProvider(), lowLatencyImplementationsProvider());
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
        EvictedElementSpy<String> evictedElementSpy = evictedElementSpy();

        queue.add("key", "element", evictedElementSpy);

        assertThat(queue.size()).isEqualTo(1);
        assertThat(queue.poll()).isEqualTo("element");
        assertThat(evictedElementSpy.evicted).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("allImplementationsProvider")
    void shouldRemoveElementWhenPolled(CoalescingQueue<String> queue) {
        queue.add("key", "element", DROP_EVICTED_ELEMENT);
        queue.poll();

        assertThat(queue.size()).isEqualTo(0);
        assertThat(queue.poll()).isNull();
    }

    @ParameterizedTest
    @MethodSource("allImplementationsProvider")
    void shouldCountElementsIndividuallyIfKeysAreDifferent(CoalescingQueue<String> queue) {
        queue.add("key1", "element1", DROP_EVICTED_ELEMENT);
        queue.add("key2", "element2", DROP_EVICTED_ELEMENT);

        assertThat(queue.size()).isEqualTo(2);
    }

    @ParameterizedTest
    @MethodSource("allImplementationsProvider")
    void shouldPlaceNewElementAfterExistingOneIfKeysAreDifferent(CoalescingQueue<String> queue) {
        EvictedElementSpy<String> evictedElementSpy = evictedElementSpy();
        queue.add("key1", "element1", DROP_EVICTED_ELEMENT);

        queue.add("key2", "element2", evictedElementSpy);

        assertThat(queue.poll()).isEqualTo("element1");
        assertThat(evictedElementSpy.evicted).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("allImplementationsProvider")
    void shouldReplaceElementWithTheSameKey(CoalescingQueue<String> queue) {
        EvictedElementSpy<String> evictedElementSpy = evictedElementSpy();
        queue.add("key1", "element1a", DROP_EVICTED_ELEMENT);

        queue.add("key1", "element1b", evictedElementSpy);

        assertThat(queue.size()).isEqualTo(1);
        assertThat(evictedElementSpy.evicted).containsExactly("element1a");
    }

    @ParameterizedTest
    @MethodSource("allImplementationsProvider")
    void shouldReturnReplacedElement(CoalescingQueue<String> queue) {
        queue.add("key1", "element1a", DROP_EVICTED_ELEMENT);

        queue.add("key1", "element1b", DROP_EVICTED_ELEMENT);

        assertThat(queue.poll()).isEqualTo("element1b");
        assertThat(queue.size()).isEqualTo(0);
    }

    @ParameterizedTest
    @MethodSource("allImplementationsProvider")
    void shouldRemovePreviousElementWithTheSameKey(CoalescingQueue<String> queue) {
        queue.add("key1", "element1a", DROP_EVICTED_ELEMENT);
        queue.add("key1", "element1b", DROP_EVICTED_ELEMENT);
        queue.add("key2", "element2", DROP_EVICTED_ELEMENT);
        queue.poll();

        assertThat(queue.poll()).isEqualTo("element2");
        assertThat(queue.size()).isEqualTo(0);

    }

    @ParameterizedTest
    @MethodSource("allImplementationsProvider")
    void shouldPollElementsInFIFOOrderIfKeysAreDifferent(CoalescingQueue<String> queue) {
        queue.add("key1", "element1", DROP_EVICTED_ELEMENT);
        queue.add("key2", "element2", DROP_EVICTED_ELEMENT);
        queue.add("key3", "element3", DROP_EVICTED_ELEMENT);

        assertThat(queue.poll()).isEqualTo("element1");
        assertThat(queue.poll()).isEqualTo("element2");
        assertThat(queue.poll()).isEqualTo("element3");
    }

    @ParameterizedTest
    @MethodSource("allImplementationsProvider")
    void shouldKeepThePositionOfTheOriginalElementWithTheSameKey(CoalescingQueue<String> queue) {
        queue.add("key1", "element1", DROP_EVICTED_ELEMENT);
        queue.add("key2", "element2", DROP_EVICTED_ELEMENT);
        queue.add("key3", "element3", DROP_EVICTED_ELEMENT);
        queue.add("key2", "element2a", DROP_EVICTED_ELEMENT);

        assertThat(queue.poll()).isEqualTo("element1");
        assertThat(queue.poll()).isEqualTo("element2a");
        assertThat(queue.poll()).isEqualTo("element3");
    }

    @ParameterizedTest
    @MethodSource("allImplementationsProvider")
    void shouldSupportArbitraryType(CoalescingQueue<Integer> queue) {
        queue.add("key1", 1, DROP_EVICTED_ELEMENT);
        queue.add("key2", 2, DROP_EVICTED_ELEMENT);
        queue.add("key3", 3, DROP_EVICTED_ELEMENT);
        queue.add("key2", 20, DROP_EVICTED_ELEMENT);

        assertThat(queue.poll()).isEqualTo(1);
        assertThat(queue.poll()).isEqualTo(20);
        assertThat(queue.poll()).isEqualTo(3);
    }

    @ParameterizedTest
    @MethodSource("allImplementationsProvider")
    void shouldWorkEvenIfKeysMutatedAfterwards(CoalescingQueue<String> queue) {
        EvictedElementSpy<String> evictedElementSpy = evictedElementSpy();
        StringBuilder key = new StringBuilder();
        key.append("key1");
        queue.add(key, "element1", evictedElementSpy);
        key.setLength(0);
        key.append("key2");
        queue.add(key, "element2", evictedElementSpy);
        key.setLength(0);
        key.append("key3");
        queue.add(key, "element3", evictedElementSpy);
        key.setLength(0);
        key.append("key2");
        queue.add(key, "element2a", evictedElementSpy);
        key.setLength(0);

        assertThat(queue.poll()).isEqualTo("element1");
        assertThat(queue.poll()).isEqualTo("element2a");
        assertThat(queue.poll()).isEqualTo("element3");
        assertThat(evictedElementSpy.evicted).containsExactly("element2");
    }

    private static class EvictedElementSpy<T> implements CoalescingQueue.EvictedElementListener<T> {

        List<T> evicted = new ArrayList<>();

        @Override
        public void onEvicted(T evictedElement) {
            evicted.add(evictedElement);
        }
    }
}