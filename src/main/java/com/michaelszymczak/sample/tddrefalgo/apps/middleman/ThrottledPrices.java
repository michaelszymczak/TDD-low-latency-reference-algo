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
        if (windowFull()) return;
        publisher.publishQuote(isin, tier, bidPrice, askPrice);
        inFlightMessages++;
    }

    public void onCancel(String isin, int tier) {
        if (windowFull()) return;
        publisher.publishCancel(isin, tier);
        inFlightMessages++;
    }

    public void onAck() {
        inFlightMessages = 0;
    }

    private boolean windowFull() {
        return inFlightMessages >= windowSize;
    }
}
