package com.michaelszymczak.sample.tddrefalgo.domain.messages;

public enum MessageType {
    HEARTBEAT('H'),
    QUOTE('Q');

    private final char type;

    MessageType(char type) {
        this.type = type;
    }

    public char charType() {
        return type;
    }
}
