package com.michaelszymczak.sample.tddrefalgo.apps.marketmaker.support;

import com.michaelszymczak.sample.tddrefalgo.apps.marketmaker.MarketMakingModule;

@FunctionalInterface
public interface Command {

    void executeAgainst(MarketMakingModule marketMakingModule);
}
