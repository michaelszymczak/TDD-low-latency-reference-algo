package com.michaelszymczak.sample.tddrefalgo.coalescingqueue;

import com.michaelszymczak.sample.tddrefalgo.coalescingqueue.perf.ComponentTestingTask;
import net.openhft.chronicle.core.jlbh.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.stream.Stream;

import static java.time.Duration.ofMillis;
import static java.time.Duration.ofNanos;
import static org.assertj.core.api.Assertions.assertThat;

class CoalescingQueuePerformanceTest {

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
    @MethodSource("lowLatencyImplementationsProvider")
    void shouldBeOfAcceptableLatencyUnderHighLoad(CoalescingQueue<Object> queue) {
        //Given
        final JLBHResultConsumer results = JLBHResultConsumer.newThreadSafeInstance();
        JLBHOptions jlbhOptions = parametersWhenTesting(queue, 5_000_000, 300_000);
        final JLBH jlbh = new JLBH(jlbhOptions, System.out, results);

        //When
        jlbh.start();

        //Then
        JLBHResult.RunResult latency = results.get().endToEnd().summaryOfLastRun();
        assertThat(latency.get50thPercentile())
                .describedAs("50th percentile is " + latency.get50thPercentile())
                .isLessThan(ofNanos(500));
        assertThat(latency.get9999thPercentile())
                .describedAs("99.99th percentile is " + latency.get9999thPercentile())
                .isLessThan(ms(1));
        assertThat(latency.getWorst())
                .describedAs("worst is " + latency.getWorst())
                .isLessThan(ms(1));
    }

    @ParameterizedTest
    @MethodSource("referenceImplementationsProvider")
    void shouldBeOfAcceptableLatencyUnderMediumLoad(CoalescingQueue<Object> queue) {
        //Given
        final JLBHResultConsumer results = JLBHResultConsumer.newThreadSafeInstance();
        JLBHOptions jlbhOptions = parametersWhenTesting(queue, 50_000, 10_000);
        final JLBH jlbh = new JLBH(jlbhOptions, System.out, results);

        //When
        jlbh.start();

        //Then
        JLBHResult.RunResult latency = results.get().endToEnd().summaryOfLastRun();
        assertThat(latency.get50thPercentile())
                .describedAs("50th percentile is " + latency.get50thPercentile())
                .isLessThan(ofNanos(500));
        assertThat(latency.get9999thPercentile())
                .describedAs("99.99th percentile is " + latency.get9999thPercentile())
                .isLessThan(ms(5));
        assertThat(latency.getWorst())
                .describedAs("worst is " + latency.getWorst())
                .isLessThan(ms(10));
    }

    private JLBHOptions parametersWhenTesting(final CoalescingQueue<Object> sut, final int iterations, final int throughput) {
        return new JLBHOptions()
                .warmUpIterations(500_000)
                .iterations(iterations) // 50_000 - for reference
                .throughput(throughput) // 10_000 - for reference
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


}