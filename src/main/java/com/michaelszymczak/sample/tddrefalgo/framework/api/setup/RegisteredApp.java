package com.michaelszymczak.sample.tddrefalgo.framework.api.setup;

class RegisteredApp<D extends ProtocolDecoder<D, L>, L> {
    private final PayloadSchema protocolSchemaId;
    private final D protocolDecoder;
    private final L decodedMessageListener;

    RegisteredApp(
            PayloadSchema payloadSchema,
            D protocolDecoder,
            L decodedMessageListener) {
        this.protocolSchemaId = payloadSchema;
        this.protocolDecoder = protocolDecoder;
        this.decodedMessageListener = decodedMessageListener;
    }

    int getProtocolSchemaId() {
        return protocolSchemaId.id();
    }

    D getProtocolDecoder() {
        return protocolDecoder;
    }

    L getDecodedMessageListener() {
        return decodedMessageListener;
    }
}
