package com.michaelszymczak.sample.tddrefalgo.apps.pingpong;

import com.michaelszymczak.sample.tddrefalgo.testsupport.SameOutputProperty;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        SameOutputProperty property = new SameOutputProperty();
        PingApp pingApp = new PingApp(1024);
        PongApp pongApp1 = new PongApp();
        PongApp pongApp2 = new PongApp();
        pingApp.ping();
        pongApp1.onInput(pingApp.output());
        pongApp2.onInput(pingApp.output());

        int output1Offset1 = property.verifySameOutputs(
                pongApp1.output().buffer(), pongApp1.output().offset(), pongApp1.output().length(),
                pongApp2.output().buffer(), pongApp2.output().offset(), pongApp2.output().length());
        property.verifySameOutputs(
                pongApp1.output().buffer(), pongApp1.output().offset() + output1Offset1, pongApp1.output().length() - output1Offset1,
                pongApp2.output().buffer(), pongApp2.output().offset() + output1Offset1, pongApp2.output().length() - output1Offset1);
    }
}