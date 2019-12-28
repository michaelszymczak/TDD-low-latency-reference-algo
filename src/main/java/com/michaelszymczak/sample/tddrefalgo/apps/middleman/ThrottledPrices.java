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
        validate(isin, tier, bidPrice, askPrice);
        if (windowFull()) return;
        publisher.publishQuote(isin, tier, bidPrice, askPrice);
        inFlightMessages++;
    }

    private void validate(CharSequence isin, int tier, long bidPrice, long askPrice) {
        if (isin.length() == 0 || tier == 0 || (askPrice == 0 && bidPrice == 0)) {
            throw new IllegalArgumentException("Invalid quote update");
        }
    }

    public void onCancel(CharSequence isin) {
        if (windowFull()) return;
        publisher.publishCancel(isin);
        inFlightMessages++;
    }

    public void onAck() {
        inFlightMessages = 0;
    }

    private boolean windowFull() {
        return inFlightMessages >= windowSize;
    }
}
