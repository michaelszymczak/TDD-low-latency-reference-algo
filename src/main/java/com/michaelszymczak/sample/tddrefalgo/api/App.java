package com.michaelszymczak.sample.tddrefalgo.api;

import org.agrona.DirectBuffer;

public interface App {

    int onInput(DirectBuffer input, int offset, int length);

    Output output();
}
