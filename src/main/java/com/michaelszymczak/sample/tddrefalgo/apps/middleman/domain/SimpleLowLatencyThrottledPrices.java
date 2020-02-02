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

    private final CoalescingQueue<MutableQuotePricingMessage> queue = new LowLatencyCoalescingQueue<>();
    private final Pool<MutableQuotePricingMessage> pool = new Pool<>(MutableQuotePricingMessage::new);
    private final CoalescingQueue.EvictedElementListener<MutableQuotePricingMessage> returnToThePool = evictedElement -> {
        evictedElement.clear();
        pool.returnToPool(evictedElement);
    };
    private final StringIntReusableKey keyPlaceholder = new StringIntReusableKey("/");

    private final ThrottledPricesPublisher publisher;
    private final int windowSize;

    private int inFlightMessages = 0;

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
                keyPlaceholder.withParts(isin, tier),
                pool.get().set(isin, tier, bidPrice, askPrice),
                returnToThePool
        );
    }

    private void enqueueCancelledAllTiers(CharSequence isin) {
        for (int i = 0; i < POSSIBLE_TIERS.length; i++) {
            queue.add(
                    keyPlaceholder.withParts(isin, POSSIBLE_TIERS[i]),
                    pool.get().set(isin, 0, 0, 0),
                    returnToThePool
            );
        }
    }

    private void tryPublish() {
        while (!isWindowFull()) {
            MutableQuotePricingMessage nextMessage = queue.poll();
            if (nextMessage == null) {
                return;
            }
            if (isCancel(nextMessage)) {
                publisher.publishCancel(nextMessage.isin());
            } else {
                publisher.publishQuote(nextMessage.isin(), nextMessage.priceTier(), nextMessage.bidPrice(), nextMessage.askPrice());
            }
            nextMessage.clear();
            pool.returnToPool(nextMessage);
            inFlightMessages++;
        }
    }

    private boolean isCancel(MutableQuotePricingMessage msg) {
        return msg.priceTier() == 0 && msg.bidPrice() == 0 && msg.askPrice() == 0;
    }

    private boolean isWindowFull() {
        return inFlightMessages >= windowSize;
    }


}
