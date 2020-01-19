package com.michaelszymczak.sample.tddrefalgo.framework.api.io;

import org.agrona.DirectBuffer;

public interface AppIO {

    default int onInput(Output output) {
        return onInput(output.buffer(), output.offset(), output.length(), false);
    }

    default void onSingleReaderInput(Output output) {
        onInput(output);
        output.reset();
    }

    int onInput(DirectBuffer input, int offset, int length, boolean canReturnEarly);

    Output output();
}
