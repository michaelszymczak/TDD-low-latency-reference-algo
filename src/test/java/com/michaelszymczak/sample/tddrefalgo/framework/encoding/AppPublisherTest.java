package com.michaelszymczak.sample.tddrefalgo.framework.encoding;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
    void shouldAdjustItsPositionWhenWrittenTo() {
        AppPublisher publisher = new AppPublisher(100);

        publisher.setWrittenPosition(30);

        assertEquals(30, publisher.writtenPosition());
        assertEquals(0, publisher.offset());
        assertEquals(30, publisher.length());
        assertEquals(100, publisher.initialCapacity());
        assertEquals(70, publisher.remainingCapacity());
    }

    @Test
    void shouldAdjustItsPositionWhenReadFrom() {
        AppPublisher publisher = new AppPublisher(100);
        publisher.setWrittenPosition(30);

        publisher.setReadPosition(10);

        assertEquals(30, publisher.writtenPosition());
        assertEquals(10, publisher.offset());
        assertEquals(20, publisher.length());
        assertEquals(100, publisher.initialCapacity());
        assertEquals(80, publisher.remainingCapacity());
    }

    @Test
    void shouldNotAllowToMarkReadMoreThanWritten() {
        AppPublisher publisher = new AppPublisher(100);
        publisher.setWrittenPosition(30);
        publisher.setReadPosition(5);

        try {
            publisher.setReadPosition(31);
            fail();
        } catch (IllegalStateException e)
        {
            assertEquals(30, publisher.writtenPosition());
            assertEquals(5, publisher.offset());
            assertEquals(25, publisher.length());
            assertEquals(100, publisher.initialCapacity());
            assertEquals(75, publisher.remainingCapacity());
        }
    }
}