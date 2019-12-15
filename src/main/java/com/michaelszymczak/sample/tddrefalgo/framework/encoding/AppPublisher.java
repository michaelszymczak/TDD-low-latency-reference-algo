package com.michaelszymczak.sample.tddrefalgo.framework.encoding;

import com.michaelszymczak.sample.tddrefalgo.framework.api.io.Output;
import org.agrona.MutableDirectBuffer;
import org.agrona.collections.MutableInteger;
import org.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;

public class AppPublisher implements Output {

    private final MutableDirectBuffer out;
    private final MutableInteger outputWrittenPosition = new MutableInteger(0);

    public AppPublisher(final int capacity) {
        out = new UnsafeBuffer(ByteBuffer.allocateDirect(capacity));
    }

    @Override
    public MutableDirectBuffer buffer() {
        return out;
    }

    @Override
    public int writtenPosition() {
        return outputWrittenPosition.get();
    }

    @Override
    public void reset() {
        outputWrittenPosition.set(0);
    }

    @Override
    public int capacity() {
        return out.capacity();
    }

    void setWrittenPosition(int position) {
        outputWrittenPosition.set(position);
    }

    @Override
    public int offset() {
        return 0;
    }
}
