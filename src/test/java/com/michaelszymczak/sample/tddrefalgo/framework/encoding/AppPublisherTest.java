package com.michaelszymczak.sample.tddrefalgo.framework.encoding;

import com.michaelszymczak.sample.tddrefalgo.framework.api.setup.PayloadSchema;
import com.michaelszymczak.sample.tddrefalgo.protocols.plaintext.PlainTextEncoding;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AppPublisherTest {

    @Test
    void shouldSetInitialValues() {
        AppPublisher publisher = new AppPublisher(100);

        assertEquals(0, publisher.writtenPosition());
        assertEquals(0, publisher.offset());
        assertEquals(0, publisher.length());
        assertEquals(100, publisher.initialCapacity());
        assertEquals(100, publisher.remainingCapacity());
    }

    @Test
    void shouldAdjustItsPositionWhenReadFrom() {
        AppPublisher publisher = new AppPublisher(100);
        publisher.publishMessage(textEncoder(), "foo");

        publisher.setReadPosition(10);

        assertEquals(17, publisher.writtenPosition());
        assertEquals(10, publisher.offset());
        assertEquals(7, publisher.length());
        assertEquals(100, publisher.initialCapacity());
        assertEquals(83, publisher.remainingCapacity());
    }

    @Test
    void shouldPublishMessage() {
        AppPublisher publisher = new AppPublisher(100);

        publisher.publishMessage(textEncoder(), "foo");

        assertEquals(17, publisher.writtenPosition());
        assertEquals(0, publisher.offset());
        assertEquals(17, publisher.length());
        assertEquals(100, publisher.initialCapacity());
        assertEquals(83, publisher.remainingCapacity());
    }


    @Test
    void shoulPublishOneMessageAfterAnother() {
        AppPublisher publisher = new AppPublisher(100);
        publisher.publishMessage(textEncoder(), "foo");

        publisher.publishMessage(textEncoder(), "bar");

        assertEquals(34, publisher.writtenPosition());
        assertEquals(0, publisher.offset());
        assertEquals(34, publisher.length());
        assertEquals(100, publisher.initialCapacity());
        assertEquals(66, publisher.remainingCapacity());
    }

    @Test
    void shouldFailToPublishMoreThanPossible() {
        AppPublisher publisher = new AppPublisher(33);
        publisher.publishMessage(textEncoder(), "foo");

        assertThrows(
                IndexOutOfBoundsException.class,
                () -> publisher.publishMessage(textEncoder(), "bar")
        );

        assertEquals(17, publisher.writtenPosition());
        assertEquals(0, publisher.offset());
        assertEquals(17, publisher.length());
        assertEquals(33, publisher.initialCapacity());
        assertEquals(16, publisher.remainingCapacity());
    }


    private PlainTextEncoding.Encoder textEncoder() {
        return new PlainTextEncoding.Encoder(new PayloadSchema.KnownPayloadSchema((short) 1));
    }
}