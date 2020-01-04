package com.michaelszymczak.sample.tddrefalgo.apps.support;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

public class InputPermutations<T> {

    private final List<T> choices;

    @SafeVarargs
    public InputPermutations(T... choices) {
        this(asList(choices));
    }

    public InputPermutations(List<T> choices) {
        this.choices = unmodifiableList(new ArrayList<>(choices));
    }

    public List<List<T>> generate(int slotsCount) {
        final int totalPermutationsCount = slotsCount <= 0 || choices.size() == 0 ? 0 : pow(choices.size(), slotsCount);
        final List<List<T>> results = new ArrayList<>(totalPermutationsCount);
        for (int pi = 0; pi < totalPermutationsCount; pi++) {
            List<T> slots = new ArrayList<>(slotsCount);
            for (int si = slotsCount - 1; si >= 0; si--) {
                int slotToInsertToIndex = slotsCount - si - 1;
                int ci = (pi / pow(choices.size(), si)) % choices.size();
                slots.add(slotToInsertToIndex, choices.get(ci));
            }
            results.add(slots);
        }

        return results;
    }

    private int pow(int base, final int exponent) {
        return BigInteger.valueOf(base).pow(exponent).intValue();
    }
}
