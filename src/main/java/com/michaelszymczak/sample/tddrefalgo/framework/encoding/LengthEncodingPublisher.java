package com.michaelszymczak.sample.tddrefalgo.framework.encoding;

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
        int writeStartPosition = appPublisher.writtenPosition();
        int writeEndPosition = encoder.wrap(appPublisher.buffer(), writeStartPosition)
                .encode(protocolEncoder, message);
        appPublisher.setWrittenPosition(writeEndPosition);
    }
}
