package com.michaelszymczak.sample.tddrefalgo.testsupport;

import com.michaelszymczak.sample.tddrefalgo.framework.api.io.AppIO;
import com.michaelszymczak.sample.tddrefalgo.framework.api.io.Output;
import com.michaelszymczak.sample.tddrefalgo.framework.encoding.LengthBasedMessageEncoding;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.PricingMessage;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.PricingProtocolEncoding;
import org.agrona.DirectBuffer;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

    public <T extends PricingMessage> List<T> receivedMessages(Class<T> type, Predicate<T> predicate) {
        return spy.messages().stream()
                .filter(msg -> type.isAssignableFrom(msg.getClass()))
                .map(type::cast)
                .filter(predicate)
                .collect(Collectors.toList());
    }

    @Override
    public Output output() {
        return null;
    }
}
