package com.michaelszymczak.sample.tddrefalgo.domain.messages.plaintext;

import com.michaelszymczak.sample.tddrefalgo.domain.messages.Message;

public class MessageWithPlainText implements Message<String> {

    private String payload;

    public MessageWithPlainText(String payload) {
        this.payload = payload;
    }

    @Override
    public int payloadLength() {
        return payload.length();
    }

    @Override
    public String payload() {
        return payload;
    }

}
