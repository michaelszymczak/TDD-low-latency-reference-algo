package com.michaelszymczak.sample.tddrefalgo.apps.middleman;

import com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain.ReferenceThrottledPrices;
import com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain.ThrottledPrices;
import com.michaelszymczak.sample.tddrefalgo.apps.middleman.support.ThrottledPricesPublisherSpy;
import org.junit.jupiter.api.Test;

import java.util.function.IntConsumer;
import java.util.stream.IntStream;

import static com.michaelszymczak.sample.tddrefalgo.apps.middleman.support.ThrottledPricesPublisherSpy.*;

class ThrottledPricesTest {

    private final ThrottledPricesPublisherSpy publisherSpy = new ThrottledPricesPublisherSpy();

    @Test
    void shouldRespondToHeartbeats() {
        throttledPricesWithWindowOfSize(1).onHeartbeat(123L);

        publisherSpy.assertPublished(heartbeat(123L));
    }

    @Test
    void shouldSendFirstQuoteUpdateRightAfterReceipt() {
        throttledPricesWithWindowOfSize(1).onQuoteUpdate("isin", 3, 10055L, 10066L);

        publisherSpy.assertPublished(quote("isin", 3, 10055L, 10066L));
    }

    @Test
    void shouldSendFirstCancelRightAfterReceipt() {
        throttledPricesWithWindowOfSize(1).onCancel("isin");

        publisherSpy.assertPublished(cancel("isin"));
    }

    @Test
    void shouldThrottleQuotesToThePredefinedNumberBetweenAcks() {
        int windowSize = 1;
        ThrottledPrices throttledPrices = throttledPricesWithWindowOfSize(windowSize);

        // Given
        throttledPrices.onQuoteUpdate("isin1", 3, 10055L, 10066L);
        publisherSpy.assertPublished(quote("isin1", 3, 10055L, 10066L));

        // When
        throttledPrices.onQuoteUpdate("isin2", 4, 20055L, 20066L);

        // Then
        publisherSpy.assertPublished(quote("isin1", 3, 10055L, 10066L));
    }

    @Test
    void shouldPublishMoreQuotesWhenAckReceived() {
        ThrottledPrices throttledPrices = throttledPricesWithWindowOfSize(1);

        // Given
        throttledPrices.onQuoteUpdate("isin1", 3, 10055L, 10066L);
        throttledPrices.onAck();
        publisherSpy.assertPublished(quote("isin1", 3, 10055L, 10066L));

        // When
        throttledPrices.onQuoteUpdate("isin2", 4, 20055L, 20066L);

        // Then
        publisherSpy.assertPublished(
                quote("isin1", 3, 10055L, 10066),
                quote("isin2", 4, 20055L, 20066)
        );
    }


    @Test
    void shouldThrottleCancelsToThePredefinedNumberBetweenAcks() {
        int windowSize = 1;
        ThrottledPrices throttledPrices = throttledPricesWithWindowOfSize(windowSize);

        // Given
        throttledPrices.onCancel("isin1");
        publisherSpy.assertPublished(cancel("isin1"));

        // When
        throttledPrices.onCancel("isin2");

        // Then
        publisherSpy.assertPublished(cancel("isin1"));
    }

    @Test
    void shouldPublishMoreCancelsWhenAckReceived() {
        ThrottledPrices throttledPrices = throttledPricesWithWindowOfSize(1);

        // Given
        throttledPrices.onQuoteUpdate("isin1", 1, 1, 1);
        throttledPrices.onAck();
        publisherSpy.assertPublished(quote("isin1", 1, 1, 1));

        // When
        throttledPrices.onCancel("isin2");

        // Then
        publisherSpy.assertPublished(
                quote("isin1", 1, 1, 1),
                cancel("isin2")
        );
    }

    @Test
    void shouldThrottleQuotesAndCancelsTogether() {
        ThrottledPrices throttledPrices = throttledPricesWithWindowOfSize(2);

        // Given
        throttledPrices.onQuoteUpdate("isin1", 1, 100L, 200L);
        throttledPrices.onCancel("isin2");
        publisherSpy.assertPublished(
                quote("isin1", 1, 100L, 200L),
                cancel("isin2")
        );

        // When
        throttledPrices.onQuoteUpdate("isin3", 3, 300L, 400L);
        throttledPrices.onCancel("isin4");

        // Then
        publisherSpy.assertPublished(
                quote("isin1", 1, 100L, 200L),
                cancel("isin2")
        );
    }

    @Test
    void shouldAllowMoreQuotesAndCancelsWhenAckReceived() {
        ThrottledPrices throttledPrices = throttledPricesWithWindowOfSize(2);

        // Given
        throttledPrices.onQuoteUpdate("isin1", 1, 100L, 200L);
        throttledPrices.onCancel("isin2");
        throttledPrices.onAck();
        publisherSpy.assertPublished(
                quote("isin1", 1, 100L, 200L),
                cancel("isin2")
        );

        // When
        throttledPrices.onQuoteUpdate("isin3", 3, 300L, 400L);
        throttledPrices.onCancel("isin4");

        // Then
        publisherSpy.assertPublished(
                quote("isin1", 1, 100L, 200L),
                cancel("isin2"),
                quote("isin3", 3, 300L, 400L),
                cancel("isin4")
        );
    }

