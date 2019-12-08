package com.michaelszymczak.sample.tddrefalgo.encoding.plaintext;

import com.michaelszymczak.sample.tddrefalgo.domain.messages.plaintext.MessageWithPlainText;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.plaintext.PlainTextPublisher;
import com.michaelszymczak.sample.tddrefalgo.encoding.AppPublisher;
import com.michaelszymczak.sample.tddrefalgo.encoding.MessageEncoding;

public class EncodingPlainTextPublisher implements PlainTextPublisher {

    private final MessageEncoding.Encoder encoder = new MessageEncoding.Encoder();
    private final AppPublisher appPublisher;

    public EncodingPlainTextPublisher(AppPublisher appPublisher) {

        this.appPublisher = appPublisher;
    }


    @Override
    public void publish(String message) {
        appPublisher.setWrittenPosition(encoder.wrap(appPublisher.buffer(), appPublisher.writtenPosition())
                .encode(new MessageWithPlainText(message)));
    }
}
