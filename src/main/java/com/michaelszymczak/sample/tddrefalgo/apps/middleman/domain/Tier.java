package com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Tier {

    private static final List<Integer> VALUES = Collections.unmodifiableList(Arrays.asList(1, 2, 3, 4, 5));

    public static boolean isValidForQuote(int value) {
        return value >= 1 && value <= 5;
    }

    public static boolean isForCancel(int value) {
        return value == 0;
    }

    static List<Integer> allValues() {
        return VALUES;
    }
}
