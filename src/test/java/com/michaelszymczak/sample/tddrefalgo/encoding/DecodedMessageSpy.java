package com.michaelszymczak.sample.tddrefalgo.encoding;

import com.michaelszymczak.sample.tddrefalgo.domain.messages.*;

import java.util.ArrayList;
import java.util.List;

public class DecodedMessageSpy implements MessageEncoding.DecodedMessageConsumer {

    private List<Message> messages = new ArrayList<>();


    @Override
    public void onHeartbeat(Heartbeat message) {
        messages.add(new ImmutableHeartbeat(message));
    }

    @Override
    public void onQuote(Quote message) {
        messages.add(new ImmutableQuote(message));
    }

    public List<Message> messages() {
        return messages;
    }
}
