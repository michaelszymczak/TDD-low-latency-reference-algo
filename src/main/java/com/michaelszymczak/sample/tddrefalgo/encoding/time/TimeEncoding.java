package com.michaelszymczak.sample.tddrefalgo.encoding.time;

import com.michaelszymczak.sample.tddrefalgo.domain.messages.time.Time;
import com.michaelszymczak.sample.tddrefalgo.encoding.PayloadSchema;
import com.michaelszymczak.sample.tddrefalgo.encoding.ProtocolDecoder;
import com.michaelszymczak.sample.tddrefalgo.encoding.ProtocolEncoder;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

import static org.agrona.BitUtil.SIZE_OF_LONG;

public class TimeEncoding {

    public interface DecodedMessageListener {
        void onMessage(Time message);
    }

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

        @Override
        public Class<Time> messageType() {
            return Time.class;
        }
    }

    public static class Decoder implements ProtocolDecoder<Decoder, DecodedMessageListener> {
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
        public int decode(DecodedMessageListener decodedMessageListener) {
            time.set(buffer.getLong(offset));
            decodedMessageListener.onMessage(time);
            return offset + SIZE_OF_LONG;
        }
    }
}
