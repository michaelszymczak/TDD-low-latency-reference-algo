package com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain;

import com.michaelszymczak.sample.tddrefalgo.apps.middleman.ThrottledPricesPublisher;
import com.michaelszymczak.sample.tddrefalgo.coalescingqueue.CoalescingQueue;
import com.michaelszymczak.sample.tddrefalgo.coalescingqueue.LowLatencyCoalescingQueue;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.MutableQuotePricingMessage;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class SimpleLowLatencyThrottledPrices implements ThrottledPrices {

    private static final int[] POSSIBLE_TIERS;
    private static final String KEY_DELIMITER = "/";

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
    private final StringBuilder keyPlaceholder = new StringBuilder();
    private final Deque<MutableQuotePricingMessage> messagePool = new ArrayDeque<>();
    private int inFlightMessages = 0;
    private CoalescingQueue.EvictedElementListener<MutableQuotePricingMessage> returnToThePoolEvictedMessage = message -> {
        message.clear();
        messagePool.addFirst(message);
    };

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
        keyPlaceholder.setLength(0);
        keyPlaceholder.append(isin).append(KEY_DELIMITER).append(tier);
        queue.add(
                keyPlaceholder,
                newMessage().set(isin, tier, bidPrice, askPrice),
                returnToThePoolEvictedMessage
        );
        tryPublish();
    }

    @Override
    public void onCancel(CharSequence isin) {
        enqueueCancelledAllTiers(isin);
        tryPublish();
    }

    private void enqueueCancelledAllTiers(CharSequence isin) {
        for (int i = 0; i < POSSIBLE_TIERS.length; i++) {
            keyPlaceholder.setLength(0);
            keyPlaceholder.append(isin).append(KEY_DELIMITER).append(POSSIBLE_TIERS[i]);
            queue.add(
                    keyPlaceholder,
                    newMessage().set(isin, 0, 0, 0),
                    returnToThePoolEvictedMessage
            );
        }
    }

    @Override
    public void onAck() {
        inFlightMessages = 0;
        tryPublish();
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
            inFlightMessages++;
        }
    }

    private boolean isWindowFull() {
        return inFlightMessages >= windowSize;
    }


    private MutableQuotePricingMessage newMessage() {
        MutableQuotePricingMessage pooled = messagePool.pollFirst();
        if (pooled == null) {
            return new MutableQuotePricingMessage();
        } else {
            return pooled;
        }
    }

}
