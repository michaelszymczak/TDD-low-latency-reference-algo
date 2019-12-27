package com.michaelszymczak.sample.tddrefalgo.apps.middleman;

class ThrottledPrices {

    private final ThrottledPricesPublisher publisher;

    ThrottledPrices(ThrottledPricesPublisher publisher) {
        this.publisher = publisher;
    }

    void onHeartbeat(long nanoTime) {
        publisher.publishHeartbeat(nanoTime);
    }

    public void onQuoteUpdate(String isin, int tier, long bidPrice, long askPrice) {
        publisher.publishQuote(isin, tier, bidPrice, askPrice);
    }

    public void onCancel(String isin, int tier) {
        publisher.publishCancel(isin, tier);
    }
}
