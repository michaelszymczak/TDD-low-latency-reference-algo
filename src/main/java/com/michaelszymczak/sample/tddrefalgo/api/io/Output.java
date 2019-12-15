package com.michaelszymczak.sample.tddrefalgo.api.io;

import org.agrona.DirectBuffer;

public interface Output {

    DirectBuffer buffer();

    int offset();

    int writtenPosition();

    default int totalLength() {
        return writtenPosition() - offset();
    }
}
