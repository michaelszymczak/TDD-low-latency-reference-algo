package com.michaelszymczak.sample.tddrefalgo;

import com.michaelszymczak.sample.tddrefalgo.encoding.pricingprotocol.DecodedMessageSpy;
import com.michaelszymczak.sample.tddrefalgo.encoding.pricingprotocol.PricingProtocolEncoding;
import org.agrona.ExpandableArrayBuffer;
import org.junit.jupiter.api.Test;

import static com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol.Heartbeat.heartbeat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AppTest {

    private static final int IN_OFFSET = 5;
    private final App app = new App();
    private final PricingProtocolEncoding.Encoder encoder = new PricingProtocolEncoding.Encoder();
    private final PricingProtocolEncoding.Decoder decoder = new PricingProtocolEncoding.Decoder();
    private final ExpandableArrayBuffer in = new ExpandableArrayBuffer();
    private final DecodedMessageSpy decodedMessageSpy = new DecodedMessageSpy();

    @Test
    void shouldNotDoAnythingUnprompted() {
        int outputPosition = app.onInput(in, IN_OFFSET, 0);

        assertEquals(0, outputPosition);
        assertEquals(0, app.outputOffset());
        assertEquals(0, app.outputPosition());
    }

    @Test
    void shouldRespondToHeartBeat() {
        long nanoTime = System.nanoTime();
        int inputEndPosition = encoder.wrap(in, IN_OFFSET).encode(heartbeat(nanoTime));

        // When
        int read = app.onInput(in, IN_OFFSET, inputEndPosition);

        // Then
        decoder.wrap(app.output(), app.outputOffset()).decode(decodedMessageSpy);
        assertEquals(1, decodedMessageSpy.messages().size());
        assertEquals(heartbeat(nanoTime), decodedMessageSpy.messages().get(0));

        assertEquals(inputEndPosition, read);
        assertEquals(0, app.outputOffset());
        assertEquals(read - IN_OFFSET, app.outputPosition());
    }
}
