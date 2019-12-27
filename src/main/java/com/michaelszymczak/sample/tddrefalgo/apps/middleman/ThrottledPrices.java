package com.michaelszymczak.sample.tddrefalgo.apps.middleman;

class ThrottledPrices {

    private final ThrottledPricesPublisher publisher;
    private final int windowSize;
    private int inFlightMessages = 0;

    ThrottledPrices(ThrottledPricesPublisher publisher, int windowSize) {
        this.publisher = publisher;
        this.windowSize = windowSize;
    }

    void onHeartbeat(long nanoTime) {
        publisher.publishHeartbeat(nanoTime);
    }

    public void onQuoteUpdate(String isin, int tier, long bidPrice, long askPrice) {
        if (inFlightMessages >= windowSize) {
            return;
        }
        publisher.publishQuote(isin, tier, bidPrice, askPrice);
        inFlightMessages++;
    }

    public void onCancel(String isin, int tier) {
        // TODO: check window size
        publisher.publishCancel(isin, tier);
        // TODO: increase in-flight messages
    }

    public void onAck() {
        inFlightMessages = 0;
    }
}
