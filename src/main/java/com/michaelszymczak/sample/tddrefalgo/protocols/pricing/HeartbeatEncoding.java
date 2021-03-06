package com.michaelszymczak.sample.tddrefalgo.protocols.pricing;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

import static org.agrona.BitUtil.SIZE_OF_LONG;

public class HeartbeatEncoding {

    private static final int NANO_TIME_OFFSET = 0;
    private static final int TOTAL_LENGTH = SIZE_OF_LONG;

    public static class Encoder {

        private MutableDirectBuffer buffer;
        private int offset;


        public Encoder wrap(MutableDirectBuffer buffer, int offset) {
            this.buffer = buffer;
            this.offset = offset;
            return this;
        }

        public int encode(HeartbeatPricingMessage heartbeatPricingMessage) {
            buffer.putLong(offset + NANO_TIME_OFFSET, heartbeatPricingMessage.nanoTime());
            return offset + TOTAL_LENGTH;
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

        public int decode(MutableHeartbeatPricingMessage result) {
            result.set(buffer.getLong(offset + NANO_TIME_OFFSET));
            return offset + TOTAL_LENGTH;
        }
    }

}
