package com.michaelszymczak.sample.tddrefalgo.encoding;

import com.michaelszymczak.sample.tddrefalgo.domain.messages.*;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

import static org.agrona.BitUtil.SIZE_OF_BYTE;

public class MessageEncoding {

    public interface DecodedMessageConsumer {
        void onHeartbeat(Heartbeat message);

        void onQuote(Quote message);
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

        public int encode(Message message) {
            if (message.type() == MessageType.HEARTBEAT) {
                buffer.putByte(offset, (byte) MessageType.HEARTBEAT.charType());
                return heartbeatEncoder.wrap(buffer, offset + SIZE_OF_BYTE).encode((Heartbeat) message);
            }
            if (message.type() == MessageType.QUOTE) {
                buffer.putByte(offset, (byte) MessageType.QUOTE.charType());
                return quoteEncoder.wrap(buffer, offset + SIZE_OF_BYTE).encode((Quote) message);
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

        public int decode(DecodedMessageConsumer consumer) {
            char type = (char) buffer.getByte(offset);
            if (type == MessageType.HEARTBEAT.charType()) {
                int position = heartbeatDecoder.wrap(buffer, offset + SIZE_OF_BYTE).decode(mutableHeartbeat);
                consumer.onHeartbeat(mutableHeartbeat);
                return position;
            }
            if (type == MessageType.QUOTE.charType()) {
                int position = quoteDecoder.wrap(buffer, offset + SIZE_OF_BYTE).decode(mutableQuote);
                consumer.onQuote(mutableQuote);
                return position;
            }
            return offset;
        }
    }
}
