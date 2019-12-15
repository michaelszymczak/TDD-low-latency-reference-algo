package com.michaelszymczak.sample.tddrefalgo.framework.api.io;

import org.agrona.DirectBuffer;

public interface AppIO {

    int onInput(DirectBuffer input, int offset, int length);

    Output output();
}
