package com.michaelszymczak.sample.tddrefalgo.api.setup;

import org.agrona.DirectBuffer;

public interface ProtocolDecoder<D extends ProtocolDecoder<D, L>, L> {

    D wrap(DirectBuffer buffer, int offset, int length);

    int decode(L decodedMessageListener);
}
