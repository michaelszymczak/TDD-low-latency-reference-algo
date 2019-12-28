package com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain;

import com.michaelszymczak.sample.tddrefalgo.apps.middleman.ThrottledPricesPublisher;

import static com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain.PriceContributionType.CANCEL;

public class Cancel implements PriceContribution {

    private final String isin;

    Cancel(CharSequence isin) {
        validate(isin);
        this.isin = isin.toString();
    }

    private static void validate(CharSequence isin) {
        if (isin.length() == 0) {
            throw new IllegalArgumentException("Invalid isin");
        }
    }

    @Override
    public void publishBy(ThrottledPricesPublisher publisher) {
        publisher.publishCancel(isin);
    }

    @Override
    public boolean canBeReplacedWith(PriceContribution other) {
        return other.type() == CANCEL && isin().equals(other.isin());
    }

    @Override
    public String isin() {
        return isin;
    }

    @Override
    public int tier() {
        return 0;
    }

    @Override
    public PriceContributionType type() {
        return CANCEL;
    }
}
