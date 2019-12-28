package com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain;

import com.michaelszymczak.sample.tddrefalgo.apps.middleman.ThrottledPricesPublisher;

import java.util.LinkedList;

import static com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain.Empty.EMPTY_INSTANCE;
import static com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain.PriceContributionType.EMPTY;

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
        onUpdate(new Quote(isin, tier, bidPrice, askPrice));

    }

    public void onCancel(CharSequence isin) {
        onUpdate(new Cancel(isin));
    }

    private void onUpdate(PriceContribution newPriceContribution) {
        boolean replaced = false;
        for (int i = 0; i < awaitingContributions.size(); i++) {
            if (awaitingContributions.get(i).canBeReplacedWith(newPriceContribution)) {
                if (replaced) {
                    awaitingContributions.set(i, EMPTY_INSTANCE);
                } else {
                    awaitingContributions.set(i, newPriceContribution);
                    replaced = true;
                }
            }
        }
        awaitingContributions.removeIf(priceContribution -> priceContribution.type() == EMPTY);
        if (!replaced) {
            awaitingContributions.offer(newPriceContribution);
        }
        tryPublishEnqueued();
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
