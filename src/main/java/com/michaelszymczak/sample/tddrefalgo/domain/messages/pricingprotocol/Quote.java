package com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol;

import static org.agrona.BitUtil.SIZE_OF_INT;
import static org.agrona.BitUtil.SIZE_OF_LONG;

public interface Quote extends PricingMessage {

    @Override
    default PricingMessageType type() {
        return PricingMessageType.QUOTE;
    }

    @Override
    default int length() {
        return 12 + SIZE_OF_INT + SIZE_OF_LONG + SIZE_OF_LONG;
    }

    /**
     * @return International Securities Identification Number
     */
    CharSequence isin();

    int priceTier();

    long bidPrice();

    long askPrice();
}
