package com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain;

import com.michaelszymczak.sample.tddrefalgo.apps.middleman.ThrottledPricesPublisher;

import static com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain.PriceContributionType.QUOTE;

class Quote implements PriceContribution {
    private final String isin;
    private final int tier;
    private final long bidPrice;
    private final long askPrice;

    Quote(CharSequence isin, int tier, long bidPrice, long askPrice) {
        validateQuote(isin, tier, bidPrice, askPrice);
        this.isin = isin.toString();
        this.tier = tier;
        this.bidPrice = bidPrice;
        this.askPrice = askPrice;
    }

    @Override
    public boolean publishBy(ThrottledPricesPublisher publisher) {
        publisher.publishQuote(isin, tier, bidPrice, askPrice);
        return true;
    }

    @Override
    public String isin() {
        return isin;
    }

    @Override
    public int tier() {
        return tier;
    }

    @Override
    public PriceContributionType type() {
        return QUOTE;
    }

    @Override
    public boolean sameIsinAsIn(PriceContribution other) {
        return isin.equals(other.isin());
    }

    private static void validateQuote(CharSequence isin, int tier, long bidPrice, long askPrice) {
        if (isin.length() == 0 || tier == 0 || (askPrice == 0 && bidPrice == 0)) {
            throw new IllegalArgumentException("Invalid quote update");
        }
    }
}
