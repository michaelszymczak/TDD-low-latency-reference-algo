package com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain;

public interface ThrottledPrices {

    void onHeartbeat(long nanoTime);

    void onQuoteUpdate(CharSequence isin, int tier, long bidPrice, long askPrice);

    void onCancel(CharSequence isin);

    void onAck();
}
