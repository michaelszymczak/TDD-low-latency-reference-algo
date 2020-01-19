package com.michaelszymczak.sample.tddrefalgo.apps.pingpong;

import org.agrona.ExpandableArrayBuffer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PongAppTest {

    private static final int IN_OFFSET = 55;

    private final ExpandableArrayBuffer in = new ExpandableArrayBuffer();

    @Test
    void shouldNotDoAnythingUnprompted() {
        PongApp app = new PongApp();

        int read = app.onInput(in, IN_OFFSET, 0, false);

        assertEquals(IN_OFFSET, read);
        assertEquals(0, app.output().offset());
        assertEquals(0, app.output().writtenPosition());
    }
}