package com.michaelszymczak.sample.tddrefalgo.encoding.time;

import com.michaelszymczak.sample.tddrefalgo.domain.messages.PayloadSchema;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.time.Time;
import com.michaelszymczak.sample.tddrefalgo.encoding.ProtocolEncoder;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

import static org.agrona.BitUtil.SIZE_OF_LONG;

public class TimeEncoding {

    public interface DecodedMessageConsumer {
        void onTimeMessage(Time message);
    }

    public static class Encoder implements ProtocolEncoder<Encoder, Time> {
        private MutableDirectBuffer buffer;
        private int offset;


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
            return PayloadSchema.TIME;
        }

        @Override
        public Class<Time> messageType() {
            return Time.class;
        }
    }

    public static class Decoder {
        private final Time time = new Time(0);
        private DirectBuffer buffer;
        private int offset;
        private int length;

        public Decoder wrap(DirectBuffer buffer, int offset, int length) {
            this.buffer = buffer;
            this.offset = offset;
            this.length = length;
            return this;
        }

        public int decode(DecodedMessageConsumer consumer) {
            time.set(buffer.getLong(offset));
            consumer.onTimeMessage(time);
            return offset + SIZE_OF_LONG;
        }
    }
}