    @Test
    void shouldPublishPreviouslyEnqueuedQuoteWhenAckReceived() {
        ThrottledPrices throttledPrices = throttledPricesWithWindowOfSize(1);

        // Given
        throttledPrices.onQuoteUpdate("isin1", 3, 10055L, 10066L);
        throttledPrices.onQuoteUpdate("isin2", 4, 20055L, 20066L);
        publisherSpy.assertPublished(quote("isin1", 3, 10055L, 10066L));

        // When
        throttledPrices.onAck();

        // Then
        publisherSpy.assertPublished(
                quote("isin1", 3, 10055L, 10066),
                quote("isin2", 4, 20055L, 20066)
        );
    }

    @Test
    void shouldPublishPreviouslyEnqueuedCancelWhenAckReceived() {
        ThrottledPrices throttledPrices = throttledPricesWithWindowOfSize(1);

        // Given
        throttledPrices.onQuoteUpdate("isin1", 1, 1, 1);
        throttledPrices.onCancel("isin2");
        publisherSpy.assertPublished(quote("isin1", 1, 1, 1));

        // When
        throttledPrices.onAck();

        // Then
        publisherSpy.assertPublished(
                quote("isin1", 1, 1, 1),
                cancel("isin2")
        );
    }

    @Test
    void shouldPublishPreviouslyEnqueuedQuotesAndCancelsThatFitsTheWindowWhenAckReceived() {
        ThrottledPrices throttledPrices = throttledPricesWithWindowOfSize(4);

        // Given
        throttledPrices.onCancel("isin1");
        throttledPrices.onQuoteUpdate("isin2", 1, 20055L, 20066L);
        throttledPrices.onQuoteUpdate("isin3", 2, 30055L, 30066L);
        throttledPrices.onCancel("isin4");
        throttledPrices.onCancel("isin5");
        throttledPrices.onQuoteUpdate("isin6", 3, 60055L, 60066L);
        throttledPrices.onQuoteUpdate("isin7", 4, 70055L, 70066L);
        throttledPrices.onCancel("isin8");
        throttledPrices.onQuoteUpdate("isin9", 5, 90055L, 90066L);
        throttledPrices.onCancel("isin10");
        publisherSpy.clear();

        // When
        throttledPrices.onAck();

        // Then
        publisherSpy.assertPublished(
                cancel("isin5"),
                quote("isin6", 3, 60055L, 60066),
                quote("isin7", 4, 70055L, 70066),
                cancel("isin8")
        );
    }

    @Test
    void shouldEventuallyPublishAll() {
        ThrottledPrices throttledPrices = throttledPricesWithWindowOfSize(1);

        // When
        throttledPrices.onCancel("isin4");
        throttledPrices.onCancel("isin5");
        throttledPrices.onQuoteUpdate("isin6", 4, 60055L, 60066L);
        throttledPrices.onQuoteUpdate("isin7", 5, 70055L, 70066L);
        throttledPrices.onCancel("isin8");
        runTimes(10, throttledPrices::onAck);

        // Then
        publisherSpy.assertPublished(
                cancel("isin4"),
                cancel("isin5"),
                quote("isin6", 4, 60055L, 60066),
                quote("isin7", 5, 70055L, 70066),
                cancel("isin8")
        );
    }

    @Test
    void shouldSquashSameIsinCancellationsBeforePublishing() {
        ThrottledPrices throttledPrices = throttledPricesWithWindowOfSize(1);
        throttledPrices.onCancel("isin1");
        publisherSpy.clear();

        // When
        throttledPrices.onCancel("isin2");
        throttledPrices.onCancel("isin2");
        throttledPrices.onCancel("isin2");
        throttledPrices.onAck();

        // Then
        publisherSpy.assertPublished(cancel("isin2"));
        assertNoMoreItemsPublished(throttledPrices);
    }

    @Test
    void shouldRemoveQuotesWithTheSameIsinsWhenCancelling() {
        ThrottledPrices throttledPrices = throttledPricesWithWindowOfSize(4);

        // Given
        runTimes(4, i -> throttledPrices.onQuoteUpdate("otherisin" + i, 1, 1, 1));
        publisherSpy.clear();

        // When
        throttledPrices.onQuoteUpdate("isin1", 1, 111, 112);
        throttledPrices.onQuoteUpdate("isin1", 2, 121, 122);
        throttledPrices.onQuoteUpdate("isin2", 1, 211, 212);
        throttledPrices.onQuoteUpdate("isin2", 2, 221, 222);
        throttledPrices.onQuoteUpdate("isin3", 1, 311, 312);
        throttledPrices.onQuoteUpdate("isin3", 2, 321, 322);
        throttledPrices.onCancel("isin1");
        throttledPrices.onCancel("isin3");
        throttledPrices.onAck();

        // Then
        publisherSpy.assertPublished(
                cancel("isin1"),
                quote("isin2", 1, 211, 212),
                quote("isin2", 2, 221, 222),
                cancel("isin3")
        );
        assertNoMoreItemsPublished(throttledPrices);
    }

