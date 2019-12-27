package com.michaelszymczak.sample.tddrefalgo.apps.middleman;

public interface ThrottledPricesPublisher {
    void publishHeartbeat(long nanoTime);
}
