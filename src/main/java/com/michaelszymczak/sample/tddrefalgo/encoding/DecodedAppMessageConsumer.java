package com.michaelszymczak.sample.tddrefalgo.encoding;

import org.agrona.DirectBuffer;

public interface DecodedAppMessageConsumer {
    void onMessage(short payloadSchemaId, DirectBuffer buffer, int offset, int length);
}
