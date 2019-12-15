package com.michaelszymczak.sample.tddrefalgo.apps.pingpong;

import org.junit.jupiter.api.Disabled;
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
    @Disabled
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
        double usedPerCent = (double) ((long) pingApp.output().totalLength() * 100) / pingApp.output().capacity();
        assertEquals(heartbeatsToSend, pongApp.heartbeatCount());
        assertEquals(1.33, usedPerCent, 0.01);
    }

    @Test
    void shouldResetBufferWhenSingleReader() {
        PingApp pingApp = new PingApp(1024 * 1024);
        PongApp pongApp = new PongApp();
        final int rounds = 200;
        final int heartbeatsToSendInOneRound = 50_000;

        // When
        for (int round = 0; round < rounds; round++) {
            for (int i = 0; i < heartbeatsToSendInOneRound; i++) {
                pingApp.heartbeat();
            }
            long usedPerCent = ((long) pingApp.output().totalLength() * 100) / pingApp.output().capacity();
            assertEquals(66, usedPerCent);
            pongApp.onSingleReaderInput(pingApp.output());
        }

        // Then
        assertEquals(heartbeatsToSendInOneRound * rounds, pongApp.heartbeatCount());

    }
}