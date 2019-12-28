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

    private static void validate(CharSequence isin) {
        if (isin.length() == 0) {
            throw new IllegalArgumentException("Invalid isin");
        }
    }
}
