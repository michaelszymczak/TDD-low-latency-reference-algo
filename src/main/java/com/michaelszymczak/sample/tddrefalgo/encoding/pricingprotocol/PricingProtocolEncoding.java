package com.michaelszymczak.sample.tddrefalgo.encoding.pricingprotocol;

import com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol.*;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

import static com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol.PricingMessageType.HEARTBEAT;
import static com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol.PricingMessageType.QUOTE;
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

    public static class Encoder {
        private final HeartbeatEncoding.Encoder heartbeatEncoder = new HeartbeatEncoding.Encoder();
        private final QuoteEncoding.Encoder quoteEncoder = new QuoteEncoding.Encoder();
        private MutableDirectBuffer buffer;
        private int offset;


        public Encoder wrap(MutableDirectBuffer buffer, int offset) {
            this.buffer = buffer;
            this.offset = offset;
            return this;
        }

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
    }

    public static class Decoder {

        private final HeartbeatEncoding.Decoder heartbeatDecoder = new HeartbeatEncoding.Decoder();
        private final QuoteEncoding.Decoder quoteDecoder = new QuoteEncoding.Decoder();
        private final MutableHeartbeat mutableHeartbeat = new MutableHeartbeat();
        private final MutableQuote mutableQuote = new MutableQuote();
        private DirectBuffer buffer;
        private int offset;


        public Decoder wrap(DirectBuffer buffer, int offset) {
            this.buffer = buffer;
            this.offset = offset;
            return this;
        }

        public int decode(PricingProtocolListener consumer) {
            PricingMessageType type = toType(buffer.getByte(offset));
            int position;
            switch (type) {
                case HEARTBEAT:
                    position = heartbeatDecoder.wrap(buffer, offset + SIZE_OF_BYTE).decode(mutableHeartbeat);
                    consumer.onHeartbeat(mutableHeartbeat);
                    return position;
                case QUOTE:
                    position = quoteDecoder.wrap(buffer, offset + SIZE_OF_BYTE).decode(mutableQuote);
                    consumer.onQuote(mutableQuote);
                    return position;
            }
            return offset;
        }
    }
}
