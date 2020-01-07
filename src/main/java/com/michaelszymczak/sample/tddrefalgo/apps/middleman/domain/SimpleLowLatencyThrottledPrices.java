package com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain;

import com.michaelszymczak.sample.tddrefalgo.apps.middleman.ThrottledPricesPublisher;
import com.michaelszymczak.sample.tddrefalgo.coalescingqueue.CoalescingQueue;
import com.michaelszymczak.sample.tddrefalgo.coalescingqueue.LowLatencyCoalescingQueue;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.MutableQuotePricingMessage;

import java.util.List;

import static com.michaelszymczak.sample.tddrefalgo.coalescingqueue.CoalescingQueue.DROP_EVICTED_ELEMENT;

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
    private final CoalescingQueue<MutableQuotePricingMessage> queue;
    private int inFlightMessages = 0;


    public SimpleLowLatencyThrottledPrices(ThrottledPricesPublisher publisher, int windowSize) {
        this.publisher = publisher;
        this.windowSize = windowSize;
        this.queue = new LowLatencyCoalescingQueue<>();
    }

    @Override
    public void onHeartbeat(long nanoTime) {
        publisher.publishHeartbeat(nanoTime);
    }

    @Override
    public void onQuoteUpdate(CharSequence isin, int tier, long bidPrice, long askPrice) {
        queue.add(
                isin + "/" + tier,
                new MutableQuotePricingMessage().set(isin, tier, bidPrice, askPrice),
                DROP_EVICTED_ELEMENT
        );
        tryPublish();
    }

    @Override
    public void onCancel(CharSequence isin) {
        for (int i = 0; i < POSSIBLE_TIERS.length; i++) {
            int tier = POSSIBLE_TIERS[i];
            queue.add(
                    isin + "/" + tier,
                    new MutableQuotePricingMessage().set(isin, 0, 0, 0),
                    DROP_EVICTED_ELEMENT
            );
        }
        tryPublish();
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
}
