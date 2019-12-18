package com.michaelszymczak.sample.tddrefalgo.protocols.pricing;

import com.michaelszymczak.sample.tddrefalgo.framework.api.setup.PayloadSchema;
import com.michaelszymczak.sample.tddrefalgo.framework.api.setup.ProtocolDecoder;
import com.michaelszymczak.sample.tddrefalgo.framework.encoding.ProtocolEncoder;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

import static com.michaelszymczak.sample.tddrefalgo.protocols.pricing.AckMessage.ACK_MESSAGE;
import static com.michaelszymczak.sample.tddrefalgo.protocols.pricing.PricingMessage.Type.*;
import static org.agrona.BitUtil.SIZE_OF_BYTE;

public class PricingProtocolEncoding {

    private static byte toCharType(final PricingMessage.Type type) {
        switch (type) {
            case HEARTBEAT:
                return 'H';
            case QUOTE:
                return 'Q';
            case ACK:
                return 'A';
            default:
                throw new IllegalArgumentException();
        }
    }

    private static PricingMessage.Type toType(final byte type) {
        switch (type) {
            case 'H':
                return HEARTBEAT;
            case 'Q':
                return QUOTE;
            case 'A':
                return ACK;
            default:
                throw new IllegalArgumentException("" + type);
        }
    }

    public static class Encoder implements ProtocolEncoder<Encoder, PricingMessage> {
        private final AckEncoding.Encoder ackEncoder = new AckEncoding.Encoder();
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
            if (pricingMessage.type() == PricingMessage.Type.HEARTBEAT) {
                buffer.putByte(offset, toCharType(PricingMessage.Type.HEARTBEAT));
                return heartbeatEncoder.wrap(buffer, offset + SIZE_OF_BYTE).encode((HeartbeatPricingMessage) pricingMessage);
            }
            if (pricingMessage.type() == PricingMessage.Type.QUOTE) {
                buffer.putByte(offset, toCharType(PricingMessage.Type.QUOTE));
                return quoteEncoder.wrap(buffer, offset + SIZE_OF_BYTE).encode((QuotePricingMessage) pricingMessage);
            }
            if (pricingMessage.type() == PricingMessage.Type.ACK) {
                buffer.putByte(offset, toCharType(PricingMessage.Type.ACK));
                return ackEncoder.wrap(buffer, offset + SIZE_OF_BYTE).encode((AckMessage) pricingMessage);
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
        private final AckEncoding.Decoder ackDecoder = new AckEncoding.Decoder();
        private final QuoteEncoding.Decoder quoteDecoder = new QuoteEncoding.Decoder();
        private final MutableHeartbeatPricingMessage mutableHeartbeat = new MutableHeartbeatPricingMessage();
        private final MutableQuotePricingMessage mutableQuote = new MutableQuotePricingMessage();
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
            PricingMessage.Type type = toType(buffer.getByte(offset));
            int position;
            switch (type) {
                case HEARTBEAT:
                    position = heartbeatDecoder.wrap(buffer, offset + SIZE_OF_BYTE).decode(mutableHeartbeat);
                    decodedMessageListener.onMessage(mutableHeartbeat);
                    return position;
                case QUOTE:
                    position = quoteDecoder.wrap(buffer, offset + SIZE_OF_BYTE).decode(mutableQuote);
                    decodedMessageListener.onMessage(mutableQuote);
                    return position;
                case ACK:
                    position = ackDecoder.wrap(buffer, offset + SIZE_OF_BYTE).decode();
                    decodedMessageListener.onMessage(ACK_MESSAGE);
                    return position;
            }
            return offset;
        }
    }
}
