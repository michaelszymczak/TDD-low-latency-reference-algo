package com.michaelszymczak.sample.tddrefalgo.apps.middleman;

import com.michaelszymczak.sample.tddrefalgo.framework.api.io.AppIO;
import com.michaelszymczak.sample.tddrefalgo.framework.api.io.Output;
import org.agrona.DirectBuffer;

public class MiddleManApp implements AppIO {

    @Override
    public int onInput(DirectBuffer input, int offset, int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Output output() {
        throw new UnsupportedOperationException();
    }
}
