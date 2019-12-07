package com.michaelszymczak.sample.tddrefalgo.domain.messages;

public class Message<Payload> {

    private int payloadLength;

    private PayloadSchema payloadSchema;

    private Class<Payload> payloadClass;
    private Payload payload;

    public Message(int payloadLength, PayloadSchema payloadSchema, Class<Payload> payloadClass, Payload payload) {
        this.payloadLength = payloadLength;
        this.payloadSchema = payloadSchema;
        this.payloadClass = payloadClass;
        this.payload = payload;
    }

    public int payloadLength() {
        return payloadLength;
    }

    public PayloadSchema payloadSchema() {
        return payloadSchema;
    }

    public Class<Payload> payloadType() {
        return payloadClass;
    }

    public Payload payload() {
        return payload;
    }


}
