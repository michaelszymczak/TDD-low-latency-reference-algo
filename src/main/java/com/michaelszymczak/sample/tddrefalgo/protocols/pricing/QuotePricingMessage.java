package com.michaelszymczak.sample.tddrefalgo.protocols.pricing;

public interface QuotePricingMessage extends PricingMessage {

    @Override
    default Type type() {
        return Type.QUOTE;
    }

    /**
     * @return International Securities Identification Number
     */
    CharSequence isin();

    int priceTier();

    long bidPrice();

    long askPrice();
}
