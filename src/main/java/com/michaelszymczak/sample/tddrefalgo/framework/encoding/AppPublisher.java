package com.michaelszymczak.sample.tddrefalgo.framework.encoding;

import com.michaelszymczak.sample.tddrefalgo.framework.api.io.Output;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;

public class AppPublisher implements Output {

    private final MutableDirectBuffer out;
    private int outputWrittenPosition = 0;
    private int outputReadPosition = 0;

    public AppPublisher(final int capacity) {
        out = new UnsafeBuffer(ByteBuffer.allocateDirect(capacity));
    }

    @Override
    public MutableDirectBuffer buffer() {
        return out;
    }

    @Override
    public int writtenPosition() {
        return outputWrittenPosition;
    }

    @Override
    public int length() {
        return writtenPosition() - offset();
    }

    @Override
    public void reset() {
        setWrittenPosition(0);
    }

    @Override
    public int initialCapacity() {
        return out.capacity();
    }

    void setWrittenPosition(int position) {
        outputWrittenPosition = position;
    }

    @Override
    public int offset() {
        return outputReadPosition;
    }

    public void setReadPosition(int position) {
        if (position > outputWrittenPosition)
        {
            throw new IllegalStateException("unable to mark as read beyond what has been written");
        }
        outputReadPosition = position;
    }

    @Override
    public int remainingCapacity() {
        return out.capacity() - outputWrittenPosition + outputReadPosition;
    }
}
