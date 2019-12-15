package com.michaelszymczak.sample.tddrefalgo.encoding.lengthbased;

import com.michaelszymczak.sample.tddrefalgo.api.setup.PayloadSchema;
import com.michaelszymczak.sample.tddrefalgo.apps.plaintext.PlainTextEncoding;
import com.michaelszymczak.sample.tddrefalgo.apps.time.Time;
import com.michaelszymczak.sample.tddrefalgo.apps.time.TimeEncoding;
import com.michaelszymczak.sample.tddrefalgo.encoding.DecodedAppMessageConsumer;
import com.michaelszymczak.sample.tddrefalgo.encoding.LengthBasedMessageEncoding;
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
    private final LengthBasedMessageEncoding.Encoder encoder = new LengthBasedMessageEncoding.Encoder();
    private final LengthBasedMessageEncoding.Decoder decoder = new LengthBasedMessageEncoding.Decoder();
    private final DecodedMessageSpy messageSpy = new DecodedMessageSpy();

    @Test
    void shouldNotDoAnythingIfToldThatNoData() {
        // Given
        assertEquals(17, encoder.wrap(buffer, 5).encode(
                new PlainTextEncoding.Encoder(new PayloadSchema.KnownPayloadSchema((short) 1)),
                "fooBar"));

        // When
        int decodedLastPosition = decoder.wrap(buffer, 5, 0).decode(messageSpy);

        // Then
        assertTrue(messageSpy.decoded.isEmpty());
        assertEquals(5, decodedLastPosition);
    }

    @Test
    void shouldDecodeMessage() {
        // Given
        assertEquals(17, encoder.wrap(buffer, 5).encode(
                new PlainTextEncoding.Encoder(new PayloadSchema.KnownPayloadSchema((short) 1)),
                "fooBar"
        ));

        // When
        int decodedLastPosition = decoder.wrap(buffer, 5, 12).decode(messageSpy);

        assertEquals(17, decodedLastPosition);
        assertEquals(1, messageSpy.decoded.size());
        DecodedMessageSpy.Entry decodedEntry = messageSpy.decoded.get(0);
        assertSame(buffer, decodedEntry.buffer);
        assertSame(11, decodedEntry.offset);
        assertSame(6, decodedEntry.length);
        assertEquals("fooBar",
                new AsciiSequenceView(decodedEntry.buffer, decodedEntry.offset, decodedEntry.length).toString());
    }

    @Test
    void shouldDecodeMessageWithoutOffset() {
        // Given
        assertEquals(12, encoder.wrap(buffer, 0).encode(
                new PlainTextEncoding.Encoder(new PayloadSchema.KnownPayloadSchema((short) 1)),
                "fooBar"
        ));

        // When
        int decodedLastPosition = decoder.wrap(buffer, 0, 12).decode(messageSpy);

        assertEquals(12, decodedLastPosition);
        assertEquals(1, messageSpy.decoded.size());
        DecodedMessageSpy.Entry decodedEntry = messageSpy.decoded.get(0);
        assertSame(buffer, decodedEntry.buffer);
        assertSame(6, decodedEntry.offset);
        assertSame(6, decodedEntry.length);
        assertEquals("fooBar",
                new AsciiSequenceView(decodedEntry.buffer, decodedEntry.offset, decodedEntry.length).toString());
    }

    @Test
    void shouldDecodeHeartbeatMessage() {
        // Given
        assertEquals(19, encoder.wrap(buffer, 5).encode(
                new TimeEncoding.Encoder(new PayloadSchema.KnownPayloadSchema((short) 1)),
                new Time(123456L))
        );

        // When
        int decodedLastPosition = decoder.wrap(buffer, 5, 14).decode(messageSpy);

        assertEquals(19, decodedLastPosition);
        assertEquals(1, messageSpy.decoded.size());
        DecodedMessageSpy.Entry decodedEntry = messageSpy.decoded.get(0);
        assertSame(buffer, decodedEntry.buffer);
        assertSame(11, decodedEntry.offset);
        assertSame(8, decodedEntry.length);
        assertEquals(123456, buffer.getLong(11));
    }

    @Test
    void shouldNotDoAnythingIfNotEnoughData() {
        // Given
        assertEquals(17, encoder.wrap(buffer, 5).encode(
                new PlainTextEncoding.Encoder(new PayloadSchema.KnownPayloadSchema((short) 1)),
                "fooBar")
        );

        // When
        int decodedLastPosition = decoder.wrap(buffer, 5, 6).decode(messageSpy);

        assertEquals(5, decodedLastPosition);
        assertTrue(messageSpy.decoded.isEmpty());
    }

    private static class DecodedMessageSpy implements DecodedAppMessageConsumer {

        final List<Entry> decoded = new ArrayList<>();

        @Override
        public void onMessage(short payloadSchemaId, DirectBuffer buffer, int offset, int length) {
            decoded.add(new Entry(payloadSchemaId, buffer, offset, length));
        }

        static class Entry {
            short payloadSchemaId;
            DirectBuffer buffer;
            int offset;
            int length;

            Entry(short payloadSchemaId, DirectBuffer buffer, int offset, int length) {
                this.payloadSchemaId = payloadSchemaId;
                this.buffer = buffer;
                this.offset = offset;
                this.length = length;
            }
        }
    }
}