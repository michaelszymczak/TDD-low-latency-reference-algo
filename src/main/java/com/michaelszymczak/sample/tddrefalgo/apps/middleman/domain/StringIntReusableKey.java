package com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain;

public class StringIntReusableKey {
    private final StringBuilder keyPlaceholder = new StringBuilder();
    private final String delimiter;

    public StringIntReusableKey(final CharSequence delimiter) {
        this.delimiter = delimiter.toString();
    }

    CharSequence withParts(CharSequence part1, int part2) {
        keyPlaceholder.setLength(0);
        return keyPlaceholder.append(part1).append(delimiter).append(part2);
    }
}
