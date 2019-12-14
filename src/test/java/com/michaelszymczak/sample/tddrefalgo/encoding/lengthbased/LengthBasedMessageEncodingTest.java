package com.michaelszymczak.sample.tddrefalgo.encoding.lengthbased;

import com.michaelszymczak.sample.tddrefalgo.Setup;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.SupportedPayloadSchemas;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.plaintext.MessageWithPlainText;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.time.MessageWithTime;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.time.Time;
import com.michaelszymczak.sample.tddrefalgo.encoding.DecodedAppMessageConsumer;
import com.michaelszymczak.sample.tddrefalgo.encoding.PayloadSchema;
import org.agrona.AsciiSequenceView;
import org.agrona.DirectBuffer;
import org.agrona.ExpandableArrayBuffer;
import org.agrona.MutableDirectBuffer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LengthBasedMessageEncodingTest {

    private final MutableDirectBuffer buffer = new ExpandableArrayBuffer();
    private final LengthBasedMessageEncoding.Encoder encoder = Setup.encoder();
    private final LengthBasedMessageEncoding.Decoder decoder = new LengthBasedMessageEncoding.Decoder();
    private final DecodedMessageSpy messageSpy = new DecodedMessageSpy();

    @Test
    void shouldNotDoAnythingIfToldThatNoData() {
        // Given
        assertEquals(17, encoder.wrap(buffer, 5).encode(new MessageWithPlainText("fooBar")));

        // When
        int decodedLastPosition = decoder.wrap(buffer, 5).decode(0, messageSpy);

        // Then
        assertTrue(messageSpy.decoded.isEmpty());
        assertEquals(5, decodedLastPosition);
    }

    @Test
    void shouldDecodeMessage() {
        // Given
        assertEquals(17, encoder.wrap(buffer, 5).encode(new MessageWithPlainText("fooBar")));

        // When
        int decodedLastPosition = decoder.wrap(buffer, 5).decode(12, messageSpy);

        assertEquals(17, decodedLastPosition);
        assertEquals(1, messageSpy.decoded.size());
        DecodedMessageSpy.Entry decodedEntry = messageSpy.decoded.get(0);
        assertSame(SupportedPayloadSchemas.PLAIN_TEXT, decodedEntry.payloadSchema);
        assertSame(buffer, decodedEntry.buffer);
        assertSame(11, decodedEntry.offset);
        assertSame(6, decodedEntry.length);
        assertEquals("fooBar",
                new AsciiSequenceView(decodedEntry.buffer, decodedEntry.offset, decodedEntry.length).toString());
    }

    @Test
    void shouldDecodeMessageWithoutOffset() {
        // Given
        assertEquals(12, encoder.wrap(buffer, 0).encode(new MessageWithPlainText("fooBar")));

        // When
        int decodedLastPosition = decoder.wrap(buffer, 0).decode(12, messageSpy);

        assertEquals(12, decodedLastPosition);
        assertEquals(1, messageSpy.decoded.size());
        DecodedMessageSpy.Entry decodedEntry = messageSpy.decoded.get(0);
        assertSame(SupportedPayloadSchemas.PLAIN_TEXT, decodedEntry.payloadSchema);
        assertSame(buffer, decodedEntry.buffer);
        assertSame(6, decodedEntry.offset);
        assertSame(6, decodedEntry.length);
        assertEquals("fooBar",
                new AsciiSequenceView(decodedEntry.buffer, decodedEntry.offset, decodedEntry.length).toString());
    }

    @Test
    void shouldDecodeHeartbeatMessage() {
        // Given
        assertEquals(19, encoder.wrap(buffer, 5).encode(new MessageWithTime(new Time(123456L))));

        // When
        int decodedLastPosition = decoder.wrap(buffer, 5).decode(14, messageSpy);

        assertEquals(19, decodedLastPosition);
        assertEquals(1, messageSpy.decoded.size());
        DecodedMessageSpy.Entry decodedEntry = messageSpy.decoded.get(0);
        assertSame(SupportedPayloadSchemas.TIME, decodedEntry.payloadSchema);
        assertSame(buffer, decodedEntry.buffer);
        assertSame(11, decodedEntry.offset);
        assertSame(8, decodedEntry.length);
        assertEquals(123456, buffer.getLong(11));
    }

    @Test
    void shouldNotDoAnythingIfNotEnoughData() {
        // Given
        assertEquals(17, encoder.wrap(buffer, 5).encode(new MessageWithPlainText("fooBar")));

        // When
        int decodedLastPosition = decoder.wrap(buffer, 5).decode(6, messageSpy);

        assertEquals(5, decodedLastPosition);
        assertTrue(messageSpy.decoded.isEmpty());
    }

    private static class DecodedMessageSpy implements DecodedAppMessageConsumer {

        final List<Entry> decoded = new ArrayList<>();

        @Override
        public void onMessage(PayloadSchema payloadSchema, DirectBuffer buffer, int offset, int length) {
            decoded.add(new Entry(payloadSchema, buffer, offset, length));
        }

        static class Entry {
            PayloadSchema payloadSchema;
            DirectBuffer buffer;
            int offset;
            int length;

            Entry(PayloadSchema payloadSchema, DirectBuffer buffer, int offset, int length) {
                this.payloadSchema = payloadSchema;
                this.buffer = buffer;
                this.offset = offset;
                this.length = length;
            }
        }
    }
}