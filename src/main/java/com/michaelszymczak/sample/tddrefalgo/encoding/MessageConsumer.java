package com.michaelszymczak.sample.tddrefalgo.encoding;

import com.michaelszymczak.sample.tddrefalgo.domain.messages.PayloadSchema;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol.PricingProtocolListener;
import com.michaelszymczak.sample.tddrefalgo.encoding.pricingprotocol.PricingProtocolEncoding;
import org.agrona.DirectBuffer;

class MessageConsumer implements DecodedMessageConsumer {

    private final PricingProtocolEncoding.Decoder decoder = new PricingProtocolEncoding.Decoder();
    private PricingProtocolListener pricingProtocolListener;

    MessageConsumer(PricingProtocolListener pricingProtocolListener) {
        this.pricingProtocolListener = pricingProtocolListener;
    }

    @Override
    public void onMessage(PayloadSchema payloadSchema, DirectBuffer buffer, int offset, int length) {
        if (payloadSchema != PayloadSchema.PRICING) {
            throw new UnsupportedOperationException();
        }
        decoder.wrap(buffer, offset).decode(pricingProtocolListener);
    }
}
