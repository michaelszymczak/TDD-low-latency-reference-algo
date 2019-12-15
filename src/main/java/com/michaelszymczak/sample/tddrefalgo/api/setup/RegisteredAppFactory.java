package com.michaelszymczak.sample.tddrefalgo.api.setup;

import com.michaelszymczak.sample.tddrefalgo.encoding.EncodingPublisher;
import com.michaelszymczak.sample.tddrefalgo.encoding.ProtocolEncoder;

import java.util.function.Function;

public class RegisteredAppFactory<D extends ProtocolDecoder<D, L>, E extends ProtocolEncoder<E, M>, L, M> {
    private final PayloadSchema protocolSchemaId;
    private final D protocolDecoder;
    private final E protocolEncoder;
    private final Function<EncodingPublisher<M>, L> appFactory;

    public RegisteredAppFactory(
            PayloadSchema payloadSchema,
            D protocolDecoder,
            E protocolEncoder,
            Function<EncodingPublisher<M>, L> appFactory) {
        this.protocolSchemaId = payloadSchema;
        this.protocolDecoder = protocolDecoder;
        this.protocolEncoder = protocolEncoder;
        this.appFactory = appFactory;
    }

    PayloadSchema getProtocolSchema() {
        return protocolSchemaId;
    }

    D getProtocolDecoder() {
        return protocolDecoder;
    }

    E getProtocolEncoder() {
        return protocolEncoder;
    }

    Function<EncodingPublisher<M>, L> getAppFactory() {
        return appFactory;
    }
}
