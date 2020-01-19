package com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain;

import com.michaelszymczak.sample.tddrefalgo.apps.middleman.ThrottledPricesPublisher;
import com.michaelszymczak.sample.tddrefalgo.coalescingqueue.CoalescingQueue;
import com.michaelszymczak.sample.tddrefalgo.coalescingqueue.LowLatencyCoalescingQueue;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.MutableQuotePricingMessage;

import java.util.List;

public class SimpleLowLatencyThrottledPrices implements ThrottledPrices {

    private static final int[] POSSIBLE_TIERS;

    static {
        POSSIBLE_TIERS = new int[Tier.allValues().size()];
        List<Integer> values = Tier.allValues();
        for (int i = 0; i < values.size(); i++) {
            POSSIBLE_TIERS[i] = values.get(i);
        }
    }

    private final ThrottledPricesPublisher publisher;
    private final int windowSize;
    private final CoalescingQueue<MutableQuotePricingMessage> queue = new LowLatencyCoalescingQueue<>();
    private int inFlightMessages = 0;
    private final Pool pool = new Pool();
    private final CoalescingQueue.EvictedElementListener<MutableQuotePricingMessage> returnToPool = pool::returnToPool;

    public SimpleLowLatencyThrottledPrices(ThrottledPricesPublisher publisher, int windowSize) {
        this.publisher = publisher;
        this.windowSize = windowSize;
    }

    @Override
    public void onHeartbeat(long nanoTime) {
        publisher.publishHeartbeat(nanoTime);
    }

    @Override
    public void onQuoteUpdate(CharSequence isin, int tier, long bidPrice, long askPrice) {
        enqueueQuote(isin, tier, bidPrice, askPrice);
        tryPublish();
    }

    @Override
    public void onCancel(CharSequence isin) {
        enqueueCancelledAllTiers(isin);
        tryPublish();
    }

    @Override
    public void onAck() {
        inFlightMessages = 0;
        tryPublish();
    }

    private void enqueueQuote(CharSequence isin, int tier, long bidPrice, long askPrice) {
        queue.add(
                pool.reusableKey(isin, tier),
                pool.pooledMessage(isin, tier, bidPrice, askPrice),
                returnToPool
        );
    }

    private void enqueueCancelledAllTiers(CharSequence isin) {
        for (int i = 0; i < POSSIBLE_TIERS.length; i++) {
            queue.add(
                    pool.reusableKey(isin, POSSIBLE_TIERS[i]),
                    pool.pooledMessage(isin, 0, 0, 0),
                    returnToPool
            );
        }
    }

    private void tryPublish() {
        while (!isWindowFull()) {
            MutableQuotePricingMessage msg = queue.poll();
            if (msg == null) {
                return;
            }
            if (msg.priceTier() == 0 && msg.bidPrice() == 0 && msg.askPrice() == 0) {
                publisher.publishCancel(msg.isin());
            } else {
                publisher.publishQuote(msg.isin(), msg.priceTier(), msg.bidPrice(), msg.askPrice());
            }
            pool.returnToPool(msg);
            inFlightMessages++;
        }
    }

    private boolean isWindowFull() {
        return inFlightMessages >= windowSize;
    }


}
