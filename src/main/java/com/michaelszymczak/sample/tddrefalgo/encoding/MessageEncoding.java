package com.michaelszymczak.sample.tddrefalgo.encoding;

import com.michaelszymczak.sample.tddrefalgo.domain.messages.Message;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.PayloadSchema;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.Time.Time;
import com.michaelszymczak.sample.tddrefalgo.encoding.plaintext.PlainTextEncoding;
import com.michaelszymczak.sample.tddrefalgo.encoding.time.TimeEncoding;
import org.agrona.BitUtil;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

import static org.agrona.BitUtil.SIZE_OF_INT;

public class MessageEncoding {

    private static final int HEADER_SIZE = SIZE_OF_INT + BitUtil.SIZE_OF_SHORT;

    public interface DecodedMessageConsumer {
        void onMessage(PayloadSchema payloadSchema, DirectBuffer buffer, int offset, int length);
    }

    public static class Encoder {

        private final PlainTextEncoding.Encoder plainTextEncoder = new PlainTextEncoding.Encoder();
        private final TimeEncoding.Encoder timeEncoder = new TimeEncoding.Encoder();

        private MutableDirectBuffer buffer;
        private int offset;

        public Encoder wrap(MutableDirectBuffer buffer, int offset) {
            this.buffer = buffer;
            this.offset = offset;
            return this;
        }

        public int encode(Message<?> message) {
            if (message.payloadType().equals(String.class)) {
                Message<String> msg = (Message<String>) message;
                buffer.putInt(offset, message.payloadLength());
                buffer.putInt(offset + SIZE_OF_INT, PayloadSchema.PLAIN_TEXT.value);
                plainTextEncoder.wrap(buffer, offset + HEADER_SIZE).encode(msg.payload());
                return offset + HEADER_SIZE + message.payloadLength();
            } else if (message.payloadType().equals(Time.class)) {
                Message<Time> msg = (Message<Time>) message;
                buffer.putInt(offset, message.payloadLength());
                buffer.putInt(offset + SIZE_OF_INT, PayloadSchema.TIME.value);
                timeEncoder.wrap(buffer, offset + HEADER_SIZE).encode(msg.payload());
                return offset + HEADER_SIZE + message.payloadLength();
            }
            return offset;
        }
    }

    public static class Decoder {

        private DirectBuffer buffer;
        private int offset;

        public Decoder wrap(DirectBuffer buffer, int offset) {
            this.buffer = buffer;
            this.offset = offset;
            return this;
        }

        public int decode(final int length, final DecodedMessageConsumer consumer) {
            if (length - HEADER_SIZE <= 0) {
                return offset;
            }
            int payloadLength = buffer.getInt(offset);
            int schemaCode = buffer.getShort(offset + SIZE_OF_INT);
            consumer.onMessage(PayloadSchema.of(schemaCode), buffer, offset + HEADER_SIZE, payloadLength);

            return offset + length;
        }
    }
}
