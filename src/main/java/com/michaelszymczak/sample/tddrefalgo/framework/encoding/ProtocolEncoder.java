package com.michaelszymczak.sample.tddrefalgo.framework.encoding;

import com.michaelszymczak.sample.tddrefalgo.framework.api.setup.PayloadSchema;
import org.agrona.MutableDirectBuffer;

public interface ProtocolEncoder<E extends ProtocolEncoder<E, M>, M> {

    E wrap(MutableDirectBuffer buffer, int offset);

    int encode(M message);

    PayloadSchema payloadSchema();

}
