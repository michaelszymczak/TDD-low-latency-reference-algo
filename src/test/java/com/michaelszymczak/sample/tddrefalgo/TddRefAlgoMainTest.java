package com.michaelszymczak.sample.tddrefalgo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TddRefAlgoMainTest {

    private final TddRefAlgoMain system = new TddRefAlgoMain(2);

    @Test
    void shouldFoo() {
        String events = "" +
                "Q/   isin1/  1/     4455/   4466\n" +
                "Q/   isin2/  2/     7755/   8866\n" +
                "Q/   isin3/  0/     0/         0\n" +
                "A\n" +
                "Q/   isin4/  0/     0/         0\n" +
                "Q/   isin5/  5/     1234/   5678\n" +
                "A\n";

        assertEquals(
                "" +
                        "[" +
                        "Q/isin1/1/4455/4466, Q/isin2/2/7755/8866, " +
                        "Q/isin3/0/0/0, Q/isin4/0/0/0, Q/isin5/5/1234/5678" +
                        "]",
                system.process(events));
    }
}