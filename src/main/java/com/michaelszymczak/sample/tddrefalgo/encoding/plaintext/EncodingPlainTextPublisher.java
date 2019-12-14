package com.michaelszymczak.sample.tddrefalgo.encoding.plaintext;

import com.michaelszymczak.sample.tddrefalgo.domain.messages.plaintext.MessageWithPlainText;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.plaintext.PlainTextPublisher;
import com.michaelszymczak.sample.tddrefalgo.encoding.AppPublisher;
import com.michaelszymczak.sample.tddrefalgo.encoding.lengthbased.LengthBasedMessageEncoding;

public class EncodingPlainTextPublisher implements PlainTextPublisher {

    private final LengthBasedMessageEncoding.Encoder encoder;
    private final AppPublisher appPublisher;

    public EncodingPlainTextPublisher(AppPublisher appPublisher, LengthBasedMessageEncoding.Encoder encoder) {

        this.appPublisher = appPublisher;
        this.encoder = encoder;
    }


    @Override
    public void publish(String message) {
        appPublisher.setWrittenPosition(encoder.wrap(appPublisher.buffer(), appPublisher.writtenPosition())
                .encode(new MessageWithPlainText(message)));
    }
}
