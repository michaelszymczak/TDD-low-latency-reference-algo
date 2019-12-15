package com.michaelszymczak.sample.tddrefalgo.encoding;

import com.michaelszymczak.sample.tddrefalgo.encoding.lengthbased.LengthBasedMessageEncoding;

public class LengthEncodingPublisher<M> implements EncodingPublisher<M> {

    private final LengthBasedMessageEncoding.Encoder encoder;
    private final ProtocolEncoder<?, M> protocolEncoder;
    private final AppPublisher appPublisher;

    public LengthEncodingPublisher(ProtocolEncoder<?, M> protocolEncoder, AppPublisher appPublisher, LengthBasedMessageEncoding.Encoder encoder) {
        this.protocolEncoder = protocolEncoder;
        this.appPublisher = appPublisher;
        this.encoder = encoder;
    }


    @Override
    public void publish(M message) {
        appPublisher.setWrittenPosition(encoder.wrap(appPublisher.buffer(), appPublisher.writtenPosition())
                .encode(protocolEncoder, message));
    }
}
