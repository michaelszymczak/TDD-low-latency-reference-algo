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

    public void onQuoteUpdate(CharSequence isin, int tier, long bidPrice, long askPrice) {
        if (askPrice == 0 && bidPrice == 0) {
            throw new IllegalArgumentException("Bid and ask price cannot be both zero");
        }
        if (windowFull()) return;
        publisher.publishQuote(isin, tier, bidPrice, askPrice);
        inFlightMessages++;
    }

    public void onCancel(CharSequence isin, int tier) {
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
