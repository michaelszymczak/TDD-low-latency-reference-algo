package com.michaelszymczak.sample.tddrefalgo.domain.messages;

import java.util.Objects;

public class ImmutableQuote implements Quote {

    private final String isin;
    private final int priceTier;
    private final long bidPrice;
    private final long askPrice;

    public ImmutableQuote(CharSequence isin, int priceTier, long bidPrice, long askPrice) {
        if (isin.length() > 12) {
            throw new IllegalArgumentException();
        }
        this.isin = isin.toString();
        this.priceTier = priceTier;
        this.bidPrice = bidPrice;
        this.askPrice = askPrice;
    }

    public ImmutableQuote(Quote quote) {
        this(quote.isin(), quote.priceTier(), quote.bidPrice(), quote.askPrice());
    }

    @Override
    public String isin() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImmutableQuote that = (ImmutableQuote) o;
        return priceTier == that.priceTier &&
                bidPrice == that.bidPrice &&
                askPrice == that.askPrice &&
                Objects.equals(isin, that.isin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isin, priceTier, bidPrice, askPrice);
    }

    @Override
    public String toString() {
        return "Quote{" +
                "isin='" + isin + '\'' +
                ", priceTier=" + priceTier +
                ", bidPrice=" + bidPrice +
                ", bidPrice=" + askPrice +
                '}';
    }
}
