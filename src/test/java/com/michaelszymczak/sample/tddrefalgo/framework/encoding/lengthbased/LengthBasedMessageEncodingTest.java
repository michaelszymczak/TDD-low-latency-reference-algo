package com.michaelszymczak.sample.tddrefalgo.framework.encoding.lengthbased;

import com.michaelszymczak.sample.tddrefalgo.framework.api.setup.PayloadSchema;
import com.michaelszymczak.sample.tddrefalgo.framework.encoding.LengthBasedMessageEncoding;
import com.michaelszymczak.sample.tddrefalgo.protocols.plaintext.PlainTextEncoding;
import com.michaelszymczak.sample.tddrefalgo.protocols.time.Time;
import com.michaelszymczak.sample.tddrefalgo.protocols.time.TimeEncoding;
import org.agrona.AsciiSequenceView;
import org.agrona.ExpandableArrayBuffer;
import org.agrona.MutableDirectBuffer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LengthBasedMessageEncodingTest {

    private static final PlainTextEncoding.Encoder TEXT_ENCODER = new PlainTextEncoding.Encoder(new PayloadSchema.KnownPayloadSchema((short) 1));
    private final MutableDirectBuffer buffer = new ExpandableArrayBuffer();
    private final LengthBasedMessageEncoding.Encoder encoder = new LengthBasedMessageEncoding.Encoder();
    private final LengthBasedMessageEncoding.Decoder decoder = new LengthBasedMessageEncoding.Decoder();
    private final DecodedMessageSpy messageSpy = new DecodedMessageSpy();

    @Test
    void shouldNotDoAnythingIfToldThatNoData() {
        // Given
        assertEquals(25, encoder.wrap(buffer, 5).encode(
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
        assertEquals(25, encoder.wrap(buffer, 5).encode(
                new PlainTextEncoding.Encoder(new PayloadSchema.KnownPayloadSchema((short) 1)),
                "fooBar"
        ));

        // When
        int decodedLastPosition = decoder.wrap(buffer, 5, 25).decode(messageSpy);

        assertEquals(25, decodedLastPosition);
        assertEquals(1, messageSpy.decoded.size());
        DecodedMessageSpy.Entry decodedEntry = messageSpy.decoded.get(0);
        assertSame(buffer, decodedEntry.buffer);
        assertSame(19, decodedEntry.offset);
        assertSame(6, decodedEntry.length);
        assertEquals("fooBar",
                new AsciiSequenceView(decodedEntry.buffer, decodedEntry.offset, decodedEntry.length).toString());
    }

    @Test
    void shouldDecodeMessageWithoutOffset() {
        // Given
        assertEquals(20, encoder.wrap(buffer, 0).encode(
                new PlainTextEncoding.Encoder(new PayloadSchema.KnownPayloadSchema((short) 1)),
                "fooBar"
        ));

        // When
        int decodedLastPosition = decoder.wrap(buffer, 0, 20).decode(messageSpy);

        assertEquals(20, decodedLastPosition);
        assertEquals(1, messageSpy.decoded.size());
        DecodedMessageSpy.Entry decodedEntry = messageSpy.decoded.get(0);
        assertSame(buffer, decodedEntry.buffer);
        assertSame(14, decodedEntry.offset);
        assertSame(6, decodedEntry.length);
        assertEquals("fooBar",
                new AsciiSequenceView(decodedEntry.buffer, decodedEntry.offset, decodedEntry.length).toString());
    }

    @Test
    void shouldDecodeHeartbeatMessage() {
        // Given
        assertEquals(27, encoder.wrap(buffer, 5).encode(
                new TimeEncoding.Encoder(new PayloadSchema.KnownPayloadSchema((short) 1)),
                new Time(123456L))
        );

        // When
        int decodedLastPosition = decoder.wrap(buffer, 5, 27).decode(messageSpy);

        assertEquals(27, decodedLastPosition);
        assertEquals(1, messageSpy.decoded.size());
        DecodedMessageSpy.Entry decodedEntry = messageSpy.decoded.get(0);
        assertSame(buffer, decodedEntry.buffer);
        assertSame(19, decodedEntry.offset);
        assertSame(8, decodedEntry.length);
        assertEquals(123456, buffer.getLong(19));
    }

    @Test
    void shouldNotDoAnythingIfNotEnoughData() {
        // Given
        assertEquals(25, encoder.wrap(buffer, 5).encode(
                new PlainTextEncoding.Encoder(new PayloadSchema.KnownPayloadSchema((short) 1)),
                "fooBar")
        );

        // When
        int decodedLastPosition = decoder.wrap(buffer, 5, 6).decode(messageSpy);

        assertEquals(5, decodedLastPosition);
        assertTrue(messageSpy.decoded.isEmpty());
    }

    @Test
    void shouldDecodeAsLongAsMessagesAvailable() {
        // Given
        int encoded1 = encoder.wrap(buffer, 0).encode(TEXT_ENCODER, "fooBar");
        int encoded2 = encoder.wrap(buffer, encoded1).encode(TEXT_ENCODER, "BARBAZZ");

        // When
        int decodedLastPosition = decoder.wrap(buffer, 0, encoded2).decode(messageSpy);

        assertEquals(encoded2, decodedLastPosition);
        assertEquals(2, messageSpy.decoded.size());
        DecodedMessageSpy.Entry decodedEntry1 = messageSpy.decoded.get(0);
        assertSame(buffer, decodedEntry1.buffer);
        assertSame(14, decodedEntry1.offset);
        assertSame(6, decodedEntry1.length);
        assertEquals("fooBar",
                new AsciiSequenceView(decodedEntry1.buffer, decodedEntry1.offset, decodedEntry1.length).toString());
        DecodedMessageSpy.Entry decodedEntry2 = messageSpy.decoded.get(1);
        assertSame(buffer, decodedEntry2.buffer);
        assertSame(34, decodedEntry2.offset);
        assertSame(7, decodedEntry2.length);
        assertEquals("BARBAZZ",
                new AsciiSequenceView(decodedEntry2.buffer, decodedEntry2.offset, decodedEntry2.length).toString());

    }

}