package com.michaelszymczak.sample.tddrefalgo.testsupport;

import com.michaelszymczak.sample.tddrefalgo.framework.encoding.LengthBasedMessageEncoding;
import com.michaelszymczak.sample.tddrefalgo.framework.encoding.lengthbased.DecodedMessageSpy;
import org.agrona.DirectBuffer;

public class SameOutputProperty {

    private final LengthBasedMessageEncoding.Decoder decoder = new LengthBasedMessageEncoding.Decoder();
    private final DecodedMessageSpy output1Spy = new DecodedMessageSpy();
    private final DecodedMessageSpy output2Spy = new DecodedMessageSpy();


    public int verifySameOutputs(
            final DirectBuffer buffer1, final int offset1, final int length1,
            final DirectBuffer buffer2, final int offset2, final int length2
    ) {
        if (offset1 != offset2 || length1 != length2) {
            throw new IllegalArgumentException("Incorrect inputs");
        }
        int currentOffset1 = offset1;
        int currentOffset2 = offset2;
        int remainingLength1 = length1;
        int remainingLength2 = length2;

        while (remainingLength1 > 0) {
            int currentOffset1Before = currentOffset1;
            output1Spy.reset();
            output2Spy.reset();
            currentOffset1 = decoder.wrap(buffer1, currentOffset1, remainingLength1).decodeNext(output1Spy);
            currentOffset2 = decoder.wrap(buffer2, currentOffset2, remainingLength2).decodeNext(output2Spy);
            if (currentOffset1 != currentOffset2) {
                throw new IllegalArgumentException("Incorrect outputs");
            }
            remainingLength1 -= (currentOffset1 - currentOffset1Before);
            remainingLength2 -= (currentOffset2 - currentOffset1Before);
            DecodedMessageSpy.Entry entry1 = output1Spy.firstEntry();
            DecodedMessageSpy.Entry entry2 = output2Spy.firstEntry();
            if ((entry1 != null && entry2 == null) ||
                    (entry1 == null && entry2 != null) ||
                    (entry1 != null && !entry1.equals(entry2))) {
                throw new AssertionError("Mismatch of outputs. Output 1: " + entry1 + ", output 2: " + entry2);
            }
        }
        return currentOffset1;
    }
}
