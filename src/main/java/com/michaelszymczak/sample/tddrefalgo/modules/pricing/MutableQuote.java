package com.michaelszymczak.sample.tddrefalgo.modules.pricing;

public class MutableQuote implements Quote {

    private final StringBuilder isin = new StringBuilder();
    private int priceTier;
    private long bidPrice;
    private long askPrice;

    void set(CharSequence isin, int priceTier, long bidPrice, long askPrice) {
        if (isin.length() > 12) {
            throw new IllegalArgumentException();
        }
        this.isin.setLength(0);
        this.isin.append(isin);
        this.priceTier = priceTier;
        this.bidPrice = bidPrice;
        this.askPrice = askPrice;
    }

    @Override
    public CharSequence isin() {
        return isin;
    }

    @Override
    public int priceTier() {
        return priceTier;
    }

    @Override
    public long bidPrice() {
        return bidPrice;
    }

    @Override
    public long askPrice() {
        return askPrice;
    }
}
