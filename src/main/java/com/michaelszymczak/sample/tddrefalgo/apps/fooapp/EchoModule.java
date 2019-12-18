package com.michaelszymczak.sample.tddrefalgo.apps.fooapp;

import com.michaelszymczak.sample.tddrefalgo.framework.encoding.EncodingPublisher;
import com.michaelszymczak.sample.tddrefalgo.protocols.plaintext.PlainTextListener;

public class EchoModule implements PlainTextListener {

    private final EncodingPublisher<String> publisher;

    public EchoModule(EncodingPublisher<String> publisher) {
        this.publisher = publisher;
    }

    @Override
    public void onMessage(String message) {
        publisher.publish(message);
    }
}
