package com.michaelszymczak.sample.tddrefalgo.framework.encoding;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

import static org.agrona.BitUtil.*;

public class LengthBasedMessageEncoding {

    private static final int HEADER_SIZE = SIZE_OF_INT + SIZE_OF_LONG + SIZE_OF_SHORT;

    public static class Encoder {

        private MutableDirectBuffer buffer;
        private int offset;
        private long timeNanos;

        public Encoder updateTime(long timeNanos) {
            this.timeNanos = timeNanos;
            return this;
        }

        public Encoder wrap(MutableDirectBuffer buffer, int offset) {
            this.buffer = buffer;
            this.offset = offset;
            return this;
        }

        public <M> int encode(ProtocolEncoder<? extends ProtocolEncoder<?, M>, M> protocolEncoder, M message) {
            int payloadOffset = this.offset + HEADER_SIZE;
            int positionAfterPayloadWritten = protocolEncoder.wrap(buffer, payloadOffset).encode(message);
            int payloadLength = positionAfterPayloadWritten - payloadOffset;
            buffer.putInt(this.offset, payloadLength);
//            buffer.putLong(this.offset + SIZE_OF_INT, 0);
            buffer.putShort(this.offset + SIZE_OF_INT + SIZE_OF_LONG, protocolEncoder.payloadSchema().id());
            return positionAfterPayloadWritten;
        }


    }

    public static class Decoder {
        private DirectBuffer buffer;
        private int offset;
        private int length;

        public Decoder() {
        }

        public Decoder wrap(DirectBuffer buffer, int offset, int length) {
            this.buffer = buffer;
            this.offset = offset;
            this.length = length;
            return this;
        }

        public int decode(final DecodedAppMessageConsumer consumer) {
            while (length > HEADER_SIZE) {
                int payloadLength = buffer.getInt(offset);
                short schemaId = buffer.getShort(offset + SIZE_OF_INT + SIZE_OF_LONG);
                consumer.onMessage(schemaId, buffer, offset + HEADER_SIZE, payloadLength);
                offset = offset + HEADER_SIZE + payloadLength;
                length = length - HEADER_SIZE - payloadLength;
            }
            return offset;
        }
    }
}
