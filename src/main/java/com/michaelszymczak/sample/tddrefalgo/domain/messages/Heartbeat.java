package com.michaelszymczak.sample.tddrefalgo.domain.messages;

public interface Heartbeat extends Message {

    @Override
    default MessageType type()
    {
        return MessageType.HEARTBEAT;
    }

    long nanoTime();
}
