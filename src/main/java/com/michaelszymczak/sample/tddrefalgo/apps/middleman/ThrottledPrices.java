package com.michaelszymczak.sample.tddrefalgo.apps.middleman;

class ThrottledPrices {

    private final ThrottledPricesPublisher publisher;

    ThrottledPrices(ThrottledPricesPublisher publisher) {
        this.publisher = publisher;
    }

    void onHeartbeat(long nanoTime) {
        publisher.publishHeartbeat(nanoTime);
    }
}
