package com.michaelszymczak.sample.tddrefalgo.apps.marketmaker.support;

import com.michaelszymczak.sample.tddrefalgo.framework.api.io.Output;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;

public class OutputCopy implements Output {

    private final MutableDirectBuffer out;
    private final int length;
    private final int outputWrittenPosition;
    private final int remainingCapacity;
    private final int offset;
    private final int initialCapacity;

    public OutputCopy(Output output) {
        this.length = output.length();
        this.out = new UnsafeBuffer(ByteBuffer.allocateDirect(output.offset() + output.length()));
        output.buffer().getBytes(output.offset(), out, output.offset(), output.length());
        this.outputWrittenPosition = output.writtenPosition();
        this.offset = output.offset();
        this.remainingCapacity = output.remainingCapacity();
        this.initialCapacity = output.initialCapacity();
    }

    @Override
    public DirectBuffer buffer() {
        return out;
    }

    @Override
    public int writtenPosition() {
        return outputWrittenPosition;
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int initialCapacity() {
        return initialCapacity;
    }

    @Override
    public int offset() {
        return offset;
    }

    @Override
    public int remainingCapacity() {
        return remainingCapacity;
    }
}
