package com.michaelszymczak.sample.tddrefalgo.quote;

public interface Quote {

    /**
     * @return International Securities Identification Number
     */
    CharSequence isin();

    int priceTier();

    long bidPrice();

    long askPrice();
}
