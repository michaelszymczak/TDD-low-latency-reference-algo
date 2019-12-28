package com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain;

import com.michaelszymczak.sample.tddrefalgo.apps.middleman.ThrottledPricesPublisher;

import static com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain.PriceContributionType.EMPTY;

public class Empty implements PriceContribution {

    static final Empty EMPTY_INSTANCE = new Empty();

    @Override
    public void publishBy(ThrottledPricesPublisher publisher) {

    }

    @Override
    public boolean matches(PriceContribution other) {
        return false;
    }

    @Override
    public String isin() {
        return "";
    }

    @Override
    public int tier() {
        return 0;
    }

    @Override
    public PriceContributionType type() {
        return EMPTY;
    }
}
