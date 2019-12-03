package com.michaelszymczak.sample.tddrefalgo;

import com.michaelszymczak.sample.tddrefalgo.domain.messages.ImmutableHeartbeat;
import com.michaelszymczak.sample.tddrefalgo.encoding.DecodedMessageSpy;
import com.michaelszymczak.sample.tddrefalgo.encoding.MessageEncoding;
import org.agrona.ExpandableArrayBuffer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppTest {

    private static final int IN_OFFSET = 5;
    private final App app = new App();
    private final MessageEncoding.Encoder encoder = new MessageEncoding.Encoder();
    private final MessageEncoding.Decoder decoder = new MessageEncoding.Decoder();
    private final ExpandableArrayBuffer in = new ExpandableArrayBuffer();
    private final DecodedMessageSpy decodedMessageSpy = new DecodedMessageSpy();

    @Test
    void shouldNotDoAnythingUnprompted() {
        int outputPosition = app.onInput(in, 0, 0);

        assertEquals(0, outputPosition);
        assertEquals(0, app.outputOffset());
        assertEquals(0, app.outputPosition());
    }

    @Test
    void shouldRespondToHeartBeat() {
        ImmutableHeartbeat message = new ImmutableHeartbeat(System.nanoTime());
        encoder.wrap(in, IN_OFFSET);
        decoder.wrap(app.output(), 0);
        int inputEndPosition = encoder.encode(message);

        // When
        int read = app.onInput(in, IN_OFFSET, inputEndPosition);

        // Then
        decoder.decode(decodedMessageSpy);
        assertEquals(1, decodedMessageSpy.messages().size());
        assertEquals(message, decodedMessageSpy.messages().get(0));

        assertEquals(inputEndPosition, read);
        assertEquals(0, app.outputOffset());
        assertEquals(read - IN_OFFSET, app.outputPosition());
    }
}
