package com.michaelszymczak.sample.tddrefalgo;

import org.agrona.DirectBuffer;
import org.agrona.ExpandableDirectByteBuffer;
import org.agrona.MutableDirectBuffer;

import static org.agrona.BitUtil.SIZE_OF_BYTE;
import static org.agrona.BitUtil.SIZE_OF_LONG;

public class App {

    private final MutableDirectBuffer out;
    private long length = 0;

    public App() {
        this.out = new ExpandableDirectByteBuffer();
    }

    public int onInput(DirectBuffer input, int offset, int length) {
        byte type = input.getByte(offset);
        if (type != 'H') {
            return 0;
        }

        long nanoTime = input.getLong(offset + SIZE_OF_BYTE);
        out.putByte(0, type);
        out.putLong(SIZE_OF_BYTE, nanoTime);
        this.length = SIZE_OF_BYTE + SIZE_OF_LONG;
        return length;
    }

    public DirectBuffer output() {
        return out;
    }

    public long outputOffset() {
        return 0;
    }

    public long outputPosition() {
        return length;
    }
}
