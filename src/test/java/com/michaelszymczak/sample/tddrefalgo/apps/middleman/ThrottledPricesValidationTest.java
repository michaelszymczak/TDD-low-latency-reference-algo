package com.michaelszymczak.sample.tddrefalgo.apps.middleman;

import com.michaelszymczak.sample.tddrefalgo.apps.middleman.support.ThrottledPricesPublisherSpy;
import com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain.ThrottledPrices;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ThrottledPricesValidationTest {

    private final ThrottledPricesPublisherSpy publisherSpy = new ThrottledPricesPublisherSpy();

    @Test
    void shouldNotAllowQuotesWithZeroPriceOnBothSides() {
        ThrottledPrices throttledPrices = new ThrottledPrices(publisherSpy, 1);

        // When
        assertThrows(IllegalArgumentException.class,
                () -> throttledPrices.onQuoteUpdate("isin", 1, 0, 0));

        // Then
        publisherSpy.assertPublishedNothing();
    }

    @Test
    void shouldNotAllowQuotesWithNoTier() {
        ThrottledPrices throttledPrices = new ThrottledPrices(publisherSpy, 1);

        // When
        assertThrows(IllegalArgumentException.class,
                () -> throttledPrices.onQuoteUpdate("isin", 0, 1001L, 1002L));

        // Then
        publisherSpy.assertPublishedNothing();
    }

    @Test
    void shouldNotAllowQuotesWithNoIsin() {
        ThrottledPrices throttledPrices = new ThrottledPrices(publisherSpy, 1);

        // When
        assertThrows(IllegalArgumentException.class,
                () -> throttledPrices.onQuoteUpdate("", 1, 101L, 102L));

        // Then
        publisherSpy.assertPublishedNothing();
    }

    @ParameterizedTest
    @CsvSource({"-1", "6"})
    void shouldNotAllowQuotesWithTierOutOfRange(final int tier) {
        ThrottledPrices throttledPrices = new ThrottledPrices(publisherSpy, 1);

        // When
        assertThrows(IllegalArgumentException.class,
                () -> throttledPrices.onQuoteUpdate("isin", tier, 101L, 102L));

        // Then
        publisherSpy.assertPublishedNothing();
    }

    @Test
    void shouldNotAllowCancelsWithNoIsin() {
        ThrottledPrices throttledPrices = new ThrottledPrices(publisherSpy, 1);

        // When
        assertThrows(IllegalArgumentException.class,
                () -> throttledPrices.onCancel(""));

        // Then
        publisherSpy.assertPublishedNothing();
    }
}