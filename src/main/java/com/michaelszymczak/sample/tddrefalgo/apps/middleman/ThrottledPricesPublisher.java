package com.michaelszymczak.sample.tddrefalgo.apps.middleman;

public interface ThrottledPricesPublisher {

    void publishHeartbeat(long nanoTime);

    void publishQuote(CharSequence isin, int tier, long bidPrice, long askPrice);

    void publishCancel(CharSequence isin);
}
