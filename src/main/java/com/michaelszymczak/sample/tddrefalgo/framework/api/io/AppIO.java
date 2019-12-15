package com.michaelszymczak.sample.tddrefalgo.framework.api.io;

import org.agrona.DirectBuffer;

public interface AppIO {

    default void onInput(Output output)
    {
        onInput(output.buffer(), output.offset(), output.length());
    }

    default void onSingleReaderInput(Output output)
    {
        onInput(output);
        output.reset();
    }

    int onInput(DirectBuffer input, int offset, int length);

    Output output();
}
