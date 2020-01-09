package com.michaelszymczak.sample.tddrefalgo.protocols.pricing;

import java.util.Comparator;
import java.util.Objects;

public class ImmutableQuotePricingMessage implements QuotePricingMessage, Comparable<ImmutableQuotePricingMessage> {

    private static final Comparator<ImmutableQuotePricingMessage> COMPARATOR = Comparator
            .comparing(ImmutableQuotePricingMessage::isin)
            .thenComparing(ImmutableQuotePricingMessage::priceTier);

    private final String isin;
    private final int priceTier;
    private final long bidPrice;
    private final long askPrice;

    public ImmutableQuotePricingMessage(CharSequence isin, int priceTier, long bidPrice, long askPrice) {
        if (isin.length() > 12) {
            throw new IllegalArgumentException();
        }
        this.isin = isin.toString();
        this.priceTier = priceTier;
        this.bidPrice = bidPrice;
        this.askPrice = askPrice;
    }

    public ImmutableQuotePricingMessage(QuotePricingMessage quotePricingMessage) {
        this(quotePricingMessage.isin(), quotePricingMessage.priceTier(), quotePricingMessage.bidPrice(), quotePricingMessage.askPrice());
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
        ImmutableQuotePricingMessage that = (ImmutableQuotePricingMessage) o;
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
        return String.format("Q/%s/%d/%d/%d", isin().trim(), priceTier(), bidPrice(), askPrice());
    }

    @Override
    public int compareTo(ImmutableQuotePricingMessage other) {
        return COMPARATOR.compare(this, other);
    }
}
