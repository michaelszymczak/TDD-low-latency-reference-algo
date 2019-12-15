package com.michaelszymczak.sample.tddrefalgo.api.io;

import org.agrona.DirectBuffer;

public interface AppIO {

    int onInput(DirectBuffer input, int offset, int length);

    Output output();
}
