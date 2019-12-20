package com.michaelszymczak.sample.tddrefalgo.testsupport;

import com.michaelszymczak.sample.tddrefalgo.framework.api.io.AppIO;
import com.michaelszymczak.sample.tddrefalgo.framework.api.io.Output;
import com.michaelszymczak.sample.tddrefalgo.framework.encoding.LengthBasedMessageEncoding;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.PricingProtocolEncoding;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.PricingProtocolListener;
import org.agrona.DirectBuffer;

public class OutputSpy<Spy extends PricingProtocolListener> implements AppIO {
    private final LengthBasedMessageEncoding.Decoder decoder = new LengthBasedMessageEncoding.Decoder();
    private final PricingProtocolEncoding.Decoder pricingDecoder = new PricingProtocolEncoding.Decoder();
    private final Spy spy;

    public static OutputSpy<PricingProtocolDecodedMessageSpy> outputSpy() {
        return new OutputSpy<>(new PricingProtocolDecodedMessageSpy());
    }

    public OutputSpy(final Spy spy) {
        this.spy = spy;
    }

    @Override
    public int onInput(DirectBuffer input, int offset, int length) {
        return decoder.wrap(input, offset, length).decode(
                (payloadSchemaId, timeNs, buffer, offset1, length1) ->
                        pricingDecoder.wrap(buffer, offset1, length1).decode(spy));
    }

    @Override
    public Output output() {
        return null;
    }

    public Spy getSpy() {
        return spy;
    }
}
