package com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain;

import com.michaelszymczak.sample.tddrefalgo.apps.middleman.ThrottledPricesPublisher;

public interface PriceContribution {

    String isin();

    void publishBy(ThrottledPricesPublisher publisher);
}
