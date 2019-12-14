package com.michaelszymczak.sample.tddrefalgo.encoding.pricingprotocol;

import com.michaelszymczak.sample.tddrefalgo.domain.messages.SupportedPayloadSchemas;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol.ImmutableHeartbeat;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.pricingprotocol.ImmutableQuote;
import org.agrona.ExpandableArrayBuffer;
import org.agrona.MutableDirectBuffer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PricingProtocolEncodingTest {

    private final MutableDirectBuffer buffer = new ExpandableArrayBuffer();
    private final PricingProtocolEncoding.Encoder encoder = new PricingProtocolEncoding.Encoder(SupportedPayloadSchemas.PRICING);
    private final PricingProtocolEncoding.Decoder decoder = new PricingProtocolEncoding.Decoder();
    private final PricingProtocolDecodedMessageSpy decodedMessageSpy = new PricingProtocolDecodedMessageSpy();

    @Test
    void shouldEncodeAndDecodeHeartbeat() {
        int positionAfterEncoded = encoder.wrap(buffer, 3).encode(new ImmutableHeartbeat(12345L));

        int positionAfterDecoded = decoder.wrap(buffer, 3).decode(decodedMessageSpy);

        assertEquals(1, decodedMessageSpy.messages().size());
        assertEquals(new ImmutableHeartbeat(12345L), decodedMessageSpy.messages().get(0));
        assertEquals(positionAfterEncoded, positionAfterDecoded);
    }

    @Test
    void shouldEncodeAndDecodeQuote() {
        int positionAfterEncoded = encoder.wrap(buffer, 3).encode(new ImmutableQuote("GB00BD0PCK97", 1, 11, 12));

        int positionAfterDecoded = decoder.wrap(buffer, 3).decode(decodedMessageSpy);

        assertEquals(1, decodedMessageSpy.messages().size());
        assertEquals(new ImmutableQuote("GB00BD0PCK97", 1, 11, 12), decodedMessageSpy.messages().get(0));
        assertEquals(positionAfterEncoded, positionAfterDecoded);
    }

    @Test
    void shouldEncodeAndDecodeMultipleMessages() {
        int positionAfterMessage1 = encoder.wrap(buffer, 5)
                .encode(new ImmutableHeartbeat(12345L));
        int positionAfterMessage2 = encoder.wrap(buffer, positionAfterMessage1)
                .encode(new ImmutableQuote("GB00BD0PCK95", 3, 31, 32));
        encoder.wrap(buffer, positionAfterMessage2)
                .encode(new ImmutableHeartbeat(999L));

        int positionAfterDecodedMessage1 = decoder.wrap(buffer, 5).decode(decodedMessageSpy);
        int positionAfterDecodedMessage2 = decoder.wrap(buffer, positionAfterDecodedMessage1).decode(decodedMessageSpy);
        decoder.wrap(buffer, positionAfterDecodedMessage2).decode(decodedMessageSpy);

        assertEquals(3, decodedMessageSpy.messages().size());
        assertEquals(
                new ImmutableHeartbeat(12345L),
                decodedMessageSpy.messages().get(0));
        assertEquals(
                new ImmutableQuote("GB00BD0PCK95", 3, 31, 32),
                decodedMessageSpy.messages().get(1));
        assertEquals(
                new ImmutableHeartbeat(999L),
                decodedMessageSpy.messages().get(2));
    }

}