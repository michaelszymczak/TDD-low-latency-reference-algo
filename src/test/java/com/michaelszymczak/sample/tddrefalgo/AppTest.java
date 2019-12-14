package com.michaelszymczak.sample.tddrefalgo;

import com.michaelszymczak.sample.tddrefalgo.api.App;
import com.michaelszymczak.sample.tddrefalgo.apps.plaintext.EchoApp;
import com.michaelszymczak.sample.tddrefalgo.apps.samplepricing.SamplePricingApp;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.plaintext.MessageWithPlainText;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.plaintext.PlainTextListener;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol.MessageWithPricingProtocol;
import com.michaelszymczak.sample.tddrefalgo.encoding.lengthbased.LengthBasedMessageEncoding;
import com.michaelszymczak.sample.tddrefalgo.encoding.plaintext.PlainTextEncoding;
import com.michaelszymczak.sample.tddrefalgo.encoding.pricingprotocol.PricingProtocolDecodedMessageSpy;
import com.michaelszymczak.sample.tddrefalgo.encoding.pricingprotocol.PricingProtocolEncoding;
import org.agrona.ExpandableArrayBuffer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol.Heartbeat.heartbeat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AppTest {

    private static final int IN_OFFSET = 55;
    private final App app = new EncodingApp(SamplePricingApp::new, EchoApp::new);
    private final LengthBasedMessageEncoding.Encoder enc = Setup.encoder();
    private final LengthBasedMessageEncoding.Decoder dec = new LengthBasedMessageEncoding.Decoder(Setup.SupportedPayloadSchemas::of);
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
        int inputEndPosition = enc.wrap(in, IN_OFFSET).encode(new MessageWithPricingProtocol().withPayload(heartbeat(nanoTime)));

        // When
        int read = app.onInput(in, IN_OFFSET, inputEndPosition - IN_OFFSET);

        // Then
        dec.wrap(app.output().buffer(), app.output().offset()).decode(app.output().writtenPosition(), (payloadSchema, buffer, offset, length) -> {
            assertEquals(Setup.SupportedPayloadSchemas.PRICING, payloadSchema);
            pricingDecoder.wrap(buffer, offset).decode(pricingDecodedMessageSpy);
        });
        assertEquals(1, pricingDecodedMessageSpy.messages().size());
        assertEquals(heartbeat(nanoTime), pricingDecodedMessageSpy.messages().get(0));

        assertEquals(inputEndPosition, read);
        assertEquals(0, app.output().offset());
        assertEquals(15, app.output().writtenPosition());
    }

    @Test
    void shouldHandlePlainTextMessage() {
        int inputEndPosition = enc.wrap(in, IN_OFFSET).encode(new MessageWithPlainText("foo"));

        // When
        int read = app.onInput(in, IN_OFFSET, inputEndPosition - IN_OFFSET);

        // Then
        dec.wrap(app.output().buffer(), app.output().offset()).decode(app.output().writtenPosition(), (payloadSchema, buffer, offset, length) -> {
            assertEquals(Setup.SupportedPayloadSchemas.PLAIN_TEXT, payloadSchema);
            textDecoder.wrap(buffer, offset, length).decode(textDecodedMessageSpy);
        });
        assertEquals(1, textDecodedMessageSpy.messages().size());
        assertEquals("foo", textDecodedMessageSpy.messages().get(0));

        assertEquals(inputEndPosition, read);
        assertEquals(0, app.output().offset());
        assertEquals(9, app.output().writtenPosition());

    }

    private static class DecodedTextMessageSpy implements PlainTextListener {

        List<String> messages = new ArrayList<>();

        @Override
        public void onPlainTextMessage(String message) {
            messages.add(message);
        }

        List<String> messages() {
            return messages;
        }
    }
}
