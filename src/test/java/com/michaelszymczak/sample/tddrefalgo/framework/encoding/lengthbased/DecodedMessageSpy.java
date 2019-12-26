package com.michaelszymczak.sample.tddrefalgo.framework.encoding.lengthbased;

import com.michaelszymczak.sample.tddrefalgo.framework.encoding.DecodedAppMessageConsumer;
import org.agrona.DirectBuffer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DecodedMessageSpy implements DecodedAppMessageConsumer {

    final List<Entry> decoded = new ArrayList<>();

    @Override
    public void onMessage(short payloadSchemaId, long timeNs, DirectBuffer buffer, int offset, int length) {
        decoded.add(new Entry(payloadSchemaId, timeNs, buffer, offset, length));
    }

    public Entry firstEntry() {
        return decoded.isEmpty() ? null : decoded.get(0);
    }

    public void reset() {
        decoded.clear();
    }

    public static class Entry {
        short payloadSchemaId;
        long timeNs;
        byte[] data;
        int offset;
        int length;

        Entry(short payloadSchemaId, long timeNs, DirectBuffer buffer, int offset, int length) {
            this.payloadSchemaId = payloadSchemaId;
            this.timeNs = timeNs;
            this.offset = offset;
            this.length = length;
            this.data = new byte[length];
            buffer.getBytes(offset, data, 0, length);
        }

        public Entry(short payloadSchemaId, long timeNs, byte[] data, int offset, int length) {
            this.payloadSchemaId = payloadSchemaId;
            this.timeNs = timeNs;
            this.offset = offset;
            this.length = length;
            this.data = data;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Entry entry = (Entry) o;
            return payloadSchemaId == entry.payloadSchemaId &&
                    timeNs == entry.timeNs &&
                    offset == entry.offset &&
                    length == entry.length &&
                    Arrays.equals(data, entry.data);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(payloadSchemaId, timeNs, offset, length);
            result = 31 * result + Arrays.hashCode(data);
            return result;
        }

        @Override
        public String toString() {
            return "Entry{" +
                    "payloadSchemaId=" + payloadSchemaId +
                    ", timeNs=" + timeNs +
                    ", data=" + Arrays.toString(data) +
                    ", offset=" + offset +
                    ", length=" + length +
                    '}';
        }
    }
}
