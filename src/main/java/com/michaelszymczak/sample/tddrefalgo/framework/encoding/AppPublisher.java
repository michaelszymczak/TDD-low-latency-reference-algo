package com.michaelszymczak.sample.tddrefalgo.framework.encoding;

import com.michaelszymczak.sample.tddrefalgo.framework.api.io.Output;
import org.agrona.ExpandableDirectByteBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.collections.MutableInteger;

public class AppPublisher implements Output {

    private final MutableDirectBuffer out = new ExpandableDirectByteBuffer();
    private final MutableInteger outputWrittenPosition = new MutableInteger(0);

    @Override
    public MutableDirectBuffer buffer() {
        return out;
    }

    @Override
    public int writtenPosition() {
        return outputWrittenPosition.get();
    }

    void setWrittenPosition(int position) {
        outputWrittenPosition.set(position);
    }

    @Override
    public int offset() {
        return 0;
    }
}
