package com.michaelszymczak.sample.tddrefalgo.framework.encoding.pricingprotocol;

import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.ImmutableQuotePricingMessage;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.MutableQuotePricingMessage;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.QuoteEncoding;
import org.agrona.ExpandableArrayBuffer;
import org.agrona.MutableDirectBuffer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuotePricingMessageEncodingTest {

    private final MutableDirectBuffer buffer = new ExpandableArrayBuffer();
    private final QuoteEncoding.Encoder encoder = new QuoteEncoding.Encoder();
    private final QuoteEncoding.Decoder decoder = new QuoteEncoding.Decoder();
    private final MutableQuotePricingMessage mutableQuote = new MutableQuotePricingMessage();


    @Test
    void shouldEncodeQuote() {
        encoder.wrap(buffer, 3)
                .encode(new ImmutableQuotePricingMessage("GB00BD0PCK97", 2, 100_98, 100_95));


        decoder.wrap(buffer, 3)
                .decode(mutableQuote);


        assertEquals(
                new ImmutableQuotePricingMessage("GB00BD0PCK97", 2, 100_98, 100_95),
                new ImmutableQuotePricingMessage(mutableQuote));
    }

    @Test
    void shouldEncodeQuoteWithShortIsin() {
        encoder.wrap(buffer, 3)
                .encode(new ImmutableQuotePricingMessage("isin", 1, 200_98, 200_95));


        decoder.wrap(buffer, 3)
                .decode(mutableQuote);


        assertEquals(
                new ImmutableQuotePricingMessage("isin        ", 1, 200_98, 200_95),
                new ImmutableQuotePricingMessage(mutableQuote));
    }

    @Test
    void shouldWriteMultipleQuotes() {
        // When
        int positionAfterFirstQuote = encoder.wrap(buffer, 3)
                .encode(new ImmutableQuotePricingMessage("GB00BD0PCK91", 1, 100_11, 100_12));
        int positionAfterSecondQuote = encoder.wrap(buffer, positionAfterFirstQuote)
                .encode(new ImmutableQuotePricingMessage("GB00BD0PCK92", 2, 100_21, 100_22));
        encoder.wrap(buffer, positionAfterSecondQuote)
                .encode(new ImmutableQuotePricingMessage("GB00BD0PCK93", 3, 100_31, 100_32));


        // Then
        int positionAfterDecoded = decoder.wrap(buffer, positionAfterFirstQuote).decode(mutableQuote);
        assertEquals(
                new ImmutableQuotePricingMessage("GB00BD0PCK92", 2, 100_21, 100_22),
                new ImmutableQuotePricingMessage(mutableQuote));
        decoder.wrap(buffer, positionAfterSecondQuote).decode(mutableQuote);
        assertEquals(
                new ImmutableQuotePricingMessage("GB00BD0PCK93", 3, 100_31, 100_32),
                new ImmutableQuotePricingMessage(mutableQuote));
        assertEquals(positionAfterSecondQuote, positionAfterDecoded);


    }
}