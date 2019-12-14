package com.michaelszymczak.sample.tddrefalgo.encoding;

import org.agrona.MutableDirectBuffer;

public interface ProtocolEncoder<E extends ProtocolEncoder<E, M>, M> {

    E wrap(MutableDirectBuffer buffer, int offset);

    int encode(M message);

    PayloadSchema payloadSchema();

    Class<M> messageType();
}
