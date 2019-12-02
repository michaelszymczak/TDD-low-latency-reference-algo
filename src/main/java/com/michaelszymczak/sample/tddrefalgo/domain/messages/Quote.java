package com.michaelszymczak.sample.tddrefalgo.domain.messages;

public interface Quote extends Message {

    @Override
    default MessageType type()
    {
        return MessageType.QUOTE;
    }

    /**
     * @return International Securities Identification Number
     */
    CharSequence isin();

    int priceTier();

    long bidPrice();

    long askPrice();
}
