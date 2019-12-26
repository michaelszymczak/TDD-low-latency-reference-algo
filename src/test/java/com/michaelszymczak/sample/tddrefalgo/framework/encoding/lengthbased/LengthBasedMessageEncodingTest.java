package com.michaelszymczak.sample.tddrefalgo.framework.encoding.lengthbased;

import com.michaelszymczak.sample.tddrefalgo.framework.api.setup.PayloadSchema;
import com.michaelszymczak.sample.tddrefalgo.framework.encoding.LengthBasedMessageEncoding;
import com.michaelszymczak.sample.tddrefalgo.protocols.plaintext.PlainTextEncoding;
import com.michaelszymczak.sample.tddrefalgo.protocols.time.Time;
import com.michaelszymczak.sample.tddrefalgo.protocols.time.TimeEncoding;
import org.agrona.ExpandableArrayBuffer;
import org.agrona.MutableDirectBuffer;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

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
        int decodedLastPosition = decoder.wrap(buffer, 5, 0).decodeAll(messageSpy);

        // Then
        assertTrue(messageSpy.decoded.isEmpty());
        assertEquals(5, decodedLastPosition);
    }

    @Test
    void shouldDecodeMessage() {
        // Given
        assertEquals(25, encoder.wrap(buffer, 5).updateTime(1234L).encode(
                new PlainTextEncoding.Encoder(new PayloadSchema.KnownPayloadSchema((short) 1)),
                "fooBar"
        ));

        // When
        int decodedLastPosition = decoder.wrap(buffer, 5, 20).decodeAll(messageSpy);

        assertEquals(25, decodedLastPosition);
        assertEquals(1, messageSpy.decoded.size());
        DecodedMessageSpy.Entry decodedEntry = messageSpy.decoded.get(0);
        assertEquals(1234L, decodedEntry.timeNs);
        assertEquals(19, decodedEntry.offset);
        assertEquals(6, decodedEntry.length);
        assertEquals(Arrays.toString("fooBar".getBytes()), Arrays.toString(decodedEntry.data));
    }

    @Test
    void shouldDecodeMessageWithoutOffset() {
        // Given
        assertEquals(20, encoder.wrap(buffer, 0).encode(
                new PlainTextEncoding.Encoder(new PayloadSchema.KnownPayloadSchema((short) 1)),
                "fooBar"
        ));

        // When
        int decodedLastPosition = decoder.wrap(buffer, 0, 20).decodeAll(messageSpy);

        assertEquals(20, decodedLastPosition);
        assertEquals(1, messageSpy.decoded.size());
        DecodedMessageSpy.Entry decodedEntry = messageSpy.decoded.get(0);
        assertSame(14, decodedEntry.offset);
        assertSame(6, decodedEntry.length);
        assertEquals(Arrays.toString("fooBar".getBytes()), Arrays.toString(decodedEntry.data));
    }

    @Test
    void shouldDecodeHeartbeatMessage() {
        // Given
        assertEquals(27, encoder.wrap(buffer, 5).encode(
                new TimeEncoding.Encoder(new PayloadSchema.KnownPayloadSchema((short) 1)),
                new Time(123456L))
        );

        // When
        int decodedLastPosition = decoder.wrap(buffer, 5, 27).decodeAll(messageSpy);

        assertEquals(27, decodedLastPosition);
        assertEquals(1, messageSpy.decoded.size());
        DecodedMessageSpy.Entry decodedEntry = messageSpy.decoded.get(0);
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
        int decodedLastPosition = decoder.wrap(buffer, 5, 6).decodeAll(messageSpy);

        assertEquals(5, decodedLastPosition);
        assertTrue(messageSpy.decoded.isEmpty());
    }

    @Test
    void shouldDecodeAsLongAsMessagesAvailable() {
        // Given
        int encoded1 = encoder.wrap(buffer, 0).encode(TEXT_ENCODER, "fooBar");
        int encoded2 = encoder.wrap(buffer, encoded1).encode(TEXT_ENCODER, "BARBAZZ");

        // When
        int decodedLastPosition = decoder.wrap(buffer, 0, encoded2).decodeAll(messageSpy);

        assertEquals(encoded2, decodedLastPosition);
        assertEquals(2, messageSpy.decoded.size());
        DecodedMessageSpy.Entry decodedEntry1 = messageSpy.decoded.get(0);
        assertSame(14, decodedEntry1.offset);
        assertSame(6, decodedEntry1.length);
        assertEquals(Arrays.toString("fooBar".getBytes()), Arrays.toString(decodedEntry1.data));
        DecodedMessageSpy.Entry decodedEntry2 = messageSpy.decoded.get(1);
        assertSame(34, decodedEntry2.offset);
        assertSame(7, decodedEntry2.length);
        assertEquals(Arrays.toString("BARBAZZ".getBytes()), Arrays.toString(decodedEntry2.data));

    }

}