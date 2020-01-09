package com.michaelszymczak.sample.tddrefalgo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TddRefAlgoMainTest {
    @Test
    void shouldFoo() {
        assertEquals(
                "Q/GB00BD0PCK97/2/10098/10095",
                new TddRefAlgoMain().foo());
    }
}