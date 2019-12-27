package com.michaelszymczak.sample.tddrefalgo.apps.middleman;

import com.michaelszymczak.sample.tddrefalgo.apps.middleman.support.ThrottledPricesPublisherSpy;
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
        new ThrottledPrices(publisherSpy, 1).onCancel("isin", 4);

        publisherSpy.assertPublished(cancel("isin", 4));
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

}