    @Test
    void shouldReplaceQuotesWithTheSameIsinAndTier() {
        ThrottledPrices throttledPrices = throttledPricesWithWindowOfSize(5);

        // Given
        runTimes(5, i -> throttledPrices.onQuoteUpdate("otherisin" + i, 1, 1, 1));
        publisherSpy.clear();
        throttledPrices.onQuoteUpdate("otherisin", 1, 111, 112);
        throttledPrices.onQuoteUpdate("isin", 1, 111, 112);
        throttledPrices.onQuoteUpdate("isin", 5, 521, 522);
        throttledPrices.onQuoteUpdate("isin", 3, 130, 131);
        throttledPrices.onQuoteUpdate("isin", 1, 113, 114);
        throttledPrices.onQuoteUpdate("isin", 5, 525, 526);
        throttledPrices.onQuoteUpdate("isin", 4, 241, 242);

        // When
        throttledPrices.onAck();

        // Then
        publisherSpy.assertPublished(
                quote("otherisin", 1, 111, 112),
                quote("isin", 1, 113, 114),
                quote("isin", 5, 525, 526),
                quote("isin", 3, 130, 131),
                quote("isin", 4, 241, 242)
        );
        assertNoMoreItemsPublished(throttledPrices);
    }

    @Test
    void shouldReplaceSameQuoteWithTheSameIsinAndTierMultipleTimes() {
        ThrottledPrices throttledPrices = throttledPricesWithWindowOfSize(2);

        // Given
        throttledPrices.onQuoteUpdate("isin101", 1, 1, 1);
        throttledPrices.onQuoteUpdate("isin102", 1, 1, 1);
        publisherSpy.clear();
        throttledPrices.onQuoteUpdate("otherisin", 1, 111, 112);
        throttledPrices.onQuoteUpdate("isin", 1, 111, 112);
        throttledPrices.onQuoteUpdate("isin", 1, 113, 114);
        throttledPrices.onQuoteUpdate("isin", 1, 115, 116);

        // When
        throttledPrices.onAck();

        // Then
        publisherSpy.assertPublished(
                quote("otherisin", 1, 111, 112),
                quote("isin", 1, 115, 116)
        );
        assertNoMoreItemsPublished(throttledPrices);
    }

    @Test
    void shouldAddQuoteWithTheSameIsinAfterTheCancelAndKeepItAsItCanBeUsedToCancelOtherTiers() {
        ThrottledPrices throttledPrices = throttledPricesWithWindowOfSize(2);

        // Given
        runTimes(2, i -> throttledPrices.onCancel("otherisin" + i));
        publisherSpy.clear();
        throttledPrices.onQuoteUpdate("isin2", 2, 211, 212);
        throttledPrices.onCancel("isin1");
        throttledPrices.onQuoteUpdate("isin1", 1, 111, 112);
        throttledPrices.onQuoteUpdate("isin1", 2, 121, 122);
        throttledPrices.onCancel("isin1");
        throttledPrices.onQuoteUpdate("isin1", 3, 130, 131);
        throttledPrices.onQuoteUpdate("isin1", 4, 130, 131);
        throttledPrices.onCancel("isin1");
        throttledPrices.onQuoteUpdate("isin1", 3, 132, 133);
        throttledPrices.onQuoteUpdate("isin1", 4, 142, 143);
        throttledPrices.onQuoteUpdate("isin1", 3, 131, 132);
        throttledPrices.onQuoteUpdate("isin1", 4, 141, 142);

        // When
        throttledPrices.onAck();
        throttledPrices.onAck();

        // Then
        publisherSpy.assertPublished(
                quote("isin2", 2, 211, 212),
                cancel("isin1"),
                quote("isin1", 3, 131, 132),
                quote("isin1", 4, 141, 142)
        );
        assertNoMoreItemsPublished(throttledPrices);
    }

    private void assertNoMoreItemsPublished(ThrottledPrices throttledPrices) {
        publisherSpy.clear();
        throttledPrices.onAck();
        publisherSpy.assertPublishedNothing();
    }

    private ThrottledPrices throttledPricesWithWindowOfSize(int windowSize) {
        return new ReferenceThrottledPrices(publisherSpy, windowSize);
    }


    private void runTimes(int times, IntConsumer runnable) {
        IntStream.range(0, times).forEach(runnable);
    }

    private void runTimes(int times, Runnable runnable) {
        IntStream.range(0, times).forEach(i -> runnable.run());
    }
}