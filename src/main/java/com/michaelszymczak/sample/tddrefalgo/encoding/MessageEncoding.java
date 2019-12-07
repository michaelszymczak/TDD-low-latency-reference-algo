package com.michaelszymczak.sample.tddrefalgo.encoding;

import com.michaelszymczak.sample.tddrefalgo.domain.messages.Message;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.PayloadSchema;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.plaintext.MessageWithPlainText;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol.MessageWithPricingProtocol;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.time.MessageWithTime;
import com.michaelszymczak.sample.tddrefalgo.encoding.plaintext.PlainTextEncoding;
import com.michaelszymczak.sample.tddrefalgo.encoding.pricingprotocol.PricingProtocolEncoding;
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
        private final PricingProtocolEncoding.Encoder pricingEncoder = new PricingProtocolEncoding.Encoder();
        private final TimeEncoding.Encoder timeEncoder = new TimeEncoding.Encoder();

        private MutableDirectBuffer buffer;
        private int offset;

        public Encoder wrap(MutableDirectBuffer buffer, int offset) {
            this.buffer = buffer;
            this.offset = offset;
            return this;
        }

        public int encode(Message<?> message) {
            if (message instanceof MessageWithPricingProtocol) {
                buffer.putInt(offset + SIZE_OF_INT, PayloadSchema.PLAIN_TEXT.value);
                pricingEncoder.wrap(buffer, offset + HEADER_SIZE).encode(((MessageWithPricingProtocol) message).payload());
            } else if (message instanceof MessageWithPlainText) {
                buffer.putInt(offset + SIZE_OF_INT, PayloadSchema.PLAIN_TEXT.value);
                plainTextEncoder.wrap(buffer, offset + HEADER_SIZE).encode(((MessageWithPlainText) message).payload());
            } else if (message instanceof MessageWithTime) {
                buffer.putInt(offset + SIZE_OF_INT, PayloadSchema.TIME.value);
                timeEncoder.wrap(buffer, offset + HEADER_SIZE).encode(((MessageWithTime) message).payload());
            } else {
                throw new IllegalArgumentException();
            }

            buffer.putInt(offset, message.payloadLength());
            return offset + HEADER_SIZE + message.payloadLength();
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
