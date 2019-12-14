package com.michaelszymczak.sample.tddrefalgo;

import com.michaelszymczak.sample.tddrefalgo.encoding.ProtocolDecoder;

class RegisteredApp<D extends ProtocolDecoder<D, L>, L> {
    private final int protocolSchemaId;
    private final D protocolDecoder;
    private final L decodedMessageListener;

    RegisteredApp(int protocolSchemaId, D protocolDecoder, L decodedMessageListener) {
        this.protocolSchemaId = protocolSchemaId;
        this.protocolDecoder = protocolDecoder;
        this.decodedMessageListener = decodedMessageListener;
    }

    int getProtocolSchemaId() {
        return protocolSchemaId;
    }

    D getProtocolDecoder() {
        return protocolDecoder;
    }

    L getDecodedMessageListener() {
        return decodedMessageListener;
    }
}
