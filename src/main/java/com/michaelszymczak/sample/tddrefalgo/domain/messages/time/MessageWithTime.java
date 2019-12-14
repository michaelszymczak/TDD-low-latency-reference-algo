package com.michaelszymczak.sample.tddrefalgo.domain.messages.time;

import com.michaelszymczak.sample.tddrefalgo.domain.messages.Message;

public class MessageWithTime implements Message<Time> {

    private Time payload;

    public MessageWithTime(Time payload) {
        this.payload = payload;
    }

    @Override
    public Time payload() {
        return payload;
    }

    @Override
    public Class<Time> payloadType() {
        return Time.class;
    }


}
