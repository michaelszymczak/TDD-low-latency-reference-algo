package com.michaelszymczak.sample.tddrefalgo.apps.middleman.support;

import com.michaelszymczak.sample.tddrefalgo.apps.middleman.ThrottledPricesPublisher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ThrottledPricesPublisherSpy implements ThrottledPricesPublisher {

    private final List<Object> published = new ArrayList<>();

    public void assertPublished(final Object... expected) {
        assertThat(published).usingRecursiveFieldByFieldElementComparator().isEqualTo(Arrays.asList(expected));
    }

    @Override
    public void publishHeartbeat(long nanoTime) {
        published.add(heartbeat(nanoTime));
    }

    public static Heartbeat heartbeat(long nanoTime) {
        Heartbeat heartbeat = new Heartbeat();
        heartbeat.nanoTime = nanoTime;
        return heartbeat;
    }

    public static class Heartbeat {
        long nanoTime;
    }
}
