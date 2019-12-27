package com.michaelszymczak.sample.tddrefalgo.apps.middleman;

import com.michaelszymczak.sample.tddrefalgo.apps.middleman.support.ThrottledPricesPublisherSpy;
import org.junit.jupiter.api.Test;

import static com.michaelszymczak.sample.tddrefalgo.apps.middleman.support.ThrottledPricesPublisherSpy.heartbeat;

class ThrottledPricesTest {

    private final ThrottledPricesPublisherSpy publisherSpy = new ThrottledPricesPublisherSpy();
    private final ThrottledPrices throttledPrices = new ThrottledPrices(publisherSpy);

    @Test
    void shouldRespondToHeartbeats() {
        throttledPrices.onHeartbeat(123L);

        publisherSpy.assertPublished(heartbeat(123L));
    }

}