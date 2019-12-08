package com.michaelszymczak.sample.tddrefalgo.encoding;

import com.michaelszymczak.sample.tddrefalgo.domain.messages.PayloadSchema;
import org.agrona.DirectBuffer;

public interface DecodedMessageConsumer {
    void onMessage(PayloadSchema payloadSchema, DirectBuffer buffer, int offset, int length);
}
