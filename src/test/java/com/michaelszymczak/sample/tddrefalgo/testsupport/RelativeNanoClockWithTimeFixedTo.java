package com.michaelszymczak.sample.tddrefalgo.testsupport;

import com.michaelszymczak.sample.tddrefalgo.supportingdomain.RelativeNanoClock;

public class RelativeNanoClockWithTimeFixedTo implements RelativeNanoClock {

    private final long value;

    public RelativeNanoClockWithTimeFixedTo(final long value) {
        this.value = value;
    }

    @Override
    public long timestampNs() {
        return value;
    }
}
