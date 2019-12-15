package com.michaelszymczak.sample.tddrefalgo.protocols.plaintext;

import com.michaelszymczak.sample.tddrefalgo.framework.encoding.EncodingPublisher;

public class EchoApp implements PlainTextListener {

    private final EncodingPublisher<String> publisher;

    public EchoApp(EncodingPublisher<String> publisher) {
        this.publisher = publisher;
    }

    @Override
    public void onMessage(String message) {
        publisher.publish(message);
    }
}
