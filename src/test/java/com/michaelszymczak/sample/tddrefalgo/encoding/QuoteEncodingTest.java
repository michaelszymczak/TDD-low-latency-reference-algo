package com.michaelszymczak.sample.tddrefalgo.encoding;

import com.michaelszymczak.sample.tddrefalgo.domain.messages.ImmutableQuote;
import com.michaelszymczak.sample.tddrefalgo.domain.messages.MutableQuote;
import org.agrona.ExpandableArrayBuffer;
import org.agrona.MutableDirectBuffer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuoteEncodingTest {

    private final MutableDirectBuffer buffer = new ExpandableArrayBuffer();
    private final QuoteEncoding.Encoder encoder = new QuoteEncoding.Encoder();
    private final QuoteEncoding.Decoder decoder = new QuoteEncoding.Decoder();
    private final MutableQuote mutableQuote = new MutableQuote();


    @Test
    void shouldEncodeQuote() {
        encoder.wrap(buffer, 3)
                .encode(new ImmutableQuote("GB00BD0PCK97", 2, 100_98, 100_95));


        decoder.wrap(buffer, 3)
                .decode(mutableQuote);


        assertEquals(
                new ImmutableQuote("GB00BD0PCK97", 2, 100_98, 100_95),
                new ImmutableQuote(mutableQuote));
    }

    @Test
    void shouldEncodeQuoteWithShortIsin() {
        encoder.wrap(buffer, 3)
                .encode(new ImmutableQuote("isin", 1, 200_98, 200_95));


        decoder.wrap(buffer, 3)
                .decode(mutableQuote);


        assertEquals(
                new ImmutableQuote("isin        ", 1, 200_98, 200_95),
                new ImmutableQuote(mutableQuote));
    }

    @Test
    void shouldWriteMultipleQuotes() {
        // When
        int positionAfterFirstQuote = encoder.wrap(buffer, 3)
                .encode(new ImmutableQuote("GB00BD0PCK91", 1, 100_11, 100_12));
        int positionAfterSecondQuote = encoder.wrap(buffer, positionAfterFirstQuote)
                .encode(new ImmutableQuote("GB00BD0PCK92", 2, 100_21, 100_22));
        encoder.wrap(buffer, positionAfterSecondQuote)
                .encode(new ImmutableQuote("GB00BD0PCK93", 3, 100_31, 100_32));


        // Then
        int positionAfterDecoded = decoder.wrap(buffer, positionAfterFirstQuote).decode(mutableQuote);
        assertEquals(
                new ImmutableQuote("GB00BD0PCK92", 2, 100_21, 100_22),
                new ImmutableQuote(mutableQuote));
        decoder.wrap(buffer, positionAfterSecondQuote).decode(mutableQuote);
        assertEquals(
                new ImmutableQuote("GB00BD0PCK93", 3, 100_31, 100_32),
                new ImmutableQuote(mutableQuote));
        assertEquals(positionAfterSecondQuote, positionAfterDecoded);


    }
}