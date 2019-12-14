package com.michaelszymczak.sample.tddrefalgo.domain.messages;

import com.michaelszymczak.sample.tddrefalgo.encoding.PayloadSchema;

public enum SupportedPayloadSchemas implements PayloadSchema {

    UNDEFINED((short) 0),
    PLAIN_TEXT((short) 1),
    TIME((short) 2),
    PRICING((short) 3);

    private final short id;

    private static final SupportedPayloadSchemas[] VALUES = values();

    SupportedPayloadSchemas(short id) {
        this.id = id;
    }

    public static PayloadSchema of(int schemaCode) {
        for (SupportedPayloadSchemas payloadSchema : VALUES) {
            if (payloadSchema.id() == schemaCode) {
                return payloadSchema;
            }
        }
        return UNDEFINED;
    }

    @Override
    public short id() {
        return id;
    }
}
