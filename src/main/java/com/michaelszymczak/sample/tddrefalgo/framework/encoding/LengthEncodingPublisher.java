package com.michaelszymczak.sample.tddrefalgo.framework.encoding;

public class LengthEncodingPublisher<M> implements EncodingPublisher<M> {

    private final ProtocolEncoder<?, M> protocolEncoder;
    private final AppPublisher appPublisher;

    public LengthEncodingPublisher(AppPublisher appPublisher, ProtocolEncoder<?, M> protocolEncoder) {
        this.protocolEncoder = protocolEncoder;
        this.appPublisher = appPublisher;
    }

    @Override
    public void publish(M message) {
        appPublisher.publishMessage(protocolEncoder, message);
    }
}
