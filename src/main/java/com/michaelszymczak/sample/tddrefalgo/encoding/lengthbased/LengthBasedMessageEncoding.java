package com.michaelszymczak.sample.tddrefalgo.encoding.lengthbased;

import com.michaelszymczak.sample.tddrefalgo.domain.messages.Message;
import com.michaelszymczak.sample.tddrefalgo.encoding.DecodedAppMessageConsumer;
import com.michaelszymczak.sample.tddrefalgo.encoding.ProtocolEncoder;
import org.agrona.BitUtil;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

import static org.agrona.BitUtil.SIZE_OF_INT;

public class LengthBasedMessageEncoding {

    private static final int HEADER_SIZE = SIZE_OF_INT + BitUtil.SIZE_OF_SHORT;

    public static class Encoder {

        private MutableDirectBuffer buffer;
        private int offset;


        public Encoder wrap(MutableDirectBuffer buffer, int offset) {
            this.buffer = buffer;
            this.offset = offset;
            return this;
        }

        public <M> int encode(ProtocolEncoder<? extends ProtocolEncoder<?, M>, M> protocolEncoder, Message<M> message) {
            int position = protocolEncoder.wrap(buffer, offset + HEADER_SIZE).encode(message.payload());
            buffer.putInt(offset, position - (offset + HEADER_SIZE));
            buffer.putShort(offset + SIZE_OF_INT, protocolEncoder.payloadSchema().id());
            return position;
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
            if (length - HEADER_SIZE <= 0) {
                return offset;
            }
            int payloadLength = buffer.getInt(offset);
            short schemaId = buffer.getShort(offset + SIZE_OF_INT);
            consumer.onMessage(schemaId, buffer, offset + HEADER_SIZE, payloadLength);

            return offset + length;
        }
    }
}
