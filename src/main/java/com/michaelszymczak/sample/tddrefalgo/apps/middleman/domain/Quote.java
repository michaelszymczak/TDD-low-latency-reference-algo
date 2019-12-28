package com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain;

import com.michaelszymczak.sample.tddrefalgo.apps.middleman.ThrottledPricesPublisher;

public class Quote implements PriceContribution {
    private final String isin;
    private final int tier;
    private final long bidPrice;
    private final long askPrice;

    public Quote(CharSequence isin, int tier, long bidPrice, long askPrice) {
        this.isin = isin.toString();
        this.tier = tier;
        this.bidPrice = bidPrice;
        this.askPrice = askPrice;
    }

    @Override
    public void publishBy(ThrottledPricesPublisher publisher) {
        publisher.publishQuote(isin, tier, bidPrice, askPrice);
    }

    @Override
    public String isin() {
        return isin;
    }

    public int tier() {
        return tier;
    }

    public long bidPrice() {
        return bidPrice;
    }

    public long askPrice() {
        return askPrice;
    }
}
