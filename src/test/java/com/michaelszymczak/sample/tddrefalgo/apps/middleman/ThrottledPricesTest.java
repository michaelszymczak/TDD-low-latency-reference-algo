package com.michaelszymczak.sample.tddrefalgo.apps.middleman;

import com.michaelszymczak.sample.tddrefalgo.apps.middleman.support.ThrottledPricesPublisherSpy;
import org.junit.jupiter.api.Test;

import static com.michaelszymczak.sample.tddrefalgo.apps.middleman.support.ThrottledPricesPublisherSpy.*;

class ThrottledPricesTest {

    private final ThrottledPricesPublisherSpy publisherSpy = new ThrottledPricesPublisherSpy();
    private final ThrottledPrices throttledPrices = new ThrottledPrices(publisherSpy);

    @Test
    void shouldRespondToHeartbeats() {
        throttledPrices.onHeartbeat(123L);

        publisherSpy.assertPublished(heartbeat(123L));
    }

    @Test
    void shouldSendFirstQuoteUpdateRightAfterReceipt() {
        throttledPrices.onQuoteUpdate("isin", 3, 10055L, 10066L);

        publisherSpy.assertPublished(quote("isin", 3, 10055L, 10066L));
    }

    @Test
    void shouldSendFirstCancelRightAfterReceipt() {
        throttledPrices.onCancel("isin", 4);

        publisherSpy.assertPublished(cancel("isin", 4));
    }
}