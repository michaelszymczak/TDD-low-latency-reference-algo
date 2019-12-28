package com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain;

import com.michaelszymczak.sample.tddrefalgo.apps.middleman.ThrottledPricesPublisher;

import static com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain.PriceContributionType.EMPTY;

class Empty implements PriceContribution {

    private final String isin;
    private final int tier;

    Empty(PriceContribution priceContribution) {
        this.isin = priceContribution.isin();
        this.tier = priceContribution.tier();
    }

    @Override
    public boolean publishBy(ThrottledPricesPublisher publisher) {
        return false;
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
        return EMPTY;
    }

    @Override
    public boolean sameIsinAsIn(PriceContribution other) {
        return isin.equals(other.isin());
    }
}
