package com.michaelszymczak.sample.tddrefalgo.apps.middleman.domain;

public class Cancel {

    private final String isin;

    public Cancel(CharSequence isin) {
        this.isin = isin.toString();
    }

    public String isin() {
        return isin;
    }
}
