package com.michaelszymczak.sample.tddrefalgo;

import com.michaelszymczak.sample.tddrefalgo.domain.messages.ImmutableHeartbeat;
import com.michaelszymczak.sample.tddrefalgo.encoding.DecodedMessageSpy;
import com.michaelszymczak.sample.tddrefalgo.encoding.MessageEncoding;
import org.agrona.ExpandableArrayBuffer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppTest {

    private static final int IN_OFFSET = 5;
    private final MessageEncoding.Encoder encoder = new MessageEncoding.Encoder();
    private final MessageEncoding.Decoder decoder = new MessageEncoding.Decoder();
    private final ExpandableArrayBuffer in = new ExpandableArrayBuffer();
    private final ExpandableArrayBuffer output = new ExpandableArrayBuffer();
    private final DecodedMessageSpy decodedMessageSpy = new DecodedMessageSpy();
    private App app = new App(output);

    @Test
    void shouldNotDoAnythingUnprompted() {
        int outputPosition = app.onInput(in, 0, 0);

        assertEquals(0, outputPosition);
    }

    @Test
    void shouldRespondToHeartBeat() {
        long nanoTime = System.nanoTime();
        int inputEndPosition = encoder.wrap(in, IN_OFFSET).encode(new ImmutableHeartbeat(nanoTime));

        // When
        int outputPosition = app.onInput(in, IN_OFFSET, inputEndPosition);

        // Then
        assertEquals(inputEndPosition, outputPosition);
        decoder.wrap(output, 0).decode(decodedMessageSpy);
        assertEquals(1, decodedMessageSpy.messages().size());
        assertEquals(new ImmutableHeartbeat(nanoTime), decodedMessageSpy.messages().get(0));
    }
}
