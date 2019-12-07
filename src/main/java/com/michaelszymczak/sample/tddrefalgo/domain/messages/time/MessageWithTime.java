package com.michaelszymczak.sample.tddrefalgo.domain.messages.time;

import com.michaelszymczak.sample.tddrefalgo.domain.messages.Message;

import static org.agrona.BitUtil.SIZE_OF_LONG;

public class MessageWithTime implements Message<Time> {

    private Time payload;

    public MessageWithTime(Time payload) {
        this.payload = payload;
    }

    @Override
    public int payloadLength() {
        return SIZE_OF_LONG;
    }

    @Override
    public Time payload() {
        return payload;
    }


}
