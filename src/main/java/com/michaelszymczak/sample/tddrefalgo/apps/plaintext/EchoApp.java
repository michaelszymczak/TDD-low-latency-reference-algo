package com.michaelszymczak.sample.tddrefalgo.apps.plaintext;

import com.michaelszymczak.sample.tddrefalgo.domain.messages.plaintext.PlainTextListener;
import com.michaelszymczak.sample.tddrefalgo.encoding.LengthEncodingPublisher;

public class EchoApp implements PlainTextListener {

    private final LengthEncodingPublisher<String> publisher;

    public EchoApp(LengthEncodingPublisher<String> publisher) {
        this.publisher = publisher;
    }

    @Override
    public void onMessage(String message) {
        publisher.publish(message);
    }
}
