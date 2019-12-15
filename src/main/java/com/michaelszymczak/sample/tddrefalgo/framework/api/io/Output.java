package com.michaelszymczak.sample.tddrefalgo.framework.api.io;

import org.agrona.DirectBuffer;

public interface Output {

    DirectBuffer buffer();

    int offset();

    int writtenPosition();

    int length();

    void reset();

    int initialCapacity();

    int remainingCapacity();
}
