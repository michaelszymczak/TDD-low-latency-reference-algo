package com.michaelszymczak.sample.tddrefalgo;

import com.michaelszymczak.sample.tddrefalgo.domain.messages.PayloadSchema;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol.MessageWithPricingProtocol;
import com.michaelszymczak.sample.tddrefalgo.encoding.MessageEncoding;
import com.michaelszymczak.sample.tddrefalgo.encoding.pricingprotocol.PricingProtocolDecodedMessageSpy;
import com.michaelszymczak.sample.tddrefalgo.encoding.pricingprotocol.PricingProtocolEncoding;
import org.agrona.ExpandableArrayBuffer;
import org.junit.jupiter.api.Test;

import static com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol.Heartbeat.heartbeat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AppTest {

    private static final int IN_OFFSET = 55;
    private final App app = new App();
    private final PricingProtocolEncoding.Encoder encoder = new PricingProtocolEncoding.Encoder();
    private final PricingProtocolEncoding.Decoder decoder = new PricingProtocolEncoding.Decoder();
    private final MessageEncoding.Encoder enc = new MessageEncoding.Encoder();
    private final MessageEncoding.Decoder dec = new MessageEncoding.Decoder();
    private final ExpandableArrayBuffer in = new ExpandableArrayBuffer();
    private final PricingProtocolDecodedMessageSpy decodedMessageSpy = new PricingProtocolDecodedMessageSpy();

    @Test
    void shouldNotDoAnythingUnprompted() {
        int read = app.onInput(in, IN_OFFSET, 0);

        assertEquals(IN_OFFSET, read);
        assertEquals(0, app.outputOffset());
        assertEquals(0, app.outputPosition());
    }

    @Test
    void shouldRespondToHeartBeat() {
        long nanoTime = System.nanoTime();
        int inputEndPosition = enc.wrap(in, IN_OFFSET).encode(new MessageWithPricingProtocol().withPayload(heartbeat(nanoTime)));

        // When
        int read = app.onInput(in, IN_OFFSET, inputEndPosition - IN_OFFSET);

        // Then
        dec.wrap(app.output(), app.outputOffset()).decode(app.outputPosition(), (payloadSchema, buffer, offset, length) -> {
            assertEquals(PayloadSchema.PRICING, payloadSchema);
            decoder.wrap(buffer, offset).decode(decodedMessageSpy);
        });
        assertEquals(1, decodedMessageSpy.messages().size());
        assertEquals(heartbeat(nanoTime), decodedMessageSpy.messages().get(0));

        assertEquals(inputEndPosition, read);
        assertEquals(0, app.outputOffset());
        assertEquals(15, app.outputPosition());
    }
}
