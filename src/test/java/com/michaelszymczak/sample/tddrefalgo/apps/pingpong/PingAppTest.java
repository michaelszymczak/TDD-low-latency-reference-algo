package com.michaelszymczak.sample.tddrefalgo.apps.pingpong;

import com.michaelszymczak.sample.tddrefalgo.framework.encoding.LengthBasedMessageEncoding;
import com.michaelszymczak.sample.tddrefalgo.framework.encoding.lengthbased.DecodedMessageSpy;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PingAppTest {


    @Test
    void shouldGenerateTextMessage() {
        PingApp pingApp = new PingApp(1024);
        PongApp pongApp = new PongApp();

        // When
        pingApp.ping();
        pingApp.ping();
        pingApp.ping();
        pongApp.onInput(pingApp.output());

        // Then
        assertEquals(Arrays.asList("ping", "ping", "ping"), pongApp.received());
    }

    @Test
    void shouldForwardTheMessage() {
        PingApp pingApp = new PingApp(1024);
        PongApp pongApp = new PongApp();
        PongApp endPongApp = new PongApp();
        pingApp.ping();
        pingApp.ping();
        pongApp.onInput(pingApp.output());

        // When
        endPongApp.onInput(pongApp.output());

        // Then
        assertEquals(Arrays.asList("ping", "ping"), endPongApp.received());
    }

    @Test
    void shouldGenerateHeartbeats() {
        PingApp pingApp = new PingApp(1024 * 1024);
        PongApp pongApp = new PongApp();
        final int heartbeatsToSend = 1_000;

        // When
        for (int i = 0; i < heartbeatsToSend; i++) {
            pingApp.heartbeat();
        }
        pongApp.onInput(pingApp.output());

        // Then
        double usedPerCent = (double) ((long) pingApp.output().length() * 100) / pingApp.output().initialCapacity();
        assertEquals(heartbeatsToSend, pongApp.heartbeatCount());
        assertEquals(2.10, usedPerCent, 0.01);
    }

    @Test
    void shouldResetBufferWhenSingleReader() {
        PingApp pingApp = new PingApp(1024 * 1024);
        PongApp pongApp = new PongApp();
        final int rounds = 200;
        final int heartbeatsToSendInOneRound = 30_000;

        // When
        for (int round = 0; round < rounds; round++) {
            for (int i = 0; i < heartbeatsToSendInOneRound; i++) {
                pingApp.heartbeat();
            }
            long usedPerCent = ((long) pingApp.output().length() * 100) / pingApp.output().initialCapacity();
            assertEquals(62, usedPerCent);
            pongApp.onSingleReaderInput(pingApp.output());
        }

        // Then
        assertEquals(heartbeatsToSendInOneRound * rounds, pongApp.heartbeatCount());
    }

    @Test
    void shouldProduceTheSameOutput() {
        final LengthBasedMessageEncoding.Decoder decoder = new LengthBasedMessageEncoding.Decoder();
        final DecodedMessageSpy output1Spy = new DecodedMessageSpy();
        final DecodedMessageSpy output2Spy = new DecodedMessageSpy();
        PingApp pingApp = new PingApp(1024);
        PongApp pongApp1 = new PongApp();
        PongApp pongApp2 = new PongApp();
        pingApp.ping();
        pongApp1.onInput(pingApp.output());
        pongApp2.onInput(pingApp.output());

        int output1Offset1 = decoder.wrap(pongApp1.output().buffer(), pongApp1.output().offset(), pongApp1.output().length())
                .decodeNext(output1Spy);
        int output2Offset1 = decoder.wrap(pongApp2.output().buffer(), pongApp2.output().offset(), pongApp2.output().length())
                .decodeNext(output2Spy);
        assertEquals(output1Spy.firstEntry(), output2Spy.firstEntry());
        assertEquals(output1Offset1, output2Offset1);
        output1Spy.reset();
        output2Spy.reset();

        int output1Offset2 = decoder.wrap(pongApp1.output().buffer(), output1Offset1, pongApp1.output().length() - output1Offset1)
                .decodeNext(output1Spy);
        int output2Offset2 = decoder.wrap(pongApp1.output().buffer(), output2Offset1, pongApp1.output().length() - output2Offset1)
                .decodeNext(output2Spy);
        assertNull(output1Spy.firstEntry());
        assertEquals(output1Spy.firstEntry(), output2Spy.firstEntry());
        assertEquals(output1Offset2, output2Offset2);
    }
}