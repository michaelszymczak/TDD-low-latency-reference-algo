package com.michaelszymczak.sample.tddrefalgo.apps.fooapp;

import com.michaelszymczak.sample.tddrefalgo.framework.api.io.AppIO;
import com.michaelszymczak.sample.tddrefalgo.framework.encoding.LengthBasedMessageEncoding;
import com.michaelszymczak.sample.tddrefalgo.framework.encoding.pricingprotocol.PricingProtocolDecodedMessageSpy;
import com.michaelszymczak.sample.tddrefalgo.protocols.plaintext.PlainTextEncoding;
import com.michaelszymczak.sample.tddrefalgo.protocols.plaintext.PlainTextListener;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.PricingProtocolEncoding;
import org.agrona.ExpandableArrayBuffer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.michaelszymczak.sample.tddrefalgo.protocols.pricing.HeartbeatPricingMessage.heartbeat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FooAppTest {

    private static final int IN_OFFSET = 55;
    private final AppIO app = new FooApp();

    private final LengthBasedMessageEncoding.Encoder enc = new LengthBasedMessageEncoding.Encoder();
    private final LengthBasedMessageEncoding.Decoder dec = new LengthBasedMessageEncoding.Decoder();
    private final ExpandableArrayBuffer in = new ExpandableArrayBuffer();

    private final PricingProtocolEncoding.Decoder pricingDecoder = new PricingProtocolEncoding.Decoder();
    private final PricingProtocolDecodedMessageSpy pricingDecodedMessageSpy = new PricingProtocolDecodedMessageSpy();

    private final PlainTextEncoding.Decoder textDecoder = new PlainTextEncoding.Decoder();
    private final DecodedTextMessageSpy textDecodedMessageSpy = new DecodedTextMessageSpy();

    @Test
    void shouldNotDoAnythingUnprompted() {
        int read = app.onInput(in, IN_OFFSET, 0);

        assertEquals(IN_OFFSET, read);
        assertEquals(0, app.output().offset());
        assertEquals(0, app.output().writtenPosition());
    }

    @Test
    void shouldHandlePricingMessage() {
        long nanoTime = System.nanoTime();
        int inputEndPosition = enc.wrap(in, IN_OFFSET).encode(
                new PricingProtocolEncoding.Encoder(FooApp.PRICING), heartbeat(nanoTime));

        // When
        int read = app.onInput(in, IN_OFFSET, inputEndPosition - IN_OFFSET);

        // Then
        dec.wrap(app.output().buffer(), app.output().offset(), app.output().totalLength()).decode(
                (payloadSchemaId, buffer, offset, length) -> {
                    assertEquals(FooApp.PRICING.id(), payloadSchemaId);
                    pricingDecoder.wrap(buffer, offset, length).decode(pricingDecodedMessageSpy);
                });
        assertEquals(1, pricingDecodedMessageSpy.messages().size());
        assertEquals(heartbeat(nanoTime), pricingDecodedMessageSpy.messages().get(0));

        assertEquals(inputEndPosition, read);
        assertEquals(0, app.output().offset());
        assertEquals(23, app.output().writtenPosition());
    }

    @Test
    void shouldHandlePlainTextMessage() {
        int inputEndPosition = enc.wrap(in, IN_OFFSET).encode(
                new PlainTextEncoding.Encoder(FooApp.PLAIN_TEXT),
                "foo");

        // When
        int read = app.onInput(in, IN_OFFSET, inputEndPosition - IN_OFFSET);

        // Then
        dec.wrap(app.output().buffer(), app.output().offset(), app.output().totalLength()).decode(
                (payloadSchemaId, buffer, offset, length) -> {
                    assertEquals(FooApp.PLAIN_TEXT.id(), payloadSchemaId);
                    textDecoder.wrap(buffer, offset, length).decode(textDecodedMessageSpy);
                });
        assertEquals(1, textDecodedMessageSpy.messages().size());
        assertEquals("foo", textDecodedMessageSpy.messages().get(0));

        assertEquals(inputEndPosition, read);
        assertEquals(0, app.output().offset());
        assertEquals(17, app.output().writtenPosition());

    }

    private static class DecodedTextMessageSpy implements PlainTextListener {

        List<String> messages = new ArrayList<>();

        @Override
        public void onMessage(String message) {
            messages.add(message);
        }

        List<String> messages() {
            return messages;
        }
    }
}
