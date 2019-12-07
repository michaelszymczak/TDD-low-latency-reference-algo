package com.michaelszymczak.sample.tddrefalgo.domain.messages;

public interface Message<Payload> {

    int payloadLength();

    Payload payload();
}
