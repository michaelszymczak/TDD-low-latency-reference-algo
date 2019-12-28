package com.michaelszymczak.sample.tddrefalgo.apps.middleman;

import com.michaelszymczak.sample.tddrefalgo.apps.middleman.support.ThrottledPricesPublisherSpy;
import com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain.ThrottledPrices;
import org.junit.jupiter.api.Test;

import static com.michaelszymczak.sample.tddrefalgo.apps.middleman.support.ThrottledPricesPublisherSpy.*;

class ThrottledPricesTest {

    private final ThrottledPricesPublisherSpy publisherSpy = new ThrottledPricesPublisherSpy();

    @Test
    void shouldRespondToHeartbeats() {
        new ThrottledPrices(publisherSpy, 1).onHeartbeat(123L);

        publisherSpy.assertPublished(heartbeat(123L));
    }

    @Test
    void shouldSendFirstQuoteUpdateRightAfterReceipt() {
        new ThrottledPrices(publisherSpy, 1).onQuoteUpdate("isin", 3, 10055L, 10066L);

        publisherSpy.assertPublished(quote("isin", 3, 10055L, 10066L));
    }

    @Test
    void shouldSendFirstCancelRightAfterReceipt() {
        new ThrottledPrices(publisherSpy, 1).onCancel("isin");

        publisherSpy.assertPublished(cancel("isin"));
    }

    @Test
    void shouldThrottleQuotesToThePredefinedNumberBetweenAcks() {
        int windowSize = 1;
        ThrottledPrices throttledPrices = new ThrottledPrices(publisherSpy, windowSize);

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
        int windowSize = 1;
        ThrottledPrices throttledPrices = new ThrottledPrices(publisherSpy, windowSize);

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
        ThrottledPrices throttledPrices = new ThrottledPrices(publisherSpy, windowSize);

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
        int windowSize = 1;
        ThrottledPrices throttledPrices = new ThrottledPrices(publisherSpy, windowSize);

        // Given
        throttledPrices.onCancel("isin1");
        throttledPrices.onAck();
        publisherSpy.assertPublished(cancel("isin1"));

        // When
        throttledPrices.onCancel("isin2");

        // Then
        publisherSpy.assertPublished(
                cancel("isin1"),
                cancel("isin2")
        );
    }

    @Test
    void shouldThrottleQuotesAndCancelsTogether() {
        int windowSize = 2;
        ThrottledPrices throttledPrices = new ThrottledPrices(publisherSpy, windowSize);

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
        int windowSize = 2;
        ThrottledPrices throttledPrices = new ThrottledPrices(publisherSpy, windowSize);

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
}