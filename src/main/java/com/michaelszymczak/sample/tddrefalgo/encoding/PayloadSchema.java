package com.michaelszymczak.sample.tddrefalgo.encoding;

public interface PayloadSchema {
    short id();

    class KnownPayloadSchema implements PayloadSchema {

        private final short id;

        public KnownPayloadSchema(short id) {
            this.id = id;
        }

        @Override
        public short id() {
            return id;
        }
    }
}
