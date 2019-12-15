package com.michaelszymczak.sample.tddrefalgo.framework.encoding;

import org.agrona.DirectBuffer;

public interface DecodedAppMessageConsumer {
    void onMessage(short payloadSchemaId, long timeNs, DirectBuffer buffer, int offset, int length);
}
