package com.michaelszymczak.sample.tddrefalgo.testsupport;

import com.michaelszymczak.sample.tddrefalgo.framework.api.setup.PayloadSchema;
import com.michaelszymczak.sample.tddrefalgo.framework.encoding.LengthBasedMessageEncoding;
import com.michaelszymczak.sample.tddrefalgo.protocols.plaintext.PlainTextEncoding;
import org.agrona.ExpandableArrayBuffer;
import org.agrona.MutableDirectBuffer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SameOutputPropertyTest {

    private static final int OFFSET = 0;

    private final LengthBasedMessageEncoding.Encoder encoder = new LengthBasedMessageEncoding.Encoder();
    private final PlainTextEncoding.Encoder textEncoder = new PlainTextEncoding.Encoder(new PayloadSchema.KnownPayloadSchema((short) 1));
    private final MutableDirectBuffer buffer1 = new ExpandableArrayBuffer();
    private final MutableDirectBuffer buffer2 = new ExpandableArrayBuffer();
    private SameOutputProperty sameOutputProperty = new SameOutputProperty();


    @Test
    void shouldAcceptWhenOutputsAreIdentical() {
        int nextOffset1 = encoder.wrap(buffer1, OFFSET).encode(textEncoder, "foo");
        int nextOffset2 = encoder.wrap(buffer2, OFFSET).encode(textEncoder, "foo");

        assertDoesNotThrow(() -> sameOutputProperty.verifySameOutputs(
                buffer1, OFFSET, nextOffset1 - OFFSET,
                buffer2, OFFSET, nextOffset2 - OFFSET
        ));
    }

    @Test
    void shouldRejectWhenOutputsAreDifferent() {
        int nextOffset1 = encoder.wrap(buffer1, OFFSET).encode(textEncoder, "foo");
        int nextOffset2 = encoder.wrap(buffer2, OFFSET).encode(textEncoder, "bar");

        assertThrows(AssertionError.class, () -> sameOutputProperty.verifySameOutputs(
                buffer1, OFFSET, nextOffset1 - OFFSET,
                buffer2, OFFSET, nextOffset2 - OFFSET
        ));
    }

    @Test
    void shouldRejectWhenOutputsAreInitiallySameButLaterDifferent() {
        int nextOffset1 = encoder.wrap(buffer1, OFFSET).encode(textEncoder, "foo");
        int nextOffset2 = encoder.wrap(buffer1, nextOffset1).encode(textEncoder, "bar");
        int nextOffset3 = encoder.wrap(buffer2, OFFSET).encode(textEncoder, "foo");
        int nextOffset4 = encoder.wrap(buffer2, nextOffset3).encode(textEncoder, "baz");

        assertThrows(AssertionError.class, () -> sameOutputProperty.verifySameOutputs(
                buffer1, OFFSET, nextOffset2 - OFFSET,
                buffer2, OFFSET, nextOffset4 - OFFSET
        ));
    }

    @Test
    void shouldAcceptWhenAllOutputsAreTheSame() {
        int nextOffset1 = encoder.wrap(buffer1, OFFSET).encode(textEncoder, "foo");
        int nextOffset2 = encoder.wrap(buffer1, nextOffset1).encode(textEncoder, "baz");
        int nextOffset3 = encoder.wrap(buffer2, OFFSET).encode(textEncoder, "foo");
        int nextOffset4 = encoder.wrap(buffer2, nextOffset3).encode(textEncoder, "baz");

        assertDoesNotThrow(() -> sameOutputProperty.verifySameOutputs(
                buffer1, OFFSET, nextOffset2 - OFFSET,
                buffer2, OFFSET, nextOffset4 - OFFSET
        ));
    }
}