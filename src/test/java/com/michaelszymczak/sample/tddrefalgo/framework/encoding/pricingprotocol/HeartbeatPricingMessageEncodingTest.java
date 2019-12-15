package com.michaelszymczak.sample.tddrefalgo.framework.encoding.pricingprotocol;

import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.HeartbeatEncoding;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.ImmutableHeartbeatPricingMessage;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.MutableHeartbeatPricingMessage;
import org.agrona.ExpandableArrayBuffer;
import org.agrona.MutableDirectBuffer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HeartbeatPricingMessageEncodingTest {

    private final MutableDirectBuffer buffer = new ExpandableArrayBuffer();
    private final HeartbeatEncoding.Encoder encoder = new HeartbeatEncoding.Encoder();
    private final HeartbeatEncoding.Decoder decoder = new HeartbeatEncoding.Decoder();
    private final MutableHeartbeatPricingMessage mutableHeartbeat = new MutableHeartbeatPricingMessage();


    @Test
    void shouldEncodeHeartbeat() {
        encoder.wrap(buffer, 3)
                .encode(new ImmutableHeartbeatPricingMessage(1234567L));


        decoder.wrap(buffer, 3)
                .decode(mutableHeartbeat);


        assertEquals(
                new ImmutableHeartbeatPricingMessage(1234567L),
                new ImmutableHeartbeatPricingMessage(mutableHeartbeat));
    }

    @Test
    void shouldWriteMultipleHeartbeats() {
        int positionAfterFirstHeartbeat = encoder.wrap(buffer, 3)
                .encode(new ImmutableHeartbeatPricingMessage(123L));
        int positionAfterSecondHeartbeat = encoder.wrap(buffer, positionAfterFirstHeartbeat)
                .encode(new ImmutableHeartbeatPricingMessage(456L));
        encoder.wrap(buffer, positionAfterSecondHeartbeat)
                .encode(new ImmutableHeartbeatPricingMessage(789L));


        int positionAfterDecoded = decoder.wrap(buffer, positionAfterFirstHeartbeat).decode(mutableHeartbeat);
        assertEquals(new ImmutableHeartbeatPricingMessage(456L), new ImmutableHeartbeatPricingMessage(mutableHeartbeat));
        decoder.wrap(buffer, positionAfterSecondHeartbeat).decode(mutableHeartbeat);
        assertEquals(new ImmutableHeartbeatPricingMessage(789L), new ImmutableHeartbeatPricingMessage(mutableHeartbeat));
        assertEquals(positionAfterSecondHeartbeat, positionAfterDecoded);


    }
}