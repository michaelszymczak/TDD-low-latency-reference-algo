package com.michaelszymczak.sample.tddrefalgo.protocols.time;

import com.michaelszymczak.sample.tddrefalgo.framework.api.setup.PayloadSchema;
import com.michaelszymczak.sample.tddrefalgo.framework.api.setup.ProtocolDecoder;
import com.michaelszymczak.sample.tddrefalgo.framework.encoding.ProtocolEncoder;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

import static org.agrona.BitUtil.SIZE_OF_LONG;

public class TimeEncoding {

    public static class Encoder implements ProtocolEncoder<Encoder, Time> {
        private final PayloadSchema payloadSchema;
        private MutableDirectBuffer buffer;
        private int offset;

        public Encoder(PayloadSchema payloadSchema) {
            this.payloadSchema = payloadSchema;
        }


        @Override
        public Encoder wrap(MutableDirectBuffer buffer, int offset) {
            this.buffer = buffer;
            this.offset = offset;
            return this;
        }

        @Override
        public int encode(Time message) {
            buffer.putLong(offset, message.timeNanos());
            return offset + SIZE_OF_LONG;
        }

        @Override
        public PayloadSchema payloadSchema() {
            return payloadSchema;
        }

    }

    public static class Decoder implements ProtocolDecoder<Decoder, TimeMessageListener> {
        private final Time time = new Time(0);
        private DirectBuffer buffer;
        private int offset;
        private int length;

        @Override
        public Decoder wrap(DirectBuffer buffer, int offset, int length) {
            this.buffer = buffer;
            this.offset = offset;
            this.length = length;
            return this;
        }

        @Override
        public int decode(TimeMessageListener timeMessageListener) {
            time.set(buffer.getLong(offset));
            timeMessageListener.onMessage(time);
            return offset + SIZE_OF_LONG;
        }
    }
}
