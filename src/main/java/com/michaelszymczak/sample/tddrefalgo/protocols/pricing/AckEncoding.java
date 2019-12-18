package com.michaelszymczak.sample.tddrefalgo.protocols.pricing;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

public class AckEncoding {

    public static class Encoder {

        private MutableDirectBuffer buffer;
        private int offset;


        public Encoder wrap(MutableDirectBuffer buffer, int offset) {
            this.buffer = buffer;
            this.offset = offset;
            return this;
        }

        public int encode(AckMessage message) {
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

        public int decode() {
            return offset;
        }
    }

}
