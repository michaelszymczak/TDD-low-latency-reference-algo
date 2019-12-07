package com.michaelszymczak.sample.tddrefalgo.domain.messages;

public enum PayloadSchema {

    UNDEFINED((short) 0),
    PLAIN_TEXT((short) 1),
    TIME((short) 2);

    public final short value;

    private static final PayloadSchema[] VALUES = values();

    PayloadSchema(short value) {
        this.value = value;
    }

    public static PayloadSchema of(int schemaCode) {
        for (PayloadSchema payloadSchema : VALUES) {
            if (payloadSchema.value == schemaCode) {
                return payloadSchema;
            }
        }
        return UNDEFINED;
    }
}
