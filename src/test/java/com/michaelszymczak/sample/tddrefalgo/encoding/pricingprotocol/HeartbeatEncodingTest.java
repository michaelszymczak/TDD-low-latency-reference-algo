package com.michaelszymczak.sample.tddrefalgo.encoding.pricingprotocol;

import com.michaelszymczak.sample.tddrefalgo.apps.pricing.HeartbeatEncoding;
import com.michaelszymczak.sample.tddrefalgo.apps.pricing.ImmutableHeartbeat;
import com.michaelszymczak.sample.tddrefalgo.apps.pricing.MutableHeartbeat;
import org.agrona.ExpandableArrayBuffer;
import org.agrona.MutableDirectBuffer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HeartbeatEncodingTest {

    private final MutableDirectBuffer buffer = new ExpandableArrayBuffer();
    private final HeartbeatEncoding.Encoder encoder = new HeartbeatEncoding.Encoder();
    private final HeartbeatEncoding.Decoder decoder = new HeartbeatEncoding.Decoder();
    private final MutableHeartbeat mutableHeartbeat = new MutableHeartbeat();


    @Test
    void shouldEncodeHeartbeat() {
        encoder.wrap(buffer, 3)
                .encode(new ImmutableHeartbeat(1234567L));


        decoder.wrap(buffer, 3)
                .decode(mutableHeartbeat);


        assertEquals(
                new ImmutableHeartbeat(1234567L),
                new ImmutableHeartbeat(mutableHeartbeat));
    }

    @Test
    void shouldWriteMultipleHeartbeats() {
        int positionAfterFirstHeartbeat = encoder.wrap(buffer, 3)
                .encode(new ImmutableHeartbeat(123L));
        int positionAfterSecondHeartbeat = encoder.wrap(buffer, positionAfterFirstHeartbeat)
                .encode(new ImmutableHeartbeat(456L));
        encoder.wrap(buffer, positionAfterSecondHeartbeat)
                .encode(new ImmutableHeartbeat(789L));


        int positionAfterDecoded = decoder.wrap(buffer, positionAfterFirstHeartbeat).decode(mutableHeartbeat);
        assertEquals(new ImmutableHeartbeat(456L), new ImmutableHeartbeat(mutableHeartbeat));
        decoder.wrap(buffer, positionAfterSecondHeartbeat).decode(mutableHeartbeat);
        assertEquals(new ImmutableHeartbeat(789L), new ImmutableHeartbeat(mutableHeartbeat));
        assertEquals(positionAfterSecondHeartbeat, positionAfterDecoded);


    }
}