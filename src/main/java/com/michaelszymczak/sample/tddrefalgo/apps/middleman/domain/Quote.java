package com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain;

public class Quote {
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
