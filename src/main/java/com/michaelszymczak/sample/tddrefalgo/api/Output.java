package com.michaelszymczak.sample.tddrefalgo.api;

import org.agrona.DirectBuffer;

public interface Output {

    DirectBuffer buffer();

    int offset();

    int writtenPosition();
}
