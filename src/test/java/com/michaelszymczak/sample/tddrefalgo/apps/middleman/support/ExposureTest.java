package com.michaelszymczak.sample.tddrefalgo.apps.middleman.support;

import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.ImmutableQuotePricingMessage;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExposureTest {

    @Test
    void shouldHaveNoExposureInitially() {
        Exposure exposure = new Exposure();
        assertThat(exposure.liveQuotes()).isEmpty();
    }

    @Test
    void shouldCaptureAQuote() {
        Exposure exposure = new Exposure();

        exposure.onPricingMessage(new ImmutableQuotePricingMessage(
                new ImmutableQuotePricingMessage("isin2       ", 2, 5555L, 6666L)));

        assertThat(exposure.liveQuotes()).containsExactly(
                new ImmutableQuotePricingMessage("isin2       ", 2, 5555L, 6666L));
    }

    @Test
    void shouldIgnoreCancelsWhenNoExposure() {
        Exposure exposure = new Exposure();

        exposure.onPricingMessage(new ImmutableQuotePricingMessage(
                new ImmutableQuotePricingMessage("isin1       ", 0, 0L, 0L)));

        assertThat(exposure.liveQuotes()).isEmpty();
    }

    @Test
    void shouldCancelAQuote() {
        Exposure exposure = new Exposure();
        exposure.onPricingMessage(new ImmutableQuotePricingMessage(
                new ImmutableQuotePricingMessage("isin1       ", 1, 1555L, 6666L)));
        exposure.onPricingMessage(new ImmutableQuotePricingMessage(
                new ImmutableQuotePricingMessage("isin2       ", 2, 2555L, 6666L)));
        exposure.onPricingMessage(new ImmutableQuotePricingMessage(
                new ImmutableQuotePricingMessage("isin3       ", 3, 3555L, 6666L)));

        exposure.onPricingMessage(new ImmutableQuotePricingMessage(
                new ImmutableQuotePricingMessage("isin2       ", 0, 0L, 0L)));


        assertThat(exposure.liveQuotes()).containsExactly(
                new ImmutableQuotePricingMessage("isin1       ", 1, 1555L, 6666L),
                new ImmutableQuotePricingMessage("isin3       ", 3, 3555L, 6666L));
    }

    @Test
    void shouldReplaceAQuoteWithTheSameIsinAndTier() {
        Exposure exposure = new Exposure();
        exposure.onPricingMessage(new ImmutableQuotePricingMessage(
                new ImmutableQuotePricingMessage("isin1       ", 1, 1555L, 6666L)));
        exposure.onPricingMessage(new ImmutableQuotePricingMessage(
                new ImmutableQuotePricingMessage("isin2       ", 2, 2555L, 6666L)));
        exposure.onPricingMessage(new ImmutableQuotePricingMessage(
                new ImmutableQuotePricingMessage("isin3       ", 3, 3555L, 6666L)));

        exposure.onPricingMessage(new ImmutableQuotePricingMessage(
                new ImmutableQuotePricingMessage("isin2       ", 4, 5L, 6L)));
        exposure.onPricingMessage(new ImmutableQuotePricingMessage(
                new ImmutableQuotePricingMessage("isin3       ", 3, 4L, 5L)));


        assertThat(exposure.liveQuotes()).containsExactly(
                new ImmutableQuotePricingMessage("isin1       ", 1, 1555L, 6666L),
                new ImmutableQuotePricingMessage("isin2       ", 2, 2555L, 6666L),
                new ImmutableQuotePricingMessage("isin2       ", 4, 5L, 6L),
                new ImmutableQuotePricingMessage("isin3       ", 3, 4L, 5L)
        );
    }

    @Test
    void shouldBeEqualWhenBothWithNoLiveQuotes() {
        assertThat(new Exposure()).isEqualTo(new Exposure());
    }

    @Test
    void shouldBeEqualWhenSameLiveQuotesArrivedInAnyOrder() {
        Exposure exposure1 = new Exposure()
                .onPricingMessage(new ImmutableQuotePricingMessage(
                        new ImmutableQuotePricingMessage("isin2       ", 2, 2555L, 6666L)))
                .onPricingMessage(new ImmutableQuotePricingMessage(
                        new ImmutableQuotePricingMessage("isin1       ", 1, 1555L, 6666L)))
                .onPricingMessage(new ImmutableQuotePricingMessage(
                        new ImmutableQuotePricingMessage("isin3       ", 3, 3555L, 6666L)));
        Exposure exposure2 = new Exposure()
                .onPricingMessage(new ImmutableQuotePricingMessage(
                        new ImmutableQuotePricingMessage("isin1       ", 1, 1555L, 6666L)))
                .onPricingMessage(new ImmutableQuotePricingMessage(
                        new ImmutableQuotePricingMessage("isin3       ", 3, 3555L, 6666L)))
                .onPricingMessage(new ImmutableQuotePricingMessage(
                        new ImmutableQuotePricingMessage("isin2       ", 2, 2555L, 6666L)));

        assertThat(exposure2).isEqualTo(exposure1);
        assertThat(exposure2).isNotEqualTo(new Exposure());
    }
}