package com.michaelszymczak.sample.tddrefalgo.framework.encoding.lengthbased;

import com.michaelszymczak.sample.tddrefalgo.framework.encoding.DecodedAppMessageConsumer;
import org.agrona.DirectBuffer;

import java.util.ArrayList;
import java.util.List;

public class DecodedMessageSpy implements DecodedAppMessageConsumer {

    final List<Entry> decoded = new ArrayList<>();

    @Override
    public void onMessage(short payloadSchemaId, DirectBuffer buffer, int offset, int length) {
        decoded.add(new Entry(payloadSchemaId, buffer, offset, length));
    }

    static class Entry {
        short payloadSchemaId;
        DirectBuffer buffer;
        int offset;
        int length;

        Entry(short payloadSchemaId, DirectBuffer buffer, int offset, int length) {
            this.payloadSchemaId = payloadSchemaId;
            this.buffer = buffer;
            this.offset = offset;
            this.length = length;
        }
    }
}
