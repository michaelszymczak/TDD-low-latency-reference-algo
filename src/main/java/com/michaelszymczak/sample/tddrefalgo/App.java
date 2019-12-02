package com.michaelszymczak.sample.tddrefalgo;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

import static org.agrona.BitUtil.SIZE_OF_BYTE;

public class App {

    private final MutableDirectBuffer out;

    public App(MutableDirectBuffer out) {
        this.out = out;
    }

    public int onInput(DirectBuffer input, int offset, int length) {
        byte data = input.getByte(offset);
        long nanoTime = input.getLong(offset + SIZE_OF_BYTE);
        // TODO: move output position
        out.putByte(0, data);
        out.putLong(SIZE_OF_BYTE, nanoTime);
        return length;
    }
}
