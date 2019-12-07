package com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol;

public interface Quote extends PricingMessage {

    @Override
    default PricingMessageType type()
    {
        return PricingMessageType.QUOTE;
    }

    /**
     * @return International Securities Identification Number
     */
    CharSequence isin();

    int priceTier();

    long bidPrice();

    long askPrice();
}
