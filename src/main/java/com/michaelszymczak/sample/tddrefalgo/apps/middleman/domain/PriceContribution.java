package com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain;

import com.michaelszymczak.sample.tddrefalgo.apps.middleman.ThrottledPricesPublisher;

public interface PriceContribution {

    String isin();

    int tier();

    PriceContributionType type();

    boolean publishBy(ThrottledPricesPublisher publisher);

    boolean sameIsinAsIn(PriceContribution other);
}
