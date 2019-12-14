package com.michaelszymczak.sample.tddrefalgo.domain.messages;

public interface Message<Payload> {

    Payload payload();

    Class<Payload> payloadType();
}
