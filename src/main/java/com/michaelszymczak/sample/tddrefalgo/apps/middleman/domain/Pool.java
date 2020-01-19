package com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain;

import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.MutableQuotePricingMessage;

import java.util.ArrayDeque;
import java.util.Deque;

class Pool {
    private static final String KEY_DELIMITER = "/";
    private final Deque<MutableQuotePricingMessage> pool = new ArrayDeque<>();
    private final StringBuilder keyPlaceholder = new StringBuilder();

    void returnToPool(MutableQuotePricingMessage evictedElement) {
        evictedElement.clear();
        pool.addFirst(evictedElement);
    }

    MutableQuotePricingMessage pooledMessage(CharSequence isin, int priceTier, long bidPrice, long askPrice) {
        return message().set(isin, priceTier, bidPrice, askPrice);
    }

    CharSequence reusableKey(CharSequence isin, int tier) {
        keyPlaceholder.setLength(0);
        return keyPlaceholder.append(isin).append(KEY_DELIMITER).append(tier);
    }

    private MutableQuotePricingMessage message() {
        MutableQuotePricingMessage pooled = pool.pollFirst();
        if (pooled == null) {
            return new MutableQuotePricingMessage();
        } else {
            return pooled;
        }
    }
}
