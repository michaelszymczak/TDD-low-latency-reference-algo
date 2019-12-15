package com.michaelszymczak.sample.tddrefalgo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TddRefAlgoMainTest {
    @Test
    void shouldFoo() {
        assertEquals(
                "QuotePricingMessage{isin='GB00BD0PCK97', priceTier=2, bidPrice=10098, bidPrice=10095}",
                new TddRefAlgoMain().foo());
    }
}