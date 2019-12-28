package com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain;

import com.michaelszymczak.sample.tddrefalgo.apps.middleman.ThrottledPricesPublisher;

import java.util.LinkedList;

public class ThrottledPrices {

    private final ThrottledPricesPublisher publisher;
    private final int windowSize;
    private final LinkedList<PriceContribution> awaitingContributions = new LinkedList<>();
    private int inFlightMessages = 0;

    public ThrottledPrices(ThrottledPricesPublisher publisher, int windowSize) {
        this.publisher = publisher;
        this.windowSize = windowSize;
    }

    public void onHeartbeat(long nanoTime) {
        publisher.publishHeartbeat(nanoTime);
    }

    public void onQuoteUpdate(CharSequence isin, int tier, long bidPrice, long askPrice) {
        validateQuote(isin, tier, bidPrice, askPrice);
        awaitingContributions.offer(new Quote(isin, tier, bidPrice, askPrice));
        if (windowFull()) return;
        awaitingContributions.remove().publishBy(publisher);
        inFlightMessages++;
    }

    public void onCancel(CharSequence isin) {
        validate(isin);
        awaitingContributions.offer(new Cancel(isin));
        if (windowFull()) return;
        awaitingContributions.remove().publishBy(publisher);
        inFlightMessages++;
    }

    public void onAck() {
        inFlightMessages = 0;
        while (!windowFull() && !awaitingContributions.isEmpty()) {
            awaitingContributions.remove().publishBy(publisher);
            inFlightMessages++;
        }
    }

    private boolean windowFull() {
        return inFlightMessages >= windowSize;
    }

    private void validateQuote(CharSequence isin, int tier, long bidPrice, long askPrice) {
        validate(isin);
        if (tier == 0 || (askPrice == 0 && bidPrice == 0)) {
            throw new IllegalArgumentException("Invalid quote update");
        }
    }

    private void validate(CharSequence isin) {
        if (isin.length() == 0) {
            throw new IllegalArgumentException("Invalid quote update");
        }
    }
}
