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
        coalesce(new Quote(isin, tier, bidPrice, askPrice));
        tryPublishEnqueued();
    }

    public void onCancel(CharSequence isin) {
        Cancel cancel = new Cancel(isin);
        coalesce(cancel);
        tryPublishEnqueued();
    }

    private void coalesce(Quote quote) {
        for (int i = 0; i < awaitingContributions.size(); i++) {
            if (awaitingContributions.get(i).matches(quote)) {
                awaitingContributions.set(i, quote);
                return;
            }
        }
        awaitingContributions.offer(quote);
    }

    private void coalesce(Cancel cancel) {
        for (PriceContribution awaitingContribution : awaitingContributions) {
            if (awaitingContribution.matches(cancel)) {
                return;
            }
        }
        awaitingContributions.offer(cancel);
    }

    public void onAck() {
        inFlightMessages = 0;
        tryPublishEnqueued();
    }

    private void tryPublishEnqueued() {
        while (!isWindowFull() && !awaitingContributions.isEmpty()) {
            awaitingContributions.remove().publishBy(publisher);
            inFlightMessages++;
        }
    }

    private boolean isWindowFull() {
        return inFlightMessages >= windowSize;
    }

}
