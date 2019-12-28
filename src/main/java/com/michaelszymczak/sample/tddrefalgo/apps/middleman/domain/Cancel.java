package com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain;

import com.michaelszymczak.sample.tddrefalgo.apps.middleman.ThrottledPricesPublisher;

public class Cancel implements PriceContribution {

    private final String isin;

    public Cancel(CharSequence isin) {
        this.isin = isin.toString();
    }

    @Override
    public void publishBy(ThrottledPricesPublisher publisher) {
        publisher.publishCancel(isin);
    }

    @Override
    public String isin() {
        return isin;
    }
}
