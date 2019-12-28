package com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain;

import com.michaelszymczak.sample.tddrefalgo.apps.middleman.ThrottledPricesPublisher;

public class Cancel implements PriceContribution {

    private final String isin;

    Cancel(CharSequence isin) {
        validate(isin);
        this.isin = isin.toString();
    }

    @Override
    public void publishBy(ThrottledPricesPublisher publisher) {
        publisher.publishCancel(isin);
    }

    @Override
    public boolean matches(PriceContribution other) {
        return isin().equals(other.isin()) && tier() == other.tier();
    }

    @Override
    public String isin() {
        return isin;
    }

    @Override
    public int tier() {
        return 0;
    }

    private static void validate(CharSequence isin) {
        if (isin.length() == 0) {
            throw new IllegalArgumentException("Invalid isin");
        }
    }
}
