package com.michaelszymczak.sample.tddrefalgo.encoding;

import org.agrona.DirectBuffer;

public interface ProtocolDecoder<D extends ProtocolDecoder<D>> {

    D wrap(DirectBuffer buffer, int offset, int length);
}
