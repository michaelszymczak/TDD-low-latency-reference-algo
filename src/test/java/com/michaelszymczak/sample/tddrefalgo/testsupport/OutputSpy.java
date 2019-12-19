package com.michaelszymczak.sample.tddrefalgo.testsupport;

import com.michaelszymczak.sample.tddrefalgo.framework.api.io.AppIO;
import com.michaelszymczak.sample.tddrefalgo.framework.api.io.Output;
import com.michaelszymczak.sample.tddrefalgo.framework.encoding.LengthBasedMessageEncoding;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.PricingMessage;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.PricingProtocolEncoding;
import org.agrona.DirectBuffer;

import java.util.List;

public class OutputSpy implements AppIO {
    private final LengthBasedMessageEncoding.Decoder decoder = new LengthBasedMessageEncoding.Decoder();
    private final PricingProtocolEncoding.Decoder pricingDecoder = new PricingProtocolEncoding.Decoder();
    private final PricingProtocolDecodedMessageSpy spy = new PricingProtocolDecodedMessageSpy();

    public void clear() {
        spy.clear();
    }

    @Override
    public int onInput(DirectBuffer input, int offset, int length) {
        return decoder.wrap(input, offset, length).decode(
                (payloadSchemaId, timeNs, buffer, offset1, length1) ->
                        pricingDecoder.wrap(buffer, offset1, length1).decode(spy));
    }

    public List<PricingMessage> receivedMessages() {
        return spy.messages();
    }

    @Override
    public Output output() {
        return null;
    }
}
