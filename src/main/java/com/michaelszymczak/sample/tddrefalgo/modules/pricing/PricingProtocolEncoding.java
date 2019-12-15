package com.michaelszymczak.sample.tddrefalgo.modules.pricing;

import com.michaelszymczak.sample.tddrefalgo.framework.api.setup.PayloadSchema;
import com.michaelszymczak.sample.tddrefalgo.framework.api.setup.ProtocolDecoder;
import com.michaelszymczak.sample.tddrefalgo.framework.encoding.ProtocolEncoder;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

import static com.michaelszymczak.sample.tddrefalgo.modules.pricing.PricingMessageType.HEARTBEAT;
import static com.michaelszymczak.sample.tddrefalgo.modules.pricing.PricingMessageType.QUOTE;
import static org.agrona.BitUtil.SIZE_OF_BYTE;

public class PricingProtocolEncoding {

    private static byte toCharType(final PricingMessageType type) {
        switch (type) {
            case HEARTBEAT:
                return 'H';
            case QUOTE:
                return 'Q';
            default:
                throw new IllegalArgumentException();
        }
    }

    private static PricingMessageType toType(final byte type) {
        switch (type) {
            case 'H':
                return HEARTBEAT;
            case 'Q':
                return QUOTE;
            default:
                throw new IllegalArgumentException();
        }
    }

    public static class Encoder implements ProtocolEncoder<Encoder, PricingMessage> {
        private final HeartbeatEncoding.Encoder heartbeatEncoder = new HeartbeatEncoding.Encoder();
        private final QuoteEncoding.Encoder quoteEncoder = new QuoteEncoding.Encoder();
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
        public int encode(PricingMessage pricingMessage) {
            if (pricingMessage.type() == HEARTBEAT) {
                buffer.putByte(offset, toCharType(HEARTBEAT));
                return heartbeatEncoder.wrap(buffer, offset + SIZE_OF_BYTE).encode((Heartbeat) pricingMessage);
            }
            if (pricingMessage.type() == PricingMessageType.QUOTE) {
                buffer.putByte(offset, toCharType(PricingMessageType.QUOTE));
                return quoteEncoder.wrap(buffer, offset + SIZE_OF_BYTE).encode((Quote) pricingMessage);
            }
            return 0;
        }

        @Override
        public PayloadSchema payloadSchema() {
            return payloadSchema;
        }

    }

    public static class Decoder implements ProtocolDecoder<Decoder, PricingProtocolListener> {

        private final HeartbeatEncoding.Decoder heartbeatDecoder = new HeartbeatEncoding.Decoder();
        private final QuoteEncoding.Decoder quoteDecoder = new QuoteEncoding.Decoder();
        private final MutableHeartbeat mutableHeartbeat = new MutableHeartbeat();
        private final MutableQuote mutableQuote = new MutableQuote();
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
        public int decode(PricingProtocolListener decodedMessageListener) {
            PricingMessageType type = toType(buffer.getByte(offset));
            int position;
            switch (type) {
                case HEARTBEAT:
                    position = heartbeatDecoder.wrap(buffer, offset + SIZE_OF_BYTE).decode(mutableHeartbeat);
                    decodedMessageListener.onHeartbeat(mutableHeartbeat);
                    return position;
                case QUOTE:
                    position = quoteDecoder.wrap(buffer, offset + SIZE_OF_BYTE).decode(mutableQuote);
                    decodedMessageListener.onQuote(mutableQuote);
                    return position;
            }
            return offset;
        }
    }
}
