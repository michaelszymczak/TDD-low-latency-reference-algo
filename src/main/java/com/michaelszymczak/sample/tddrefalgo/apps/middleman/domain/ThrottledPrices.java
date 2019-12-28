package com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain;

import com.michaelszymczak.sample.tddrefalgo.apps.middleman.ThrottledPricesPublisher;

import java.util.LinkedList;

import static com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain.PriceContributionType.*;

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

    private void onUpdate(PriceContribution update) {
        boolean replaced = false;
        for (int i = 0; i < awaitingContributions.size(); i++) {
            PriceContribution existing = awaitingContributions.get(i);
            if (!existing.sameIsinAsIn(update) || existing.type() == EMPTY) {
                continue;
            }
            if (existing.type() == CANCEL && update.type() != CANCEL) {
                continue;
            }
            if (existing.type() == QUOTE && update.type() != CANCEL && update.tier() != existing.tier()) {
                continue;
            }

            if (!replaced) {
                awaitingContributions.set(i, update);
                replaced = true;
            } else {
                awaitingContributions.set(i, new Empty(update));
            }
        }

        if (!replaced) {
            awaitingContributions.offer(update);
        }

        tryPublishEnqueued();
    }

    public void onAck() {
        inFlightMessages = 0;
        tryPublishEnqueued();
    }

    private void tryPublishEnqueued() {
        while (!isWindowFull() && !awaitingContributions.isEmpty()) {
            if (awaitingContributions.remove().publishBy(publisher)) {
                inFlightMessages++;
            }
        }
    }

    private boolean isWindowFull() {
        return inFlightMessages >= windowSize;
    }

}
