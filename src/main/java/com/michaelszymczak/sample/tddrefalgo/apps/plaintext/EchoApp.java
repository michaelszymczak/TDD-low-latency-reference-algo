package com.michaelszymczak.sample.tddrefalgo.apps.plaintext;

import com.michaelszymczak.sample.tddrefalgo.domain.messages.plaintext.PlainTextListener;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.plaintext.PlainTextPublisher;

public class EchoApp implements PlainTextListener {

    private final PlainTextPublisher publisher;

    public EchoApp(PlainTextPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void onPlainTextMessage(String message) {
        publisher.publish(message);
    }
}
